package forpleuvoir.hiirosakura.client.feature.task.executor.base;


import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
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
    private static final HiiroSakuraClient hs = HiiroSakuraClient.getINSTANCE();

    @Override
    public void doAttack() {
        ((MixinMinecraftClientInterface) mc).callDoAttack();
    }

    @Override
    public void itemUse() {
        ((MixinMinecraftClientInterface) mc).callDoItemUse();
    }

    @Override
    public void joinServer(String address) {
        ConnectScreen.connect(null, mc, ServerAddress.parse(address), null);
    }

    @Override
    public void sendChatMessage(String message) {
        hs.sendMessage(message);
    }
}
