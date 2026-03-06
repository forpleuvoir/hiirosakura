package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.fuse.TntFuseRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntRenderer.class)
public abstract class TntRendererMixin extends EntityRenderer<PrimedTnt, TntRenderState> {

    protected TntRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/TntRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    public void render(TntRenderState tntRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        TntFuseRenderer.renderTntFuse(tntRenderState, this.entityRenderDispatcher,packedLight, poseStack, bufferSource);
    }
}
