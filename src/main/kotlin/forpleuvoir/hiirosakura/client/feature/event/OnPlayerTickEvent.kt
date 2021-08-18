package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event
import net.minecraft.client.network.ClientPlayerEntity
import forpleuvoir.hiirosakura.client.feature.event.eventinterface.IClientPlayerInterface
import forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl.ClientPlayerInterface

/**
 * 玩家tick事件
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnPlayTickEvent
 *
 * #create_time 2021/7/31 23:11
 */
class OnPlayerTickEvent(player: ClientPlayerEntity?) : Event() {
	@JvmField
	val player: IClientPlayerInterface

	init {
		this.player = ClientPlayerInterface(player!!)
	}
}