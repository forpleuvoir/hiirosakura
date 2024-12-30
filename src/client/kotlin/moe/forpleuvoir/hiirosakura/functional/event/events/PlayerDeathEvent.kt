package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

class PlayerDeathEvent(
    @JvmField
    val message: String,
) : Event {

    companion object {
        @JvmStatic
        var isDead = false
    }

}