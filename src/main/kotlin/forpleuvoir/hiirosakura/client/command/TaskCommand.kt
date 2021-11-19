package forpleuvoir.hiirosakura.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser
import forpleuvoir.hiirosakura.client.feature.task.executor.JSHeadFile
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.StringUtil
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath

/**
 * 任务指令
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.command
 *
 * #class_name TaskCommand
 *
 * #create_time 2021/7/22 23:28
 */
object TaskCommand {
    private const val TYPE = "task"
    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(
            ClientCommandManager.literal(COMMAND_PREFIX + TYPE)
                .then(ClientCommandManager.literal("add")
                    .then(ClientCommandManager.argument("timeTask", NbtPathArgumentType.nbtPath())
                        .executes { add(it) }
                    )
                )
                .then(ClientCommandManager.literal("remove")
                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                        .suggests { _: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder ->
                            CommandSource.suggestMatching(
                                TimeTaskHandler.INSTANCE!!.keys,
                                builder
                            )
                        }
                        .executes { remove(it) }
                    )
                )
                .then(ClientCommandManager.literal("clear")
                    .executes { context: CommandContext<FabricClientCommandSource> ->
                        TimeTaskHandler.INSTANCE!!.clear()
                        context.source.sendFeedback(StringUtil.translatableText("command.task.clear"))
                        1
                    }
                )
                .then(ClientCommandManager.literal("headFile")
                    .then(ClientCommandManager.literal("open")
                        .executes {
                            JSHeadFile.openFile()
                            1
                        }
                    )
                    .then(ClientCommandManager.literal("reload")
                        .executes { context: CommandContext<FabricClientCommandSource> ->
                            if (JSHeadFile.read())
                                context.source.sendFeedback(StringUtil.translatableText("command.task.reload"))
                            else
                                context.source.sendFeedback(StringUtil.translatableText("command.task.reload.fail"))
                            1
                        }
                    )
                    .then(ClientCommandManager.literal("default")
                        .executes {
                            JSHeadFile.default()
                            1
                        }
                    )
                )
        )
    }

    fun add(context: CommandContext<FabricClientCommandSource>): Int {
        val nbt = context.getArgument("timeTask", NbtPath::class.java) as NbtPath
        val timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(nbt.toString()), null)
        TimeTaskHandler.INSTANCE!!.addTask(timeTask)
        context.source
            .sendFeedback(StringUtil.translatableText("command.task.add", "§a${timeTask.name}§r"))
        return 1
    }

    fun remove(context: CommandContext<FabricClientCommandSource>): Int {
        val name = StringArgumentType.getString(context, "name")
        context.source.sendFeedback(StringUtil.translatableText("command.task.remove", "§c$name§r"))
        TimeTaskHandler.INSTANCE!!.removeTask(name)
        return 1
    }
}