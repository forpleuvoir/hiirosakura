package moe.forpleuvoir.hiirosakura.util

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.ListBuilder
import com.mojang.serialization.MapLike
import com.mojang.serialization.RecordBuilder
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.UnaryOperator
import java.util.stream.Stream
import java.util.stream.StreamSupport

object NebulaOps : DynamicOps<SerializeElement> {

    override fun empty(): SerializeElement = SerializeNull

    override fun <U : Any> convertTo(
        outOps: DynamicOps<U>,
        input: SerializeElement
    ): U {
        if (input is SerializeObject) {
            return convertMap<U?>(outOps, input)
        }
        if (input is SerializeArray) {
            return convertList<U?>(outOps, input)
        }
        if (input is SerializeNull) {
            return outOps.empty()
        }
        val primitive: SerializePrimitive = input.asPrimitive
        if (primitive.isString) {
            return outOps.createString(primitive.asString)
        }
        if (primitive.isBoolean) {
            return outOps.createBoolean(primitive.asBoolean)
        }
        val value = primitive.asBigDecimal
        try {
            val l = value.longValueExact()
            if (l.toByte().toLong() == l) {
                return outOps.createByte(l.toByte())
            }
            if (l.toShort().toLong() == l) {
                return outOps.createShort(l.toShort())
            }
            if (l.toInt().toLong() == l) {
                return outOps.createInt(l.toInt())
            }
            return outOps.createLong(l)
        } catch (e: ArithmeticException) {
            val d = value.toDouble()
            if (d.toFloat().toDouble() == d) {
                return outOps.createFloat(d.toFloat())
            }
            return outOps.createDouble(d)
        }
    }

    override fun getNumberValue(input: SerializeElement): DataResult<Number> {
        if (input is SerializePrimitive && input.isNumber) {
            return DataResult.success(input.asNumber)
        }
        return DataResult.error { "Not a number: $input" }
    }

    override fun createNumeric(i: Number): SerializeElement {
        return SerializePrimitive(i)
    }

    override fun getBooleanValue(input: SerializeElement): DataResult<Boolean> {
        if (input is SerializePrimitive && input.isBoolean) {
            return DataResult.success<Boolean?>(input.asBoolean)
        }
        return DataResult.error { "Not a boolean: $input" }
    }

    override fun createBoolean(value: Boolean): SerializeElement {
        return SerializePrimitive(value)
    }

    override fun getStringValue(input: SerializeElement): DataResult<String> {
        if (input is SerializePrimitive) {
            return DataResult.success(input.asString)
        }
        return DataResult.error { "Not a string: $input" }
    }

    override fun createString(value: String): SerializeElement {
        return SerializePrimitive(value)
    }



    override fun mergeToList(
        list: SerializeElement,
        value: SerializeElement
    ): DataResult<SerializeElement> {
        if (list !is SerializeArray && list != empty()) {
            return DataResult.error({ "mergeToList called with not a list: $list" }, list)
        }
        val result = SerializeArray()
        result.addAll(list.asArray)
        result.add(value)
        return DataResult.success(result)
    }

    override fun mergeToList(list: SerializeElement, values: List<SerializeElement>): DataResult<SerializeElement> {
        if (list !is SerializeArray && list != empty()) {
            return DataResult.error({ "mergeToList called with not a list: $list" }, list)
        }
        val result = SerializeArray()
        result.addAll(list.asArray)
        result.addAll(values)
        return DataResult.success(result)
    }

    override fun mergeToMap(
        map: SerializeElement,
        key: SerializeElement,
        value: SerializeElement
    ): DataResult<SerializeElement> {
        if (map !is SerializeObject && map != empty()) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        }
        if (key !is SerializePrimitive || !key.isString) {
            return DataResult.error({ "key is not a string: $key" }, map)
        }

        val output = SerializeObject()
        if (map.asObject.isNotEmpty()) {
            map.asObject.entries
                .forEach { entry ->
                    output.put(entry.key, entry.value)
                }
        }
        output.put(key.asString, value)
        return DataResult.success(output)
    }

    override fun mergeToMap(map: SerializeElement, values: MapLike<SerializeElement>): DataResult<SerializeElement> {
        if (map !is SerializeObject && map != empty()) {
            return DataResult.error({ "mergeToMap called with not a map: $map" }, map)
        }
        val output = SerializeObject()
        if (map !== empty()) {
            map.asObject.entries
                .forEach { entry ->
                    output.put(entry.key, entry.value)
                }
        }
        val missed = mutableListOf<SerializeElement>()

        values.entries().forEach { entry ->
            val key = entry.getFirst()
            if (key !is SerializePrimitive || !key.isString) {
                missed.add(key)
                return@forEach
            }
            output.put(key.asString, entry.getSecond())
        }

        if (!missed.isEmpty()) {
            return DataResult.error({ "some keys are not strings: $missed" }, output)
        }

        return DataResult.success(output)
    }

    override fun getMapValues(input: SerializeElement): DataResult<Stream<Pair<SerializeElement, SerializeElement?>>> {
        if (input !is SerializeObject) {
            return DataResult.error { "Not a SerializeObject: $input" }
        }
        return DataResult.success(
            input.asObject.entries.stream().map<Pair<SerializeElement, SerializeElement?>> { entry ->
                Pair.of<SerializeElement, SerializeElement?>(
                    SerializePrimitive(entry.key),
                    if (entry.value is SerializeNull) null else entry.value
                )
            })

    }

    override fun getMapEntries(input: SerializeElement): DataResult<Consumer<BiConsumer<SerializeElement, SerializeElement?>>> {
        if (input !is SerializeObject) {
            return DataResult.error { "Not a SerializeObject: $input" }
        }
        return DataResult.success(Consumer { c: BiConsumer<SerializeElement, SerializeElement?> ->
            for (entry in input.asObject.entries) {
                c.accept(createString(entry.key), if (entry.value is SerializeNull) null else entry.value)
            }
        })
    }

    override fun getMap(input: SerializeElement): DataResult<MapLike<SerializeElement>> {
        if (input !is SerializeObject) {
            return DataResult.error { "Not a SerializeObject: $input" }
        }
        val obj = input.asObject
        return DataResult.success<MapLike<SerializeElement>>(object : MapLike<SerializeElement> {
            override fun get(key: SerializeElement): SerializeElement? {
                val element = obj[key.asString]
                if (element is SerializeNull) {
                    return null
                }
                return element
            }

            override fun get(key: String): SerializeElement? {
                val element = obj[key]
                if (element is SerializeNull) {
                    return null
                }
                return element
            }

            override fun entries(): Stream<Pair<SerializeElement, SerializeElement?>> {
                return obj.entries.stream().map<Pair<SerializeElement, SerializeElement?>> { e ->
                    Pair.of<SerializeElement, SerializeElement?>(
                        SerializePrimitive(e.key),
                        e.value
                    )
                }
            }

            override fun toString(): String {
                return "MapLike[$obj]"
            }
        })
    }

    override fun createMap(map: Stream<Pair<SerializeElement, SerializeElement?>>): SerializeElement {
        val result = SerializeObject()
        map.forEach { p ->
            result.put(p.getFirst().asString, p.getSecond() ?: SerializeNull)
        }
        return result
    }

    override fun getStream(input: SerializeElement): DataResult<Stream<SerializeElement>> {
        if (input is SerializeArray) {
            return DataResult.success<Stream<SerializeElement>>(
                StreamSupport.stream<SerializeElement>(input.asArray.spliterator(), false)
                    .map<SerializeElement?> { e -> if (e is SerializeNull) null else e })
        }
        return DataResult.error { "Not a SerializeArray: $input" }
    }

    override fun getList(input: SerializeElement): DataResult<Consumer<Consumer<SerializeElement?>>> {
        if (input is SerializeArray) {
            return DataResult.success<Consumer<Consumer<SerializeElement?>>>(Consumer { c: Consumer<SerializeElement?> ->
                for (element in input.asArray) {
                    c.accept(if (element is SerializeNull) null else element)
                }
            })
        }
        return DataResult.error { "Not a SerializeArray: $input" }
    }

    override fun createList(input: Stream<SerializeElement>): SerializeElement {
        return serializeArray {
            input.forEach { e -> add(e) }
        }
    }

    override fun remove(
        input: SerializeElement,
        key: String
    ): SerializeElement {
        if (input is SerializeObject) {
            val result = SerializeObject()
            result.remove(key)
            return result
        }
        return input
    }

    override fun toString(): String {
        return "Nebula.Serialization"
    }

    override fun listBuilder(): ListBuilder<SerializeElement> {
        return ArrayBuilder()
    }

    private class ArrayBuilder : ListBuilder<SerializeElement> {
        private var builder: DataResult<SerializeArray> = DataResult.success(SerializeArray(), Lifecycle.stable())
        override fun ops(): DynamicOps<SerializeElement> {
            return NebulaOps
        }

        override fun build(prefix: SerializeElement): DataResult<SerializeElement> {
            val result = builder.flatMap { b ->
                if (prefix !is SerializeArray && prefix != ops().empty()) {
                    return@flatMap DataResult<SerializeElement>.error({ "Cannot append a list to not a list: $prefix" }, prefix)
                }
                val array = SerializeArray()
                if (b.asArray.isNotEmpty()) {
                    b.asArray.forEach { e -> array.add(e) }
                }
                array.addAll(b)
                array.add(prefix)
                DataResult.success(array, Lifecycle.stable())
            }
            return result
        }

        override fun add(value: SerializeElement): ListBuilder<SerializeElement> {
            builder = builder.map {
                it.add(value)
                return@map it
            }
            return this
        }

        override fun add(value: DataResult<SerializeElement>): ListBuilder<SerializeElement> {
            builder = builder.apply2stable({ b, element ->
                b.add(element)
                b
            }, value)
            return this
        }

        override fun withErrorsFrom(result: DataResult<*>): ListBuilder<SerializeElement> {
            builder = builder.flatMap { r ->
                result.map { v -> r }
            }
            return this
        }

        override fun mapError(onError: UnaryOperator<String>): ListBuilder<SerializeElement> {
            builder = builder.mapError(onError)
            return this
        }
    }

    override fun mapBuilder(): RecordBuilder<SerializeElement> {
        return NebulaRecordBuilder()
    }

    private class NebulaRecordBuilder : RecordBuilder.AbstractStringBuilder<SerializeElement, SerializeObject>(NebulaOps) {

        override fun append(
            key: String,
            value: SerializeElement,
            builder: SerializeObject
        ): SerializeObject {
            builder.put(key, value)
            return builder
        }

        override fun initBuilder(): SerializeObject {
            return SerializeObject()
        }

        override fun build(
            builder: SerializeObject,
            prefix: SerializeElement?
        ): DataResult<SerializeElement> {
            if (prefix == null || prefix.isNull) {
                return DataResult.success(builder, Lifecycle.stable())
            }
            if (prefix is SerializeObject) {
                val result = SerializeObject()
                result.putAll(prefix)
                result.putAll(builder)
                return DataResult.success(result)
            }
            return DataResult.error({ "mergeToMap called with not a map: $prefix" }, prefix)
        }

    }

}