package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

data class DisconnectEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
    @JvmField
    val title: String,
    @JvmField
    val reason: String,
) : Event