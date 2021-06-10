package forpleuvoir.hiirosakura.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forpleuvoir.hiirosakura.client.command.common.HiiroSakuraClientCommand;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.*;

/**
 * 调整gamma值指令
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name GammaCommand
 * <p>#create_time 2021/6/10 22:39
 */
public class GammaCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(HiiroSakuraClientCommand.BASE_COMMAND + "gamma")
                        .then(argument("value", DoubleArgumentType.doubleArg(0, 100))
                                      .executes(GammaCommand::set)));
    }

    /**
     * 修改伽马值
     * <p>用法 /hs:gamma <double>
     * @param context 指令上下文
     * @return 执行次数
     */
    public static int set(CommandContext<FabricClientCommandSource> context){
        var value = DoubleArgumentType.getDouble(context,"value");
        context.getSource().getClient().options.gamma = value;

        context.getSource().sendFeedback(new TranslatableText("suika.command.feedback.gamma", value));
        return 1;
    }

}
