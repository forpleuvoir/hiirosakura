package moe.forpleuvoir.hiirosakura.mixin.client;


import com.mojang.authlib.GameProfile;
import moe.forpleuvoir.hiirosakura.functional.event.events.PlayerDeathEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoSwitchElytra;
import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay;
import moe.forpleuvoir.hiirosakura.functional.gameplay.ItemDropIntercept;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "shouldShowDeathScreen", at = @At("HEAD"), cancellable = true)
    public void shouldShowDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!GamePlay.INSTANCE.getAutoRebirth().getValue());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo callbackInfo) {
        PlayerDeathEvent.setDead(isDeadOrDying());
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;tryToStartFallFlying()Z"))
    public boolean aiStep(LocalPlayer instance) {
        var state = instance.tryToStartFallFlying();
        if (!state && !instance.onGround() && !instance.isHandsBusy() && !instance.isUnderWater() && !instance.getAbilities().flying) {
            AutoSwitchElytra.trySwitchElytra(instance);
            return instance.tryToStartFallFlying();
        }
        return state;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    public void aiStep(CallbackInfo ci) {
        var player = (LocalPlayer) (Object) this;
        if (!player.isFallFlying()) {
            AutoSwitchElytra.trySwitchChestplate(player);
        }
    }

    @Inject(method = "drop", at = @At(value = "HEAD"), cancellable = true)
    public void drop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = this.getMainHandItem();
        if (!ItemDropIntercept.canDrop(itemStack)) {
            cir.setReturnValue(false);
        }
    }

}
