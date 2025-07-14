package moe.forpleuvoir.hiirosakura.util

import com.mojang.serialization.Codec
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.NebulaOps
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.util.Identifier

internal fun Any.logger(): ModLogger = ModLogger(this::class, HiiroSakura.MOD_NAME)

internal fun identifier(path: String): Identifier = identifier(HiiroSakura.MOD_ID, path)


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

fun <T> Codec<T>.serialization(obj: T): SerializeElement {
    return this.encodeStart(NebulaOps, obj).result().get()
}

fun <T> Codec<T>.deserialization(serializeElement: SerializeElement): T {
    return this.decode(NebulaOps, serializeElement).result().get().first
}