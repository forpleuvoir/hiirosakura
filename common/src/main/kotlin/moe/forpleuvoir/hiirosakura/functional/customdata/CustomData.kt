package moe.forpleuvoir.hiirosakura.functional.customdata

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive

object CustomData : HiiroSakuraData {

    override val key: String = "custom_data"

    val data: LinkedHashMap<String, Any?> = LinkedHashMap()

    override fun serialization(): SerializeElement {
        val obj = SerializeObject()
        data.forEach { (key, value) ->
            obj[key] = serializeValue(value)
        }
        return obj
    }

    override fun deserialization(data: SerializeElement) {
        this.data.clear()
        if (data is SerializeObject) {
            data.forEach { (key, value) ->
                this.data[key] = deserializeValue(value)
            }
        }
    }

    /**
     * 将任意 Kotlin 值递归转换为 [SerializeElement]。
     *
     * 支持的类型：
     * - null                     → [SerializeNull]
     * - [String]                 → [SerializePrimitive]
     * - [Boolean]                → [SerializePrimitive]
     * - [Number]                 → [SerializePrimitive]
     * - [Map]<*, *>              → [SerializeObject]（递归转换值，非 String 的 key 调用 toString）
     * - [Iterable]<*>            → [SerializeArray]（递归转换元素）
     * - [Array]<*>               → [SerializeArray]（递归转换元素）
     * - 其他类型                  → [SerializePrimitive(value.toString())]（兜底）
     */
    private fun serializeValue(value: Any?): SerializeElement = when (value) {
        null -> SerializeNull
        is String -> SerializePrimitive(value)
        is Boolean -> SerializePrimitive(value)
        is Number -> SerializePrimitive(value)
        is Map<*, *> -> SerializeObject().also { map ->
            value.forEach { (k, v) ->
                if (k != null) map[k.toString()] = serializeValue(v)
            }
        }
        is Iterable<*> -> SerializeArray().also { arr ->
            value.forEach { arr.addLast(serializeValue(it)) }
        }
        is Array<*> -> SerializeArray().also { arr ->
            value.forEach { arr.addLast(serializeValue(it)) }
        }
        else -> SerializePrimitive(value.toString())
    }

    /**
     * 将 [SerializeElement] 递归反序列化为对应的 Kotlin 值。
     *
     * 转换规则：
     * - [SerializeNull]             → null
     * - [SerializePrimitive]        → [String] / [Boolean] / [Number]（按类型判断）
     * - [SerializeObject]           → [LinkedHashMap]<[String], [Any]?>（递归转换值）
     * - [SerializeArray]            → [List]<[Any]?>（递归转换元素）
     */
    private fun deserializeValue(element: SerializeElement): Any? = when (element) {
        is SerializeNull -> null
        is SerializePrimitive -> when {
            element.isString -> element.asString
            element.isBoolean -> element.asBoolean
            element.isNumber -> element.asNumber
            else -> element.asString
        }
        is SerializeObject -> buildMap<String, Any?> {
            element.forEach { (key, value) ->
                put(key, deserializeValue(value))
            }
        }
        is SerializeArray -> element.map { deserializeValue(it) }
    }

}