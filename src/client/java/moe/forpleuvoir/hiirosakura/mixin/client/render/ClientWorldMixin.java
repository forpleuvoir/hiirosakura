package moe.forpleuvoir.hiirosakura.mixin.client.render;

import moe.forpleuvoir.hiirosakura.functional.renderaddons.BlockMarkerRenderAddon;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "getBlockParticle", at = @At("HEAD"), cancellable = true)
    public void hiirosakura$getBlockParticle(CallbackInfoReturnable<Block> cir) {
        var block = BlockMarkerRenderAddon.getShowBlockMarker();
        if (block != null) cir.setReturnValue(block);
    }

}
