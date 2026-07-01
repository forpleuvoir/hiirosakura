package moe.forpleuvoir.hiirosakura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.config.HSConfig;
import moe.forpleuvoir.hiirosakura.functional.event.events.*;
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher;
import moe.forpleuvoir.hiirosakura.functional.gameplay.Gliding;
import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemUseIntercept;
import moe.forpleuvoir.hiirosakura.functional.misc.PickPlayerHead;
import moe.forpleuvoir.hiirosakura.functional.misc.ServerMarker;
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSHitResult;
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack;
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Final
    private Tutorial tutorial;

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(GameConfig gameConfig, CallbackInfo ci) {
        tutorial.setStep(HSConfig.INSTANCE.getTutorialStep());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStart(CallbackInfo ci) {
        HSTickTaskScheduler.INSTANCE.startTick((Minecraft) (Object) this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickEnd(CallbackInfo ci) {
        HSTickTaskScheduler.INSTANCE.endTick((Minecraft) (Object) this);
    }

    @Inject(method = "disconnect*", at = @At("HEAD"))
    public void disconnect(Screen screen, boolean keepResourcePacks, CallbackInfo ci) {
        var context = new GameExitContext(ServerMarker.getName(), ServerMarker.getAddress());
        HSEvents.GameExit.invoker().invoke(context);
        ServerMarker.clear();
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    public void startAttack(CallbackInfoReturnable<Boolean> cir) {
        assert hitResult != null;
        var context = new PlayerAttackContext(HSHitResult.fromHitResult(hitResult));
        HSEvents.PlayerAttack.invoker().invoke(context);
        if (context.isCancelled() || CameraSwitcher.getShouldBlockActions()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "pickBlockOrEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z"), cancellable = true)
    public void pickBlock(CallbackInfo callbackInfo) {
        assert hitResult != null;
        var context = new PlayerPickContext(HSHitResult.fromHitResult(hitResult));
        HSEvents.PlayerPick.invoker().invoke(context);
        if (context.isCancelled() || CameraSwitcher.getShouldBlockActions()) callbackInfo.cancel();
    }

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"), cancellable = true)
    public void startUseItem(CallbackInfo callbackInfo, @Local(name = "hand") InteractionHand hand, @Local(name = "heldItem") ItemStack heldItem) {
        assert hitResult != null;
        if (!ItemUseIntercept.canUse(BlockInfo.getTargetBlockInfoOrEmpty(), heldItem)) {
            callbackInfo.cancel();
            return;
        }
        var context = new PlayerUseContext(HSHitResult.fromHitResult(hitResult), new HSItemStack(heldItem));
        HSEvents.PlayerUse.invoker().invoke(context);
        assert player != null;
        var cancelFireworkRocket = Gliding.fireworkRocketInteractionWhenGliding(player, hand, heldItem);

        if (context.isCancelled() || CameraSwitcher.getShouldBlockActions() || cancelFireworkRocket)
            callbackInfo.cancel();
    }


    @Inject(method = "pickBlockOrEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePickItemFromEntity(Lnet/minecraft/world/entity/Entity;Z)V"))
    public void pickPlayerHead(CallbackInfo ci, @Local(name = "entityHitResult") EntityHitResult entityHitResult) {
        PickPlayerHead.pickPlayerHead(entityHitResult.getEntity());
    }

}
