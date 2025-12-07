package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.GammaOverride;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    @ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F",ordinal = 1))
    public float hiirosakura$update(float original) {
        if (GammaOverride.INSTANCE.getEnable().getValue()) {
            return GammaOverride.INSTANCE.getGamma();
        }
        return original;
    }

}
