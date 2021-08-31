package forpleuvoir.hiirosakura.client.command.base

import com.mojang.brigadier.CommandDispatcher
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.*
import forpleuvoir.hiirosakura.client.util.HSLogger
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource

/**
 * 客户端指令处理
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command.base
 *
 * #class_name HiiroSakuraClientCommand
 *
 * #create_time 2021/6/10 22:16
 */
object HiiroSakuraClientCommand {
	@Transient
	private val log = HSLogger.getLogger(HiiroSakuraClientCommand::class.java)

	/**
	 * 客户端指令将以 “/hs:”开头
	 */
	const val COMMAND_PREFIX = "hs:"

	/**
	 * 所有客户端指令在此注册
	 *
	 * @param commandDispatcher [CommandDispatcher]
	 */
	fun registerClientCommands(commandDispatcher: CommandDispatcher<FabricClientCommandSource>) {
		log.info("{}客户端指令注册...", HiiroSakuraClient.MOD_NAME)
		GammaCommand.register(commandDispatcher)
		TooltipCommand.register(commandDispatcher)
		ConfigCommand.register(commandDispatcher)
		ServerChatMessageRegexCommand.register(commandDispatcher)
		SwitchCameraEntityCommand.register(commandDispatcher)
		TaskCommand.register(commandDispatcher)
		EventCommand.register(commandDispatcher)
	}

	@JvmStatic
	fun getTranslatableTextKey(type: String, key: String): String {
		return String.format("%s.command.%s.%s", HiiroSakuraClient.MOD_ID, type, key)
	}
}