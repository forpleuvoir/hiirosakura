package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个退出游戏的事件。
 *
 * 当玩家从服务器退出游戏时触发此事件，包含退出时的相关信息：
 * - `serverName` 表示玩家所在的服务器名称。
 * - `serverAddress` 表示玩家所在的服务器地址。
 *
 * 此事件可用于监控玩家退出游戏的行为或记录退出时的相关数据。
 */
data class GameExitEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
) : Event
