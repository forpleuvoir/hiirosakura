package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.NbtPathArgumentType;

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
                        .then(argument("timeTask", StringArgumentType.string())
                                .executes(TaskCommand::add)
                        )
                )
                .then(literal("test")
                        .then(argument("nbt", NbtPathArgumentType.nbtPath())
                                .executes(TaskCommand::test)
                        )
                )
        );
    }

    public static int test(CommandContext<FabricClientCommandSource> context) {
        var nbt = (NbtPathArgumentType.NbtPath) context.getArgument("nbt", NbtPathArgumentType.NbtPath.class);
        TimeTask timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(nbt.toString()));
        TimeTaskHandler.getInstance().addTask(timeTask);
        return 1;
    }

    public static int add(CommandContext<FabricClientCommandSource> context) {
        var json = StringArgumentType.getString(context, "timeTask");
        TimeTask timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(json));
        TimeTaskHandler.getInstance().addTask(timeTask);
        return 1;
    }
}
