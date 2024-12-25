package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.config.HSConfig;
import moe.forpleuvoir.hiirosakura.functional.misc.PickPlayerHead;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.hiirosakura.functional.script.CommonApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void hiirosakura$init(RunArgs args, CallbackInfo ci) {
        tutorialManager.setStep(HSConfig.INSTANCE.getTutorialStep());
    }

    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickItemFromEntity(Lnet/minecraft/entity/Entity;Z)V"))
    public void hiirosakura$doItemPickPlayerHead(CallbackInfo ci, @Local(ordinal = 0) EntityHitResult result) {
        PickPlayerHead.pickPlayerHead(result.getEntity());
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void hiirosakura$disconnect(Screen disconnectionScreen, CallbackInfo ci) {
        ServerMarker.clear();
    }
}
