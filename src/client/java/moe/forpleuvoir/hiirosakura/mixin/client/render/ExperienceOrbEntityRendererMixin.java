package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.DropEntityRenderAddon;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class ExperienceOrbEntityRendererMixin extends EntityRenderer<ExperienceOrbEntity, ExperienceOrbEntityRenderState> {

    protected ExperienceOrbEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/ExperienceOrbEntity;Lnet/minecraft/client/render/entity/state/ExperienceOrbEntityRenderState;F)V", at = @At("RETURN"))
    public void hiirosakura$updateRenderState(ExperienceOrbEntity experienceOrbEntity, ExperienceOrbEntityRenderState experienceOrbEntityRenderState, float f, CallbackInfo ci) {
        DropEntityRenderAddon.setCurrentExperienceOrbEntity(experienceOrbEntity);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/ExperienceOrbEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("RETURN")
    )
    public void hiirosakura$render(
            ExperienceOrbEntityRenderState experienceOrbEntityRenderState,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int i,
            CallbackInfo ci,
            @Local(name = "q") int red,
            @Local(name = "s") int blue
    ) {
        if (vertexConsumerProvider instanceof VertexConsumerProvider.Immediate && DropEntityRenderAddon.getCurrentExperienceOrbEntity() != null) {
            DropEntityRenderAddon.renderExperienceOrbValue(new Color(red, 255, blue).getRGB(), DropEntityRenderAddon.getCurrentExperienceOrbEntity(), getTextRenderer(), dispatcher, matrixStack, (VertexConsumerProvider.Immediate) vertexConsumerProvider, i);
        }
    }

}
