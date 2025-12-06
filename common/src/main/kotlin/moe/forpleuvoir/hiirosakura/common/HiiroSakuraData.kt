package moe.forpleuvoir.hiirosakura.common

import moe.forpleuvoir.nebula.serialization.Deserializable
import moe.forpleuvoir.nebula.serialization.Serializable

interface HiiroSakuraData : Serializable, Deserializable {

    val key: String

}