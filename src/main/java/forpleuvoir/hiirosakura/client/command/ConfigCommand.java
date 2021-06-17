package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;

import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.COMMAND_PREFIX;
import static forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand.getTranslatableTextKey;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

/**
 * 配置相关指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name ConfigCommand
 * <p>#create_time 2021/6/17 22:35
 */
public class ConfigCommand {
    public static final String CONFIG = "config";
    public static final String DATA = "data";

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(COMMAND_PREFIX + CONFIG)
                        .then(literal("reload")
                                      .executes(ConfigCommand::configReload)
                        )
                        .then(literal("save")
                            .executes(ConfigCommand::configSave)
                        )
        );
        dispatcher.register(
                literal(COMMAND_PREFIX + DATA)
                        .then(literal("reload")
                                      .executes(ConfigCommand::dataReload)
                        )
                        .then(literal("save")
                                      .executes(ConfigCommand::dataSave)
                        )
        );
    }

    public static int configSave(CommandContext<FabricClientCommandSource> context) {
        Configs.getConfigHandler().save();
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(CONFIG, "save")));
        return 1;
    }

    public static int configReload(CommandContext<FabricClientCommandSource> context) {
        Configs.getConfigHandler().load();
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(CONFIG, "reload")));
        return 1;
    }

    public static int dataSave(CommandContext<FabricClientCommandSource> context) {
        HiiroSakuraDatas.getConfigHandler().save();
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(DATA, "save")));
        return 1;
    }

    public static int dataReload(CommandContext<FabricClientCommandSource> context) {
        HiiroSakuraDatas.getConfigHandler().load();
        context.getSource().sendFeedback(new TranslatableText(getTranslatableTextKey(DATA, "reload")));
        return 1;
    }
}
