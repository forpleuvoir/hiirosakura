package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.config.HSConfig;
import moe.forpleuvoir.hiirosakura.functional.event.events.*;
import moe.forpleuvoir.hiirosakura.functional.misc.PickPlayerHead;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockHitResult;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void hiirosakura$init(RunArgs args, CallbackInfo ci) {
        tutorialManager.setStep(HSConfig.INSTANCE.getTutorialStep());
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void hiirosakura$disconnect(Screen disconnectionScreen, CallbackInfo ci) {
        EventBus.Companion
            .broadcast(
                new GameExitEvent(ServerMarker.getName(), ServerMarker.getAddress())
            );
        ServerMarker.clear();
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void hiirosakura$doAttack(CallbackInfoReturnable<Boolean> cir) {
        assert crosshairTarget != null;
        var event = new PlayerAttackEvent(HSHitResult.fromHitResult(crosshairTarget));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"), cancellable = true)
    public void hiirosakura$doItemPick(CallbackInfo callbackInfo) {
        assert crosshairTarget != null;
        var event = new PlayerPickEvent(HSHitResult.fromHitResult(crosshairTarget));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) callbackInfo.cancel();
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"), cancellable = true)
    public void hiirosakura$doItemUse(CallbackInfo callbackInfo, @Local(ordinal = 0) ItemStack stack) {
        assert crosshairTarget != null;
        var event = new PlayerUseEvent(HSHitResult.fromHitResult(crosshairTarget), new HSItemStack(stack));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) callbackInfo.cancel();
    }


    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickItemFromEntity(Lnet/minecraft/entity/Entity;Z)V"))
    public void hiirosakura$doItemPickPlayerHead(CallbackInfo ci, @Local(ordinal = 0) EntityHitResult result) {
        PickPlayerHead.pickPlayerHead(result.getEntity());
    }

    @Inject(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
    public void hiirosakura$handleBlockBreaking(boolean breaking, CallbackInfo ci, @Local(ordinal = 0) BlockHitResult hitResult) {
        var event = new BlockBreakingEvent(new HSBlockHitResult(hitResult));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled()) ci.cancel();
    }
}
