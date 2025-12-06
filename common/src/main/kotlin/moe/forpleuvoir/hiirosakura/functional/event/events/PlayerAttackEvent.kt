package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个玩家攻击行为的事件。
 *
 * 当玩家进行攻击操作时触发该事件，可通过 `hitResult` 获取到关于攻击目标的相关信息。
 *
 * 本事件可以被取消，取消后将阻止攻击行为的继续。
 *
 * @property hitResult [HSHitResult] 表示攻击目标的信息，包括命中的对象、位置和类型等数据。
 */
class PlayerAttackEvent(
    @JvmField
    val hitResult: HSHitResult
) : CancellableEvent {
    override var canceled: Boolean = false
}

