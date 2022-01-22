package forpleuvoir.hiirosakura.client.mixin;

import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.chatbubble.HiiroSakuraChatBubble;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家渲染器注入
 *
 * @author forpleuvoir
 * <p>项目名 hiirosakura
 * <p>包名 forpleuvoir.hiirosakura.client.mixin
 * <p>文件名 MixinPlayerEntityRenderer
 * <p>创建时间 2021/6/12 23:54
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends EntityRenderer<AbstractClientPlayerEntity> {
	protected MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Inject(method = "render*", at = @At("HEAD"))
	public void render(
			AbstractClientPlayerEntity abstractClientPlayerEntity,
			float f,
			float g,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			CallbackInfo callbackInfo
	) {
		if (Configs.Toggles.CHAT_BUBBLE.getValue())
			HiiroSakuraChatBubble.render(abstractClientPlayerEntity, this.dispatcher, matrixStack);
	}
}
