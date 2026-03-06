package moe.forpleuvoir.hiirosakura.mixin.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.DropEntityRenderAddon;
import moe.forpleuvoir.hiirosakura.functional.renderaddons.ItemEntityRenderStateAccessor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity, ItemEntityRenderState> {

    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V", at = @At("HEAD"))
    public void extractRenderState(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float f, CallbackInfo ci) {
        ((ItemEntityRenderStateAccessor) itemEntityRenderState).hiirosakura$setItemEntity(itemEntity);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.AFTER
            )
    )
    public void render(ItemEntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        var currentItemEntity = ((ItemEntityRenderStateAccessor) renderState).hiirosakura$getItemEntity();
        if (currentItemEntity != null) {
            DropEntityRenderAddon.renderItemEntityInfo(currentItemEntity, renderState, this.entityRenderDispatcher, packedLight, poseStack, bufferSource);
        }
    }
}
