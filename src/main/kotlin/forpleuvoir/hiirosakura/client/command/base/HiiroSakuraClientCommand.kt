package forpleuvoir.hiirosakura.client.command.base

import com.mojang.brigadier.CommandDispatcher
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.*
import forpleuvoir.hiirosakura.client.util.HSLogger
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

/**
 * 客户端指令处理
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.command.base
 *
 * 文件名 HiiroSakuraClientCommand
 *
 * 创建时间 2021/6/10 22:16
 */
object HiiroSakuraClientCommand {

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
		log.info("客户端指令注册...")
		GammaCommand.register(commandDispatcher)
		ConfigCommand.register(commandDispatcher)
		SwitchCameraEntityCommand.register(commandDispatcher)
		TaskCommand.register(commandDispatcher)
		NbtCommand.register(commandDispatcher)
	}

	@JvmStatic
	fun getTranslatableTextKey(type: String, key: String): String {
		return String.format("%s.command.%s.%s", HiiroSakuraClient.modId, type, key)
	}
}