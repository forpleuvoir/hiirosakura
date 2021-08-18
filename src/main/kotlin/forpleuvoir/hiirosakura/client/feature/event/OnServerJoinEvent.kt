package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event

/**
 * 加入服务器事件
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnServerJoinEvent
 *
 * #create_time 2021-07-26 13:43
 */
class OnServerJoinEvent(
	@JvmField
	val name: String,
	@JvmField
	val address: String
) : Event()