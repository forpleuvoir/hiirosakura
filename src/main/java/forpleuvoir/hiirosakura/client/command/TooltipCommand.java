package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

/**
 * 工具提示相关指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name TooltipCommand
 * <p>#create_time 2021/6/17 21:58
 */
public class TooltipCommand {
    public static final String TYPE = "tooltip";
    private static final SimpleCommandExceptionType AIR = new SimpleCommandExceptionType(
            new TranslatableText(getTranslatableTextKey(TYPE, "air")));
    private static final DynamicCommandExceptionType REMOVE_FAILED = new DynamicCommandExceptionType(itemName ->
                                                                                                             new TranslatableText(
                                                                                                                     getTranslatableTextKey(
                                                                                                                             TYPE,
                                                                                                                             "removeFailed"
                                                                                                                     ),
                                                                                                                     itemName
                                                                                                             ));


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(COMMAND_PREFIX + TYPE)
                                    .then(literal("add")
                                                  .then(argument("item", ItemStackArgumentType.itemStack())
                                                                .then(argument("tip", StringArgumentType.string())
                                                                              .executes(TooltipCommand::add)
                                                                )
                                                  )
                                                  .then(argument("tip", StringArgumentType.string())
                                                                .executes(TooltipCommand::addMainHandItem)
                                                  ))
                                    .then(literal("remove")
                                                  .then(argument("item", ItemStackArgumentType.itemStack())
                                                                .then(argument("index", IntegerArgumentType.integer(-1))
                                                                              .executes(TooltipCommand::remove)
                                                                )
                                                  )
                                                  .then(argument("index", IntegerArgumentType.integer(-1))
                                                                .executes(TooltipCommand::removeMainHandItem)
                                                  )
                                    )
        );
    }

    public static int addMainHandItem(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        var tip = StringArgumentType.getString(context, "tip");
        var item = context.getSource().getPlayer().getMainHandStack();
        if (item.getItem().equals(Items.AIR)) throw AIR.create();
        HiiroSakuraDatas.TOOLTIP.add(item, tip);
        var feedback = new LiteralText("").append(item.toHoverableText());
        feedback.append(new TranslatableText(getTranslatableTextKey(TYPE, "add")));
        feedback.append(new LiteralText(tip));
        context.getSource().sendFeedback(feedback);
        return 1;
    }

    public static int add(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        var item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
        var tip = StringArgumentType.getString(context, "tip");
        if (item.getItem().equals(Items.AIR)) throw AIR.create();
        HiiroSakuraDatas.TOOLTIP.add(item, tip);
        var feedback = new LiteralText("").append(item.toHoverableText());
        feedback.append(new TranslatableText(getTranslatableTextKey(TYPE, "add")));
        feedback.append(new LiteralText(tip));
        context.getSource().sendFeedback(feedback);
        return 1;
    }

    public static int removeMainHandItem(CommandContext<FabricClientCommandSource> context
    ) throws CommandSyntaxException {
        var index = IntegerArgumentType.getInteger(context, "index");
        var item = context.getSource().getPlayer().getMainHandStack();
        if (item.getItem().equals(Items.AIR)) throw REMOVE_FAILED.create(item.getName());
        String remove = HiiroSakuraDatas.TOOLTIP.remove(item, index);
        if (!StringUtils.isEmpty(remove)) {
            var feedback = new LiteralText("").append(item.toHoverableText());
            feedback.append(new TranslatableText(getTranslatableTextKey(TYPE, "remove")));
            feedback.append(new LiteralText(remove));
            context.getSource().sendFeedback(feedback);
        } else {
            throw REMOVE_FAILED.create(item.getName());
        }
        return 1;
    }

    public static int remove(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        var item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
        var index = IntegerArgumentType.getInteger(context, "index");
        if (item.getItem().equals(Items.AIR)) throw REMOVE_FAILED.create(item.getName());
        String remove = HiiroSakuraDatas.TOOLTIP.remove(item, index);
        if (!StringUtils.isEmpty(remove)) {
            var feedback = new LiteralText("").append(item.toHoverableText());
            feedback.append(new TranslatableText(getTranslatableTextKey(TYPE, "remove")));
            feedback.append(new LiteralText(remove));
            context.getSource().sendFeedback(feedback);
        } else {
            throw REMOVE_FAILED.create(item.getName());
        }
        return 1;
    }
}
