package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.nebula.event.CancellableEvent

class PlayerUseEvent(
    @JvmField
    val hitResult: HSHitResult,
    @JvmField
    val itemStack: HSItemStack,
) : CancellableEvent {
    override var canceled: Boolean = false
}