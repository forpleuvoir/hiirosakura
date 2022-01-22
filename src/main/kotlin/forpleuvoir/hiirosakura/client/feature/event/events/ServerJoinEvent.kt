package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.Event

/**
 * 服务器加入事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 ServerJoinEvent

 * 创建时间 2022/1/21 12:40

 * @author forpleuvoir

 */
class ServerJoinEvent(@JvmField val serverName: String?, @JvmField val serverAddress: String?) : Event {
}