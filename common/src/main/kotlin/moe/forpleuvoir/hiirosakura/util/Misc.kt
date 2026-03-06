package moe.forpleuvoir.hiirosakura.util

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass

internal fun logger(name: String): ModLogger = ModLogger(name, HiiroSakura.MOD_NAME)

internal fun logger(clazz: KClass<*>): ModLogger = ModLogger(clazz, HiiroSakura.MOD_NAME)


internal fun Any.logger(): ModLogger = ModLogger(this::class, HiiroSakura.MOD_NAME)

internal fun identifier(path: String): ResourceLocation = identifier(HiiroSakura.MOD_ID, path)

fun <T : Comparable<T>> ClosedRange<T>.serialization(): SerializeElement {
    return SerializePrimitive("${this.start}..${this.endInclusive}")
}

fun <T : Comparable<T>> deserialization(serializeElement: SerializeElement, supplier: (String) -> T): ClosedRange<T> {
    serializeElement as SerializePrimitive
    serializeElement.asString.let {
        val pair = it.split("..")
        return supplier(pair[0])..supplier(pair[1])
    }
}

inline fun <reified T : Enum<T>> T.cycle(): T {
    val values = enumValues<T>()
    val nextIndex: Int = values.indexOf(this).let {
        if (it == values.lastIndex) 0 else it + 1
    }
    return values[nextIndex]
}

operator fun <A, B> Pair<A, B>.component1(): A? = this.first
operator fun <A, B> Pair<A, B>.component2(): B? = this.second

fun <T> Codec<T>.serialization(obj: T, dynamicOps: DynamicOps<SerializeElement> = NebulaOps): SerializeElement {
    return this.encodeStart(dynamicOps, obj).orThrow
}

fun <T> Codec<T>.deserialization(serializeElement: SerializeElement, dynamicOps: DynamicOps<SerializeElement> = NebulaOps): T {
    return this.decode(dynamicOps, serializeElement).orThrow.first
}

