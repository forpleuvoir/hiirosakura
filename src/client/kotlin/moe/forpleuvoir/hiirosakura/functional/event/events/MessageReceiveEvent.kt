package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

class MessageReceiveEvent(
    @JvmField
    val message: String
) : CancellableEvent {
    override var canceled: Boolean = false
}