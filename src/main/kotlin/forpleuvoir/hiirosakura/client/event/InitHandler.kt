package forpleuvoir.hiirosakura.client.event

import fi.dy.masa.malilib.config.ConfigManager
import fi.dy.masa.malilib.event.InputEventHandler
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand
import forpleuvoir.hiirosakura.client.common.IInitialized
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import forpleuvoir.hiirosakura.client.config.HotKeys
import forpleuvoir.hiirosakura.client.config.TogglesHotKeys
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler
import forpleuvoir.hiirosakura.client.feature.task.executor.JSHeadFile
import forpleuvoir.hiirosakura.client.util.HSLogger
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager

/**
 * 初始化处理程序
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.event
 *
 * #class_name InitHandler
 *
 * #create_time 2021/6/15 20:57
 */
object InitHandler : IInitialized {
	private val log = HSLogger.getLogger(InitHandler::class.java)

	override fun initialize() {
		log.info("初始化...")
		//配置初始化
		ConfigManager.getInstance().registerConfigHandler(HiiroSakuraClient.MOD_ID, Configs.configHandler)
		HiiroSakuraDatas.initialize()
		//客户端指令注册
		HiiroSakuraClientCommand.registerClientCommands(ClientCommandManager.DISPATCHER)
		//初始化输入处理程序
		InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler)
		//按键回调
		HotKeys.initCallback(HiiroSakuraClient)
		TogglesHotKeys.initCallback(HiiroSakuraClient)
		TimeTaskHandler.initialize()
		JSHeadFile.initialize()
	}

}