package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.util.TextRenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * TNT实体渲染器注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.mixin
 * <p>#class_name MixinTntEntityRenderer
 * <p>#create_time 2021/6/13 14:08
 */
@Mixin(TntEntityRenderer.class)
public abstract class MixinTntEntityRenderer extends EntityRenderer<TntEntity> {

    protected MixinTntEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(TntEntity tntEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int light,
                       CallbackInfo callbackInfo
    ) {
        if (!Configs.Toggles.SHOW_TNT_FUSE.getBooleanValue()) return;
        TextRenderUtil.renderEntityText(tntEntity, new LiteralText(String.format("tick:%d", tntEntity.getFuse())),
                                        this.dispatcher, getTextRenderer(), matrixStack,
                                        vertexConsumerProvider, light
        );
    }
}
