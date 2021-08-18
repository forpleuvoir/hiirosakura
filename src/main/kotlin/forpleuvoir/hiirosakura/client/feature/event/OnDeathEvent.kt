package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event

/**
 * 客户端玩家死亡事件
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnDeathEvent
 *
 * #create_time 2021/7/31 22:59
 */
class OnDeathEvent(
	@JvmField val showsDeathScreen: Boolean,
	@JvmField val damageSource: String?,
	@JvmField val attacker: String?
) : Event()