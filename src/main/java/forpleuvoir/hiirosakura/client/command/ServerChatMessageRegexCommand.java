package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

/**
 * 聊天消息正则匹配相关指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name ServerChatMessageRegexCommand
 * <p>#create_time 2021/6/24 0:33
 */
public class ServerChatMessageRegexCommand {
    public static final String TYPE = "scmr";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(COMMAND_PREFIX + TYPE)
                                    .then(literal("set")
                                                  .then(argument("regex", StringArgumentType.string())
                                                                .executes(ServerChatMessageRegexCommand::set)
                                                  )
                                    )
                                    .then(literal("remove")
                                                  .executes(ServerChatMessageRegexCommand::remove
                                                  )
                                    )
        );
    }

    private static int set(CommandContext<FabricClientCommandSource> context) {
        var regex = StringArgumentType.getString(context,"regex");
        HiiroSakuraDatas.SERVER_CHAT_MESSAGE_REGEX.put(regex);
        return 1;
    }

    private static int remove(CommandContext<FabricClientCommandSource> context) {
        HiiroSakuraDatas.SERVER_CHAT_MESSAGE_REGEX.remove();
        return 1;
    }
}
