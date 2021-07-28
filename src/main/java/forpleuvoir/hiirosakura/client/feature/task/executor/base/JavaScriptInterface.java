package forpleuvoir.hiirosakura.client.feature.task.executor.base;


import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.input.AnalogInput;
import forpleuvoir.hiirosakura.client.mixin.MixinMinecraftClientInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;

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
        input.setPressingForward(tick);
    }

    @Override
    public void back(int tick) {
        input.setPressingBack(tick);
    }

    @Override
    public void left(int tick) {
        input.setPressingLeft(tick);
    }

    @Override
    public void right(int tick) {
        input.setPressingRight(tick);
    }

    @Override
    public void jump(int tick) {
        input.setJumping(tick);
    }

    @Override
    public void sneak(int tick) {
        input.setSneaking(tick);
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
        if (mc.getCurrentServerEntry() == null && mc.world == null)
            ConnectScreen.connect(null, mc, ServerAddress.parse(address), null);
    }

    @Override
    public void sendChatMessage(String message) {
        hs.sendMessage(message);
    }
}
