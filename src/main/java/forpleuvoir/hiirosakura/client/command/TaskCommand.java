package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

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
        dispatcher.register(literal(COMMAND_PREFIX + TYPE)
                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("startTime", IntegerArgumentType.integer(0))
                                        .then(argument("cycles", IntegerArgumentType.integer(1))
                                                .then(argument("cyclesTime", IntegerArgumentType.integer(0))
                                                        .then(argument("message", StringArgumentType.string())
                                                                .executes(TaskCommand::test)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public static int test(CommandContext<FabricClientCommandSource> context) {
        var name = StringArgumentType.getString(context, "name");
        var startTime = IntegerArgumentType.getInteger(context, "startTime");
        var cycles = IntegerArgumentType.getInteger(context, "cycles");
        var cyclesTime = IntegerArgumentType.getInteger(context, "cyclesTime");
        var message = StringArgumentType.getString(context, "message");
        TimeTaskHandler.getInstance().addTask(new TimeTask(client -> {
            client.sendMessage(message);
        }, new TimeTaskData(startTime, cycles, cyclesTime, name)));
        return 1;
    }
}
