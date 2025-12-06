package moe.forpleuvoir.hiirosakura.functional.executor

import moe.forpleuvoir.nebula.serialization.Serializable

interface Executor : Serializable {

    fun execute()

}