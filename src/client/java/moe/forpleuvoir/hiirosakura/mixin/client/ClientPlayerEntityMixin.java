package moe.forpleuvoir.hiirosakura.mixin.client;


import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "showsDeathScreen", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$showDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!GamePlay.INSTANCE.getAutoRebirth().getValue());
    }

}
