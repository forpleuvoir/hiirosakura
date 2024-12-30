package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

class CommandSendEvent(
    @JvmField
    var command: String
) : CancellableEvent {
    override var canceled: Boolean = false
}