package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockHitResult
import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个触发方块破坏行为的事件。
 *
 * 当玩家尝试破坏某个方块时触发该事件。事件携带关于被击中方块的相关信息，可通过 `blockResult` 属性获取。
 *
 * 本事件可以被取消，取消后将阻止方块被破坏。
 *
 * @property blockResult [HSBlockHitResult] 表示被击中方块的具体信息，包含方块位置、面、类型等数据。
 */
class BlockBreakingEvent(
    @JvmField
    val blockResult: HSBlockHitResult
) : CancellableEvent {
    override var canceled: Boolean = false
}