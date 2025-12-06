package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个服务器加入的事件。
 *
 * 当玩家成功连接到服务器时触发此事件，事件中包含有关服务器的信息：
 *
 * 此事件可用于监控玩家进入服务器的行为或记录相关数据。
 *
 * @property serverName 表示加入的服务器名称。
 * @property serverAddress 表示加入的服务器地址。
 */
class ServerJoinEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String
) : Event