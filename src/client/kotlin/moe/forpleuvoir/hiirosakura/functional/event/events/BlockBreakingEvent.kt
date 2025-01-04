package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockHitResult
import moe.forpleuvoir.nebula.event.CancellableEvent

class BlockBreakingEvent(
    @JvmField
    val blockResult: HSBlockHitResult
) : CancellableEvent {
    override var canceled: Boolean = false
}