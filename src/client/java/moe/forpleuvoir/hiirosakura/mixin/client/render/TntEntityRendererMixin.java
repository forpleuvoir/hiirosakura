package moe.forpleuvoir.hiirosakura.mixin.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static moe.forpleuvoir.hiirosakura.functional.renderaddons.TntFuseKt.renderTntFuse;

@Mixin(TntEntityRenderer.class)
public abstract class TntEntityRendererMixin extends EntityRenderer<TntEntity, TntEntityRenderState> {

    protected TntEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/TntEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (vertexConsumerProvider instanceof VertexConsumerProvider.Immediate)
            renderTntFuse(tntEntityRenderState,getTextRenderer(),  matrixStack, (VertexConsumerProvider.Immediate) vertexConsumerProvider, i);
    }
}
