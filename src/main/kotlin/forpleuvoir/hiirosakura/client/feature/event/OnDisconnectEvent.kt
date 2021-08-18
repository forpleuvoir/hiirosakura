package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event

/**
 * 从服务器断开连接事件 包括主动退出服务器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnDisconnectEvent
 *
 * #create_time 2021-07-26 16:08
 */
class OnDisconnectEvent(
	@JvmField
	val name: String?,
	@JvmField
	val address: String?
) : Event()