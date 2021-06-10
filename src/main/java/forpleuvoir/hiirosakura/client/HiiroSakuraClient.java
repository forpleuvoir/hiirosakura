package forpleuvoir.hiirosakura.client;

import forpleuvoir.hiirosakura.client.command.common.HiiroSakuraClientCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client
 * <p>#class_name HiiroSakuraClient
 * <p>#create_time 2021/6/10 21:38
 */
@Environment(EnvType.CLIENT)
public class HiiroSakuraClient implements ClientModInitializer {
    public static final Logger log = LoggerFactory.getLogger(HiiroSakuraClient.class);
    public static final String MOD_ID = "hiirosakura";
    public static final String MOD_NAME = "Hiiro Sakura";


    @Override
    public void onInitializeClient() {
        //客户端指令注册
        HiiroSakuraClientCommand.registerClientCommands( ClientCommandManager.DISPATCHER);

    }
}
