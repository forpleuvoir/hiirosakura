package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 玩家实体
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinPlayerEvent
 * <p>#create_time 2021-07-27 19:04
 */
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEvent {

    @Inject(method = "shouldCancelInteraction", at = @At("RETURN"), cancellable = true)
    public void shouldCancelInteraction(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Configs.Toggles.DISABLE_BLOCK_INTERACTION.getBooleanValue()) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }
}
