package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.nebula.event.CancellableEvent

class PlayerPickEvent(
    @JvmField
    val hitResult: HSHitResult
) : CancellableEvent{
    override var canceled: Boolean = false
}