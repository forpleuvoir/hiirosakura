package forpleuvoir.hiirosakura.client.initialize

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.chatbubble.HiiroSakuraChatBubble
import forpleuvoir.hiirosakura.client.feature.common.javascript.HeadFile
import forpleuvoir.hiirosakura.client.feature.event.EventRegister
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.event.events.ClientEndTickEvent
import forpleuvoir.ibuki_gourd.event.events.GameInitializedEvent
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.event.Event

/**
 * 初始化处理程序
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.initialize
 *
 * 文件名 HiiroSakuraInitialize
 *
 * 创建时间 2021/6/15 20:57
 *
 * @author forpleuvoir
 */
object HiiroSakuraInitialize : IModInitialize {
	private val log = HSLogger.getLogger(HiiroSakuraInitialize::class.java)

	override fun initialize() {
		log.info("${HiiroSakuraClient.modName} initializing...")
		//配置初始化
		//EventRegister
		EventRegister.initialize()
		EventBus.subscribe<GameInitializedEvent> {
			HiiroSakuraData.load()
			HiiroSakuraData.initialize()
		}
		EventBus.subscribe<ClientEndTickEvent> {
			HiiroSakuraClient.endTick()
			AnalogInput.tick()
		}
		HeadFile.initialize()
		HiiroSakuraChatBubble.initialize()
		//客户端指令注册
		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			HiiroSakuraClientCommand.registerClientCommands(dispatcher)
		}
		log.info("${HiiroSakuraClient.modName} Initialized...")
	}

}

