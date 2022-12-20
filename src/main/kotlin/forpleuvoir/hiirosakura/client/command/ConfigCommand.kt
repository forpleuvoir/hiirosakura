package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.getTranslatableTextKey
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.common.text
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

/**
 * 配置相关指令
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.command
 *
 * 文件名 ConfigCommand
 *
 * 创建时间 2021/6/17 22:35
 */
object ConfigCommand {
	private const val CONFIG = "config"
	private const val DATA = "data"
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(
			ClientCommandManager.literal(COMMAND_PREFIX + CONFIG)
				.then(ClientCommandManager.literal("reload")
					.executes { configReload(it) }
				)
				.then(ClientCommandManager.literal("save")
					.executes { configSave(it) }
				)
		)
		dispatcher.register(
			ClientCommandManager.literal(COMMAND_PREFIX + DATA)
				.then(ClientCommandManager.literal("reload")
					.executes { dataReload(it) }
				)
				.then(ClientCommandManager.literal("save")
					.executes { dataSave(it) }
				)
		)
	}

	private fun configSave(context: CommandContext<FabricClientCommandSource>): Int {
        Configs.save()
        context.source.sendFeedback(getTranslatableTextKey(CONFIG, "save").tText().text)
		return 1
	}

	private fun configReload(context: CommandContext<FabricClientCommandSource>): Int {
        Configs.load()
        context.source.sendFeedback(getTranslatableTextKey(CONFIG, "reload").tText().text)
		return 1
	}

	private fun dataSave(context: CommandContext<FabricClientCommandSource>): Int {
        HiiroSakuraData.save()
        context.source.sendFeedback(getTranslatableTextKey(DATA, "save").tText().text)
		return 1
	}

	private fun dataReload(context: CommandContext<FabricClientCommandSource>): Int {
        HiiroSakuraData.load()
        context.source.sendFeedback(getTranslatableTextKey(DATA, "reload").tText().text)
		return 1
	}
}