package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.GammaOverride;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {


    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F"))
    public float hiirosakura$update(float original) {
        if (GammaOverride.INSTANCE.getEnable().getValue()) {
            return GammaOverride.INSTANCE.getGamma();
        }
        return original;
    }

}
