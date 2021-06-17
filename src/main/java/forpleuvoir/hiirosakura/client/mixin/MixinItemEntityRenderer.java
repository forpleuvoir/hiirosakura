package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.util.ItemStackUtil;
import forpleuvoir.hiirosakura.client.util.TextRenderUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

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
        if (this.dispatcher.getSquaredDistanceToCamera(itemEntity) > Configs.Values.ITEM_ENTITY_TEXT_RENDER_DISTANCE
                .getDoubleValue()) {
            return;
        }
        var texts = new LinkedList<Text>();
        //渲染附魔
        if (Configs.Toggles.SHOW_ITEM_ENTITY_ENCHANTMENT.getBooleanValue())
            texts.addAll(ItemStackUtil.getEnchantmentsWithLvl(itemEntity.getStack()));
        //渲染工具提示
        if (Configs.Toggles.SHOW_TOOLTIP_ON_ITEM_ENTITY.getBooleanValue())
            texts.addAll(HiiroSakuraDatas.TOOLTIP.getTooltip(itemEntity.getStack()));
        LiteralText text = new LiteralText("");
        if (Configs.Toggles.SHOW_ITEM_ENTITY_NAME.getBooleanValue())
            text.append(itemEntity.getStack().getName());
        if (itemEntity.getStack().getCount() > 1 && Configs.Toggles.SHOW_ITEM_ENTITY_COUNT.getBooleanValue())
            text.append(String.format(" %d ", itemEntity.getStack().getCount()));
        texts.add(text);
        TextRenderUtil.renderEntityMultiText(itemEntity, texts, this.dispatcher, getTextRenderer(), matrixStack,
                                             vertexConsumerProvider, light
        );
    }
}
