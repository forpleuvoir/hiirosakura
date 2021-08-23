package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import fi.dy.masa.malilib.gui.GuiBase
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.addTask
import forpleuvoir.hiirosakura.client.command.arguments.EventArgumentType.Companion.eventType
import forpleuvoir.hiirosakura.client.command.arguments.EventArgumentType.Companion.getEventType
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.gui.event.EventScreen
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath

/**
 * 事件指令
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command
 *
 * #class_name EventCommand
 *
 * #create_time 2021-07-23 14:48
 */
object EventCommand {
	private const val TYPE = "event"
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		dispatcher.register(ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
			.then(ClientCommandManager.literal("subscribe")
				.then(ClientCommandManager.argument("eventType", eventType())
					.then(ClientCommandManager.argument("timeTask", NbtPathArgumentType.nbtPath())
						.executes {  subscribe(it) }
					)
				)
			)
			.then(ClientCommandManager.literal("unsubscribe")
				.then(ClientCommandManager.argument("eventType", eventType())
					.then(ClientCommandManager.argument("name", StringArgumentType.string())
						.executes { unsubscribe(it) }
					)
				)
			)
			.then(ClientCommandManager.literal("gui")
				.executes {
					addTask { GuiBase.openGui(EventScreen()) }
					1
				}
			)
		)
	}

	private fun subscribe(context: CommandContext<FabricClientCommandSource>): Int {
		val nbt = context.getArgument("timeTask", NbtPath::class.java) as NbtPath
		val eventType = getEventType(context, "eventType")
		HiiroSakuraData.HIIRO_SAKURA_EVENTS.subscribe(eventType, nbt.toString())
		return 1
	}

	private fun unsubscribe(context: CommandContext<FabricClientCommandSource>): Int {
		val eventType = getEventType(
			context, "eventType"
		)
		val name = StringArgumentType.getString(context, "name")
		HiiroSakuraData.HIIRO_SAKURA_EVENTS.unsubscribe(eventType, name)
		return 1
	}
}