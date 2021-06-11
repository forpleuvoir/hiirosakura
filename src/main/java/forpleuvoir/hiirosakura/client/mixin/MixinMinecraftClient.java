package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.client.MinecraftClient;
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
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinMinecraftClient
 * <p>#create_time 2021/6/11 21:02
 */
@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Final private TutorialManager tutorialManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        //关闭游戏自带教程
        tutorialManager.setStep(TutorialStep.NONE);
    }

}
