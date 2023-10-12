package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.event.events.DisconnectEvent;
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
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinDisconnectedScreen
 * <p>创建时间 2021/8/1 0:28
 */
@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen {

    @Inject(method = "<init>*", at = @At("RETURN"))
	public void init(Screen parent, Text title, Text reason, CallbackInfo ci) {
		ServerInfoUtil.clear();
		new DisconnectEvent(
				ServerInfoUtil.getLastServerName(),
				ServerInfoUtil.getLastServerAddress(),
				title.getString(),
				reason.getString()
		).broadcast();
	}

}
