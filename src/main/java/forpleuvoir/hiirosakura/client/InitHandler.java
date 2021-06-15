package forpleuvoir.hiirosakura.client;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand;
import forpleuvoir.hiirosakura.client.config.Configs;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

/**
 * 初始化处理程序
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client
 * <p>#class_name InitHandler
 * <p>#create_time 2021/6/15 20:57
 */
public class InitHandler implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        //配置初始化
        ConfigManager.getInstance().registerConfigHandler(HiiroSakuraClient.MOD_ID, new Configs());
        //客户端指令注册
        HiiroSakuraClientCommand.registerClientCommands(ClientCommandManager.DISPATCHER);
    }
}
