package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity;
import forpleuvoir.hiirosakura.client.feature.event.DoAttackEvent;
import forpleuvoir.hiirosakura.client.feature.event.DoItemPickEvent;
import forpleuvoir.hiirosakura.client.feature.event.DoItemUseEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnDisconnectEvent;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus;
import forpleuvoir.hiirosakura.client.util.ServerInfoUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MinecraftClient注入
 * <p>{@link #init(CallbackInfo)} 关闭游戏再带教程
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinMinecraftClient
 * <p>#create_time 2021/6/11 21:02
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        //关闭游戏自带教程
        tutorialManager.setStep(TutorialStep.NONE);
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void doAttack(CallbackInfo callbackInfo) {
        EventBus.broadcast(new DoAttackEvent());
        if (SwitchCameraEntity.INSTANCE.isSwitched()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    public void doItemPick(CallbackInfo callbackInfo) {
        EventBus.broadcast(new DoItemPickEvent());
        if (SwitchCameraEntity.INSTANCE.isSwitched()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    public void doItemUse(CallbackInfo callbackInfo) {
        EventBus.broadcast(new DoItemUseEvent());
        if (SwitchCameraEntity.INSTANCE.isSwitched()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    public void disconnect(Screen screen, CallbackInfo callbackInfo) {
        EventBus.broadcast(new OnDisconnectEvent(ServerInfoUtil.getName(),ServerInfoUtil.getAddress()));
        ServerInfoUtil.clear();
    }


}
