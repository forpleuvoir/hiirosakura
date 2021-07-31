package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.event.OnDisconnectedEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 连接丢失界面
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinDisconnectedScreen
 * <p>#create_time 2021/8/1 0:28
 */
@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Screen parent, Text title, Text reason, CallbackInfo ci
    ) {
        EventBus.broadcast(
                new OnDisconnectedEvent(ServerInfoUtil.getName(), ServerInfoUtil.getAddress(), title.getString(),
                                        reason.getString()));
    }
}
