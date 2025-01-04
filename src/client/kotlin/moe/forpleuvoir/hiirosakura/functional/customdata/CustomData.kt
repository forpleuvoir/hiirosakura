package moe.forpleuvoir.hiirosakura.functional.customdata

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import moe.forpleuvoir.nebula.serialization.extensions.toMap

object CustomData : HiiroSakuraData {

    override val key: String
        get() = "custom_data"

    val data: LinkedHashMap<String, Any?> = LinkedHashMap()


    override fun serialization(): SerializeElement = serializeObject(data)

    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalApi::class)
    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.checkType<SerializeObject, Unit> {
            data.clear()
            data.putAll(it.toMap())
        }
    }

}