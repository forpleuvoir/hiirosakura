package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个玩家重生事件。
 *
 * 当玩家完成角色重生流程时触发此事件。该事件通常用于监控玩家的重生行为或执行与玩家重生相关的操作。
 *
 */
object PlayerRespawnEvent : Event