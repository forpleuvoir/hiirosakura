package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil

/**
 * 当从服务器断开连接
 * 不包括主动退出服务器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnDisconnectedEvent
 *
 * #create_time 2021/7/25 21:31
 */
class OnDisconnectedEvent(
	@JvmField
	val name: String?,
	@JvmField
	val address: String?,
	@JvmField
	val title: String?,
	@JvmField
	val reason: String?
) : Event() {
	init {
		ServerInfoUtil.disconnect()
	}
}