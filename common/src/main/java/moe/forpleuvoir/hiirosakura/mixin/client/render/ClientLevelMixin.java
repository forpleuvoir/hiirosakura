package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.BlockMarkerRenderAddon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "getMarkerParticleTarget", at = @At("HEAD"), cancellable = true)
    public void getMarkerParticleTarget(CallbackInfoReturnable<Block> cir) {
        var block = BlockMarkerRenderAddon.getShowBlockMarker();
        if (block != null) cir.setReturnValue(block);
    }

}
