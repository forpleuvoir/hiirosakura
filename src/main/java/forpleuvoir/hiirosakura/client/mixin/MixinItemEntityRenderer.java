package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData;
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

import static forpleuvoir.hiirosakura.client.config.Configs.Toggles.*;

/**
 * 物品实体渲染器注入
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinItemEntityRenderer
 * <p>创建时间 2021/6/13 12:41
 */
@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer extends EntityRenderer<ItemEntity> {
    protected MixinItemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render*", at = @At("RETURN"))
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo callbackInfo
    ) {
		if (this.dispatcher.getSquaredDistanceToCamera(itemEntity) > Configs.Values.ITEM_ENTITY_TEXT_RENDER_DISTANCE
				.getValue()) {
			return;
		}
		var texts = new LinkedList<Text>();
		//渲染附魔
		if (SHOW_ITEM_ENTITY_ENCHANTMENT.getValue())
			texts.addAll(ItemStackUtil.getEnchantmentsWithLvl(itemEntity.getStack()));

		//渲染工具提示
		if (SHOW_TOOLTIP_ON_ITEM_ENTITY.getValue())
			texts.addAll(HiiroSakuraData.TOOLTIP.getTooltip(itemEntity.getStack()));

		//渲染名字
		LiteralText text = new LiteralText("");
		if (SHOW_ITEM_ENTITY_NAME.getValue())
			text.append(itemEntity.getStack().getName()).formatted(itemEntity.getStack().getRarity().formatting);
		

		//渲染数量
		if (itemEntity.getStack().getCount() > 1 && SHOW_ITEM_ENTITY_COUNT.getValue())
			text.append(String.format(" %d ", itemEntity.getStack().getCount()));
		if (!text.getString().equals(""))
			texts.add(text);

		//渲染剩余生命
		if (SHOW_ENTITY_AGE.getValue()) {
			int maxAge = 6000;
			int age = maxAge - itemEntity.getItemAge();
			texts.add(TextRenderUtil.ageColorText(String.valueOf(age / 20), age, maxAge, false)
					.append("§rs")
			);
		}

		if (!texts.isEmpty())
			TextRenderUtil.renderEntityMultiText(itemEntity, texts, this.dispatcher, getTextRenderer(), matrixStack,
					vertexConsumerProvider, light
			);
    }
}
