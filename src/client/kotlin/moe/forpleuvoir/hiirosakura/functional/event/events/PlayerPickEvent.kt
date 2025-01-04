package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个玩家选取行为的事件。
 *
 * 当玩家尝试选取某个目标时触发该事件。可通过 `hitResult` 属性获取选取目标的相关信息，如目标的类型、位置等。
 *
 * 本事件可以被取消，取消后将阻止选取行为的继续。
 *
 * @property hitResult [HSHitResult] 表示选取目标的信息，包括命中的对象、位置和类型等数据。
 */
class PlayerPickEvent(
    @JvmField
    val hitResult: HSHitResult
) : CancellableEvent{
    override var canceled: Boolean = false
}