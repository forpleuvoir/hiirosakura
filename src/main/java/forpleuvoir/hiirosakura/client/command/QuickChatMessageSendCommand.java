package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

/**
 * 快捷发送聊天消息指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name QuickChatMessageSendCommand
 * <p>#create_time 2021/6/17 0:10
 */
public class QuickChatMessageSendCommand {
    public static final String TYPE = "qcms";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(COMMAND_PREFIX + TYPE)
                        .then(literal("add")
                                      .then(argument("remark", StringArgumentType.string())
                                                    .then(argument("messageStr", StringArgumentType.string())
                                                                  .executes(QuickChatMessageSendCommand::add)
                                                    )
                                      )
                        )
                        .then(literal("show")
                                      .executes(QuickChatMessageSendCommand::show)
                        )
                        .then(literal("remove")
                                      .then(argument("remark", StringArgumentType.string())
                                                    .suggests((c, b) ->
                                                                      suggestMatching(
                                                                              HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
                                                                                      .getKeySet(), b)
                                                    )
                                                    .executes(QuickChatMessageSendCommand::remove)
                                      )
                        )
                        .then(literal("rename")
                                      .then(argument("remark", StringArgumentType.string())
                                                    .suggests((c, b) ->
                                                                      suggestMatching(
                                                                              HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
                                                                                      .getKeySet(), b)
                                                    )
                                                    .then(argument("newRemark", StringArgumentType.string())
                                                                  .executes(QuickChatMessageSendCommand::rename)
                                                    )

                                      )
                        )
                        .then(literal("reset")
                                      .then(argument("remark", StringArgumentType.string())
                                                    .suggests((c, b) ->
                                                                      suggestMatching(
                                                                              HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
                                                                                      .getKeySet(), b)
                                                    )
                                                    .then(argument("newValue", StringArgumentType.string())
                                                                  .executes(QuickChatMessageSendCommand::reset)
                                                    )

                                      )
                        )
        );


    }

    public static int add(CommandContext<FabricClientCommandSource> context) {
        var remark = StringArgumentType.getString(context, "remark");
        var messageStr = StringArgumentType.getString(context, "messageStr");
        if (!HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.add(remark, messageStr)) {
            context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(TYPE, "add.fail"), remark));
            return 1;
        }
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(TYPE, "add"), remark));
        return 1;
    }

    public static int show(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getAsText());
        return 1;
    }

    public static int remove(CommandContext<FabricClientCommandSource> context) {
        var remark = StringArgumentType.getString(context, "remark");
        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.remove(remark);
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(TYPE, "remove")));
        return 1;
    }

    public static int rename(CommandContext<FabricClientCommandSource> context) {
        var remark = StringArgumentType.getString(context, "remark");
        var newRemark = StringArgumentType.getString(context, "newRemark");
        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.rename(remark, newRemark);
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(TYPE, "rename")));
        return 1;
    }


    public static int reset(CommandContext<FabricClientCommandSource> context) {
        var remark = StringArgumentType.getString(context, "remark");
        var newValue = StringArgumentType.getString(context, "newValue");
        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.put(remark, newValue);
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(TYPE, "reset")));
        return 1;
    }

}