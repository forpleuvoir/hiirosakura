package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.DropEntityRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.ExperienceOrbRenderStateAccessor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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
    public void extractRenderState(ExperienceOrb entity, ExperienceOrbRenderState state, float partialTicks, CallbackInfo ci) {
        ((ExperienceOrbRenderStateAccessor) state).hiirosakura$setExperienceOrb(entity);
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/ExperienceOrbRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("RETURN")
    )
    public void submit(
            ExperienceOrbRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo ci,
            @Local(name = "rc") int rc,
            @Local(name = "bc") int bc
    ) {
        var orb = ((ExperienceOrbRenderStateAccessor) state).hiirosakura$getExperienceOrb();
        if (orb != null) {
            DropEntityRenderAddon.renderExperienceOrbValue((0xFF << 24) | (rc << 16) | (0xFF << 8) | bc, orb, state, camera, poseStack, submitNodeCollector);
        }
    }

}
