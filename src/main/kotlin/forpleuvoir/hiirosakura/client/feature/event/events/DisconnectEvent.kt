package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.Event

/**
 * 掉线事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 DisconnectEvent

 * 创建时间 2022/1/21 14:06

 * @author forpleuvoir

 */
class DisconnectEvent(
	@JvmField
	val serverName: String?,
	@JvmField
	val serverAddress: String?,
	@JvmField
	val title: String?,
	@JvmField
	val reason: String?
) : Event {
}