package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.GammaOverride;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapRenderStateExtractorMixin {

    @ModifyExpressionValue(method = "extract", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F",ordinal = 0))
    public float hiirosakura$update(float original) {
        if (GammaOverride.INSTANCE.getEnable().getEnabled()) {
            return GammaOverride.INSTANCE.getGamma();
        }
        return original;
    }

}
