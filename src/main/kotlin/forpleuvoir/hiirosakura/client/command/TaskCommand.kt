package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.feature.common.javascript.JsRunner
import forpleuvoir.hiirosakura.client.feature.timertask.Timer
import forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JsExecutor
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.StringUtil
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.command

 * 文件名 TaskCommand

 * 创建时间 2022/1/19 14:56

 * @author forpleuvoir

 */
object TaskCommand {
	private const val ROOT = COMMAND_PREFIX + "task"

	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(
			literal(ROOT)
				.then(
					literal("add")
						.then(
							argument("timeTask", NbtPathArgumentType.nbtPath())
								.executes(::add)
						)
				)
				.then(
					literal("remove")
						.then(
							argument("name", StringArgumentType.string())
								.suggests { _, builder -> CommandSource.suggestMatching(Timer.keys, builder) }
								.executes(::remove)
						)
				)
				.then(literal("clear")
					.executes {
						Timer.clear()
						it.source.sendFeedback(StringUtil.translatableText("command.task.clear"))
						1
					}
				)
				.then(
					literal("execute")
						.then(
							argument("script", StringArgumentType.string())
								.executes(::execute)
						)
				)
		)
	}

	private fun add(context: CommandContext<FabricClientCommandSource>): Int {
		val nbt = context.getArgument("timeTask", NbtPath::class.java) as NbtPath
		JsExecutor.fromJson(JsonUtil.parseToJsonObject(nbt.toString()))?.let {
			Timer.scheduleEnd(it)
			context.source.sendFeedback(StringUtil.translatableText("command.task.add", "§a${it.name}§r"))
		}
		return 1
	}

	private fun remove(context: CommandContext<FabricClientCommandSource>): Int {
		val name = StringArgumentType.getString(context, "name")
		context.source.sendFeedback(StringUtil.translatableText("command.task.remove", "§c$name§r"))
		Timer.remove(name.split(":")[1])
		return 1
	}

	private fun execute(context: CommandContext<FabricClientCommandSource>): Int {
		val script = StringArgumentType.getString(context, "script")
		JsRunner(script).run()
		return 1
	}

}