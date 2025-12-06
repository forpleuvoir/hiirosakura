package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个玩家使用物品的事件。
 *
 * 当玩家尝试使用某个物品时触发该事件。事件携带与使用行为相关的信息：
 * - `hitResult` 表示玩家使用物品时命中的目标信息，可以是方块、实体或其他类型的目标。
 * - `itemStack` 表示玩家尝试使用的物品堆（ItemStack），可通过该属性获取物品的数量、名称、耐久度等信息。
 *
 * 本事件可以被取消，取消后将阻止使用行为的继续。
 *
 * @property hitResult [HSHitResult] 玩家使用行为命中的目标，提供目标的类型和位置信息。
 * @property itemStack [HSItemStack] 玩家尝试使用的物品堆信息，包括物品的详细属性。
 */
class PlayerUseEvent(
    @JvmField
    val hitResult: HSHitResult,
    @JvmField
    val itemStack: HSItemStack,
) : CancellableEvent {
    override var canceled: Boolean = false
}