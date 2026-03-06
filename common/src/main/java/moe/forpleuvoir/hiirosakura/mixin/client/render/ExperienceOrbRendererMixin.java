package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.DropEntityRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.ExperienceOrbRenderStateAccessor;
import moe.forpleuvoir.nebula.common.color.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbRenderer.class)
public abstract class ExperienceOrbRendererMixin extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {

    protected ExperienceOrbRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/ExperienceOrb;Lnet/minecraft/client/renderer/entity/state/ExperienceOrbRenderState;F)V", at = @At("HEAD"))
    public void extractRenderState(ExperienceOrb entity, ExperienceOrbRenderState reusedState, float partialTick, CallbackInfo ci) {
        ((ExperienceOrbRenderStateAccessor) reusedState).hiirosakura$setExperienceOrb(entity);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/ExperienceOrbRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    public void submit(
            ExperienceOrbRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci, @Local(ordinal = 1) int red, @Local(ordinal = 3) int blue
    ) {
        var orb = ((ExperienceOrbRenderStateAccessor) renderState).hiirosakura$getExperienceOrb();
        if (orb != null) {
            DropEntityRenderAddon.renderExperienceOrbValue(new Color(red, 255, blue, 255, false), orb, renderState, this.entityRenderDispatcher, packedLight, poseStack, bufferSource);
        }
    }

}
