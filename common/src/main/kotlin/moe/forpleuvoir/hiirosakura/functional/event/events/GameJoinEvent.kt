package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个加入游戏的事件。
 *
 * 当玩家成功进入游戏服务器时触发此事件,在群组服中切换子服也会触发，包含有关服务器的相关信息：
 * - `serverName` 表示玩家加入的服务器名称。
 * - `serverAddress` 表示玩家加入的服务器地址。
 *
 * 此事件可用于监控玩家进入游戏的行为或记录相关数据。
 */
data class GameJoinEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
) : Event