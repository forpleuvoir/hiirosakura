package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.config.HSConfig;
import moe.forpleuvoir.hiirosakura.functional.event.events.GameExitEvent;
import moe.forpleuvoir.hiirosakura.functional.event.events.PlayerAttackEvent;
import moe.forpleuvoir.hiirosakura.functional.event.events.PlayerPickEvent;
import moe.forpleuvoir.hiirosakura.functional.event.events.PlayerUseEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher;
import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay;
import moe.forpleuvoir.hiirosakura.functional.misc.PickPlayerHead;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack;
import moe.forpleuvoir.nebula.event.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    protected abstract Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor);

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
        if (event.getCanceled() || CameraSwitcher.getShouldBlockActions()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;hasControlDown()Z"), cancellable = true)
    public void hiirosakura$doItemPick(CallbackInfo callbackInfo) {
        assert crosshairTarget != null;
        var event = new PlayerPickEvent(HSHitResult.fromHitResult(crosshairTarget));
        EventBus.Companion.broadcast(event);
        if (event.getCanceled() || CameraSwitcher.getShouldBlockActions()) callbackInfo.cancel();
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"), cancellable = true)
    public void hiirosakura$doItemUse(CallbackInfo callbackInfo, @Local(ordinal = 0) Hand hand, @Local(ordinal = 0) ItemStack stack) {
        assert crosshairTarget != null;
        var event = new PlayerUseEvent(HSHitResult.fromHitResult(crosshairTarget), new HSItemStack(stack));
        EventBus.Companion.broadcast(event);
        assert player != null;
        var cancelFireworkRocket = GamePlay.fireworkRocketInteractionWhenGliding(player, hand, stack);

        if (event.getCanceled() || CameraSwitcher.getShouldBlockActions() || cancelFireworkRocket)
            callbackInfo.cancel();
    }


    @Inject(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickItemFromEntity(Lnet/minecraft/entity/Entity;Z)V"))
    public void hiirosakura$doItemPickPlayerHead(CallbackInfo ci, @Local(ordinal = 0) EntityHitResult result) {
        PickPlayerHead.pickPlayerHead(result.getEntity());
    }


}
