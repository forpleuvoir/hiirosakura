package forpleuvoir.hiirosakura.client.feature.task.executor.base;


import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.mixin.MixinMinecraftClientInterface;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;

import static forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.*;

/**
 * js脚本接口实现
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name JavaScriptInterface
 * <p>#create_time 2021/7/27 23:20
 */
public class JavaScriptInterface implements IJavaScriptInterface {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final HiiroSakuraClient hs = HiiroSakuraClient.getInstance();
    private static final AnalogInput input = AnalogInput.getInstance();

    @Override
    public void forward(int tick) {
        input.set(FORWARD, tick);
    }

    @Override
    public void back(int tick) {
        input.set(BACK, tick);
    }

    @Override
    public void left(int tick) {
        input.set(LEFT, tick);
    }

    @Override
    public void right(int tick) {
        input.set(RIGHT, tick);
    }

    @Override
    public void jump(int tick) {
        input.set(JUMP, tick);
    }

    @Override
    public void sneak(int tick) {
        input.set(SNEAK, tick);
    }

    @Override
    public void attack(int tick) {
        input.set(ATTACK, tick);
    }

    @Override
    public void use(int tick) {
        input.set(USE, tick);
    }

    @Override
    public void pickItem(int tick) {
        input.set(PICK_ITEM, tick);
    }

    @Override
    public void doAttack() {
        ((MixinMinecraftClientInterface) mc).callDoAttack();
    }

    @Override
    public void doItemUse() {
        ((MixinMinecraftClientInterface) mc).callDoItemUse();
    }

    @Override
    public void doItemPick() {
        ((MixinMinecraftClientInterface) mc).callDoItemPick();
    }

    @Override
    public void joinServer(String address) {
        joinServer(address, 5);
    }

    @Override
    public void joinServer(String address, int maxConnect) {
        if (mc.world == null && maxConnect > ServerInfoUtil.getDisConnectCounter()) {
            var multiplayerScreen = new MultiplayerScreen(new TitleScreen());
            mc.setScreen(multiplayerScreen);
            TimeTaskHandler.getInstance().addTask(
                    TimeTask.once(
                            task -> {
                                mc.disconnect();
                                ConnectScreen.connect(multiplayerScreen, mc, ServerAddress.parse(address), null);
                            },
                            15,
                            "#joinServer"
                    )
            );
        }
    }

    @Override
    public void sendChatMessage(String message) {
        hs.sendMessage(message);
    }
}
