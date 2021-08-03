package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser;
import forpleuvoir.hiirosakura.client.feature.task.executor.JSHeadFile;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.NbtPathArgumentType;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

/**
 * 任务指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name TaskCommand
 * <p>#create_time 2021/7/22 23:28
 */
public class TaskCommand {
    public static final String TYPE = "task";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(COMMAND_PREFIX + TYPE)
                        .then(literal("add")
                                      .then(argument("timeTask", NbtPathArgumentType.nbtPath())
                                                    .executes(TaskCommand::add)
                                      )
                        )
                        .then(literal("remove")
                                      .then(argument("name", StringArgumentType.string())
                                                    .suggests(((context, builder) -> suggestMatching(
                                                            TimeTaskHandler.getInstance().getKeys(),
                                                            builder
                                                    )))
                                                    .executes(TaskCommand::remove)
                                      )
                        )
                        .then(literal("clear")
                                      .executes(context -> {
                                          TimeTaskHandler.getInstance().clear();
                                          context.getSource()
                                                 .sendFeedback(StringUtil.translatableText("command.task.clear"));
                                          return 1;
                                      })
                        )
                        .then(literal("headFile")
                                      .then(literal("open")
                                                    .executes(context -> {
                                                        JSHeadFile.openFile();
                                                        return 1;
                                                    })
                                      )
                                      .then(literal("reload")
                                                    .executes(context -> {
                                                        if (JSHeadFile.read())
                                                            context.getSource()
                                                                   .sendFeedback(StringUtil.translatableText("command.task.reload"));
                                                        else  context.getSource()
                                                                     .sendFeedback(StringUtil.translatableText("command.task.reload.fail"));
                                                        return 1;
                                                    })
                                      )
                        )
        );
    }

    public static int add(CommandContext<FabricClientCommandSource> context) {
        var nbt = (NbtPathArgumentType.NbtPath) context.getArgument("timeTask", NbtPathArgumentType.NbtPath.class);
        TimeTask timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(nbt.toString()), null);
        TimeTaskHandler.getInstance().addTask(timeTask);
        context.getSource()
               .sendFeedback(StringUtil.translatableText("command.task.add", "§a" + timeTask.getName() + "§r"));
        return 1;
    }

    public static int remove(CommandContext<FabricClientCommandSource> context) {
        var name = StringArgumentType.getString(context, "name");
        context.getSource().sendFeedback(StringUtil.translatableText("command.task.remove", "§c" + name + "§r"));
        TimeTaskHandler.getInstance().removeTask(name);
        return 1;
    }
}
