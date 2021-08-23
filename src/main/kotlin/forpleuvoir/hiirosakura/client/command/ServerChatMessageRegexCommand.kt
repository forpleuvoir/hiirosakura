package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource

/**
 * 聊天消息正则匹配相关指令
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command
 *
 * #class_name ServerChatMessageRegexCommand
 *
 * #create_time 2021/6/24 0:33
 */
object ServerChatMessageRegexCommand {
	private const val TYPE = "scmr"
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
			.then(ClientCommandManager.literal("set")
				.then(ClientCommandManager.argument("regex", StringArgumentType.string())
					.executes { set(it) }
				)
			)
			.then(ClientCommandManager.literal("remove")
				.executes { remove() }
			)
		)
	}

	private fun set(context: CommandContext<FabricClientCommandSource>): Int {
		val regex = StringArgumentType.getString(context, "regex")
		HiiroSakuraData.SERVER_CHAT_MESSAGE_REGEX.put(regex)
		return 1
	}

	private fun remove(): Int {
		HiiroSakuraData.SERVER_CHAT_MESSAGE_REGEX.remove()
		return 1
	}
}