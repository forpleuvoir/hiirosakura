package forpleuvoir.hiirosakura.client.command.base;

import com.mojang.brigadier.CommandDispatcher;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.command.GammaCommand;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端指令处理
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.command.base
 * <p>#class_name HiiroSakuraClientCommand
 * <p>#create_time 2021/6/10 22:16
 */
public class HiiroSakuraClientCommand {
    private transient static final Logger log = LoggerFactory.getLogger(HiiroSakuraClientCommand.class);

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
        log.info("{}客户端指令注册...", HiiroSakuraClient.MOD_NAME);
        GammaCommand.register(commandDispatcher);
    }

}
