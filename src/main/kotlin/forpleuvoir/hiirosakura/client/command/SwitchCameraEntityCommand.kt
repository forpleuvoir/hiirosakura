package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity
import forpleuvoir.hiirosakura.client.util.StringUtil
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource

/**
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.command
 *
 * 文件名 SwitchCameraEntityCommand
 *
 * 创建时间 2021/6/24 20:56
 */
object SwitchCameraEntityCommand {
	private val hs = HiiroSakuraClient
	private const val TYPE = "sce"
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
			.then(ClientCommandManager.argument("player", StringArgumentType.string())
				.suggests { _: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder? ->
					CommandSource.suggestMatching(
						SwitchCameraEntity.playersSuggest,
						builder
					)
				}
				.executes { execute(it) }
			)
			.then(ClientCommandManager.literal("switchToTarget")
				.executes { switchToTarget() })
			.then(ClientCommandManager.literal("setTarget")
				.executes { setTarget() })
		)
	}

	private fun execute(context: CommandContext<FabricClientCommandSource>): Int {
		val playerName = StringArgumentType.getString(context, "player")
		SwitchCameraEntity.switchOtherPlayer(playerName)
		return 1
	}

	private fun switchToTarget(): Int {
		if (!SwitchCameraEntity.switchToTarget()) hs.showMessage(StringUtil.translatableText("command.sce.target.null"))
		return 1
	}

	private fun setTarget(): Int {
		if (!SwitchCameraEntity.setTargetEntity()) hs.showMessage(StringUtil.translatableText("command.sce.target.null")) else hs.showMessage(
			StringUtil.translatableText("command.sce.target.set")
		)
		return 1
	}
}