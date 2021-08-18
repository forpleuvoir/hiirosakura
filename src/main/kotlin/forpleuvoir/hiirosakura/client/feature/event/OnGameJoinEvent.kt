package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event

/**
 * 游戏加入事件
 * 本地游戏加入时不会有名称和地址
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnGameJoinEvent
 *
 * #create_time 2021-07-23 13:42
 */
class OnGameJoinEvent(
	/**
	 * 服务器名称
	 */
	@JvmField
	val name: String?,
	/**
	 * 服务器地址
	 */
	@JvmField
	val address: String?
) : Event()