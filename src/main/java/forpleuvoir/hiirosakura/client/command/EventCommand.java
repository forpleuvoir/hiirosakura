package forpleuvoir.hiirosakura.client.command;

import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.command.arguments.EventArgumentType;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

/**
 * 事件指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name EventCommand
 * <p>#create_time 2021-07-23 14:48
 */
public class EventCommand {
    public static final String TYPE = "event";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(COMMAND_PREFIX + TYPE)
                .then(literal("subscribe")
                        .then(argument("eventType", EventArgumentType.event())
                                .then(argument("name", StringArgumentType.string())
                                        .then(argument("timeTask", StringArgumentType.string())
                                                .executes(EventCommand::subscribe)
                                        )
                                )
                        )
                )
                .then(literal("unsubscribe")
                        .then(argument("eventType", EventArgumentType.event())
                                .then(argument("name", StringArgumentType.string())
                                        .executes(EventCommand::unsubscribe)
                                )
                        )
                )
        );
    }

    public static int subscribe(CommandContext<FabricClientCommandSource> context) {
        var eventType = EventArgumentType.getEventType(context, "eventType");
        var name = StringArgumentType.getString(context, "name");
        var timeTaskJson = new JsonParser().parse(StringArgumentType.getString(context, "timeTask")).getAsJsonObject();
        EventBus.subscribe(eventType, name, event -> TimeTaskHandler.getInstance().addTask(TimeTaskParser.parse(timeTaskJson)));
        return 1;
    }

    public static int unsubscribe(CommandContext<FabricClientCommandSource> context) {
        var eventType = EventArgumentType.getEventType(context, "eventType");
        var name = StringArgumentType.getString(context, "name");
        EventBus.unsubscribe(eventType, name);
        return 1;
    }
}
