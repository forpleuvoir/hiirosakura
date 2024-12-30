package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

data class GameExitEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
) : Event
