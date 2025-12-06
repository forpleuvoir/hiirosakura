package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.Event

/**
 * 表示一个断开连接事件。
 *
 * 当与服务器断开连接时触发此事件，包含断开连接相关的信息：
 * - `serverName` 表示断开连接的服务器名称。
 * - `serverAddress` 表示断开连接的服务器地址。
 * - `title` 表示显示给用户的断开连接标题。
 * - `reason` 表示断开连接的具体原因。
 *
 * 此事件可用于监控或记录断开连接时的相关数据。
 */
data class DisconnectEvent(
    @JvmField
    val serverName: String,
    @JvmField
    val serverAddress: String,
    @JvmField
    val title: String,
    @JvmField
    val reason: String,
) : Event