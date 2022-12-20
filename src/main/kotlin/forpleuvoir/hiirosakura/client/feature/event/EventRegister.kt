package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.event.events.*
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.common.mText
import forpleuvoir.ibuki_gourd.event.Event
import forpleuvoir.ibuki_gourd.event.events.Events
import net.minecraft.text.MutableText

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event

 * 文件名 EventRegister

 * 创建时间 2022/1/21 12:24

 * @author forpleuvoir

 */
object EventRegister : IModInitialize {

	override fun initialize() {
		register(AttackEvent::class.java)
		register(ItemPickEvent::class.java)
		register(ItemUseEvent::class.java)
		register(ServerJoinEvent::class.java)
		register(GameJoinEvent::class.java)
		register(GameExitEvent::class.java)
		register(DisconnectEvent::class.java)
		register(MessageEvent::class.java)
        register(MessageSendEvent::class.java)
        register(PlayerTickEvent::class.java)
        register(PlayerDeathEvent::class.java)
        register(PlayerRespawnEvent::class.java)

    }

    private fun <E : Event> register(event: Class<out E>) {
        Events.register(event, translate(event))
    }

    private fun translate(key: Class<out Event>): MutableText {
        return Events.translate(HiiroSakuraClient, key).mText
    }


}