package moe.forpleuvoir.hiirosakura.mixin.client;


import com.mojang.authlib.GameProfile;
import moe.forpleuvoir.hiirosakura.functional.event.events.PlayerDeathEvent;
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoSwitchElytra;
import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {

    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "showsDeathScreen", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$showDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!GamePlay.INSTANCE.getAutoRebirth().getValue());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void hiirosakura$tick(CallbackInfo callbackInfo) {
        PlayerDeathEvent.setDead(isDead());
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;checkGliding()Z"))
    public boolean hiirosakura$tickMovement(ClientPlayerEntity instance) {
        var state = instance.checkGliding();
        if (!state && !instance.isOnGround()) {
            AutoSwitchElytra.trySwitchElytra(instance);
            return instance.checkGliding();
        }
        return state;
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"))
    public void hiirosakura$tickMovement(CallbackInfo ci) {
        var player = (ClientPlayerEntity) (Object) this;
        if (!player.isGliding()) {
            AutoSwitchElytra.trySwitchChestplate(player);
        }
    }

}
