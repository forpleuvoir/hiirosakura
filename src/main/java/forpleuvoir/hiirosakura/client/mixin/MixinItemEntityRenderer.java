package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.util.TextRenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 物品实体渲染器注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinItemEntityRenderer
 * <p>#create_time 2021/6/13 12:41
 */
@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer extends EntityRenderer<ItemEntity> {
    protected MixinItemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo callbackInfo
    ) {
        //渲染距离相机实体 50(单位未知) 之内的的实体
        if (this.dispatcher.getSquaredDistanceToCamera(itemEntity) > 50) {
            return;
        }
        LiteralText text = new LiteralText("");
        text.append(itemEntity.getStack().getName());
        text.append(String.format(" %d", itemEntity.getStack().getCount()));
        TextRenderUtil.renderEntityText(itemEntity, text, this.dispatcher, getTextRenderer(), matrixStack,
                                        vertexConsumerProvider, light
        );
    }
}
