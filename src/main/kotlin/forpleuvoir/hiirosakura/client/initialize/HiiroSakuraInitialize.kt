package forpleuvoir.hiirosakura.client.initialize

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.chatbubble.HiiroSakuraChatBubble
import forpleuvoir.hiirosakura.client.feature.common.javascript.HeadFile
import forpleuvoir.hiirosakura.client.feature.event.EventRegister
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.config.ConfigManager
import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.event.events.ClientEndTickEvent
import forpleuvoir.ibuki_gourd.event.events.GameInitializedEvent
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager

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
		ConfigManager.register(HiiroSakuraClient, Configs)
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
		HiiroSakuraClientCommand.registerClientCommands(ClientCommandManager.DISPATCHER)
		log.info("${HiiroSakuraClient.modName} Initialized...")
	}

}