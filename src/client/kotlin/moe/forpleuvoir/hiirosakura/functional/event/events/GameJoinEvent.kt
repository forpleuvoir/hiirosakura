package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

data class GameJoinEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
) : Event