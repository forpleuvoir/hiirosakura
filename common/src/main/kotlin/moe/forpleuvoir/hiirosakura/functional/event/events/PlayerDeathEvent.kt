package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个玩家死亡事件。
 *
 * 当玩家角色死亡时触发此事件。事件携带一个死亡消息，用于描述死亡的相关信息。
 *
 * 此事件可用于监控玩家的死亡行为，记录死亡信息，或者触发其他相关操作。
 *
 * @property message 玩家死亡时的消息内容，通常用于描述死亡原因或情况。
 *
 */
class PlayerDeathEvent(
    @JvmField
    val message: String,
) : Event {

    companion object {
        @JvmStatic
        var isDead = false
    }

}