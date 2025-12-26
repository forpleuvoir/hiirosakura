package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.nebula.serialization.base.*
import moe.forpleuvoir.nebula.serialization.extensions.toList
import moe.forpleuvoir.nebula.serialization.extensions.toMap
import moe.forpleuvoir.nebula.serialization.extensions.toObj

fun SerializeElement.toJavaObject(): Any? {
    return when (this) {
        is SerializeArray     -> this.toList()
        is SerializeObject    -> this.toMap()
        is SerializePrimitive -> this.toObj()
        SerializeNull         -> null
    }
}