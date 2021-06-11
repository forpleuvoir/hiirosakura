package forpleuvoir.hiirosakura.client.command.common;

import com.mojang.brigadier.CommandDispatcher;
import forpleuvoir.hiirosakura.client.command.GammaCommand;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

/**
 * 客户端指令处理
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command
 * <p>#class_name HiiroSakuraClientCommand
 * <p>#create_time 2021/6/10 22:16
 */
public class HiiroSakuraClientCommand {

    /**
     * 客户端指令将以 “/hs:”开头
     */
    public static final String COMMAND_PREFIX = "hs:";

    /**
     * 所有客户端指令在此注册
     *
     * @param commandDispatcher {@link CommandDispatcher}
     */
    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> commandDispatcher) {
        GammaCommand.register(commandDispatcher);
    }

}
