package forpleuvoir.hiirosakura.client.event;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.command.base.HiiroSakuraClientCommand;
import forpleuvoir.hiirosakura.client.config.Callbacks;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.event.InputHandler;
import forpleuvoir.hiirosakura.client.feature.chatshow.HiiroSakuraChatShow;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;

/**
 * 初始化处理程序
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.event
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
        //初始化聊天泡泡
        HiiroSakuraChatShow.initialize();
        //初始化输入处理程序
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());

        Callbacks.init(MinecraftClient.getInstance());
    }
}