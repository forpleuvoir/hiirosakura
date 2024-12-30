package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

class MessageSendEvent(
    @JvmField
    var message: String
) : CancellableEvent {
    override var canceled: Boolean = false
}