package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState
import moe.forpleuvoir.nebula.event.CancellableEvent


/**
 * 表示一个玩家尝试破坏方块的事件。
 *
 * 当玩家触发破坏某个方块的行为时，将生成此事件。事件携带相关的方块命中信息：
 * - `blockResult` 表示命中目标方块的详细结果信息，其中包含方块的位置、命中的面以及相关状态等数据。
 *
 * 本事件可以被取消，取消后将阻止方块的破坏行为。
  *
  * @property blockState 方块的状态信息，提供了有关该方块当前属性或特性的详细信息。
  */
 class BreakBlockEvent(
     @JvmField
     val blockState: HSBlockState,
     @JvmField
     val direction: String,
 ) : CancellableEvent {
    override var canceled: Boolean = false
}