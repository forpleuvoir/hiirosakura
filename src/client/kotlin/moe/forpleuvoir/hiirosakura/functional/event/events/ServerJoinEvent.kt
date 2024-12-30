package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

class ServerJoinEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String
) : Event