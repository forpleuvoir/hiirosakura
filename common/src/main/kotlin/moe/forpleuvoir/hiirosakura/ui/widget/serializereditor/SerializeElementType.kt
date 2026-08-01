package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive

enum class SerializeElementType(val displayName: String) {
    Object("Object"),
    Array("Array"),
    String("String"),
    Boolean("Boolean"),
    Int("Int"),
    Long("Long"),
    Float("Float"),
    Double("Double"),
    Byte("Byte"),
    Short("Short"),
    Null("Null");

    val defaultValue: SerializeElement
        get() = when (this) {
            Object  -> SerializeObject()
            Array   -> SerializeArray()
            String  -> SerializePrimitive("")
            Boolean -> SerializePrimitive(false)
            Int     -> SerializePrimitive(0)
            Long    -> SerializePrimitive(0L)
            Float   -> SerializePrimitive(0f)
            Double  -> SerializePrimitive(0.0)
            Byte    -> SerializePrimitive(0.toByte())
            Short   -> SerializePrimitive(0.toShort())
            Null    -> SerializeNull
        }
}

/**
 * 当前数据是否已属于该类型：对象 / 数组 / 原始类型都按实际内容判断。
 * 用于根类型切换时避免把已有的对象 / 数组内容清空。
 */
internal fun SerializeElement.matchesType(type: SerializeElementType): Boolean = when (this) {
    is SerializeObject -> type == SerializeElementType.Object
    is SerializeArray -> type == SerializeElementType.Array
    is SerializeNull -> type == SerializeElementType.Null
    is SerializePrimitive -> when (type) {
        SerializeElementType.Object, SerializeElementType.Array, SerializeElementType.Null -> false
        SerializeElementType.String -> isString
        SerializeElementType.Boolean -> isBoolean
        SerializeElementType.Int -> asNumber is Int
        SerializeElementType.Long -> asNumber is Long
        SerializeElementType.Float -> asNumber is Float
        SerializeElementType.Double -> asNumber is Double
        SerializeElementType.Byte -> asNumber is Byte
        SerializeElementType.Short -> asNumber is Short
    }
}
