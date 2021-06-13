package forpleuvoir.hiirosakura.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name TextRenderUtil
 * <p>#create_time 2021/6/13 12:43
 */
public class TextRenderUtil {

    /**
     * 在实体上方渲染文本
     * @param entity 目标实体
     * @param text 需要渲染的文本
     * @param dispatcher {@link EntityRenderDispatcher}
     * @param textRenderer {@link TextRenderer}
     * @param matrixStack {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light 亮度
     */
    public static void renderEntityText(Entity entity, Text text, EntityRenderDispatcher dispatcher,
                                        TextRenderer textRenderer,
                                        MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                        int light
    ) {
        //渲染高度
        matrixStack.push();
        matrixStack.translate(0.0D, entity.getHeight() + 0.5F, 0.0D);
        matrixStack.multiply(dispatcher.getRotation());
        //缩放
        matrixStack.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int backgroundColor = (int) (g * 255.0F) << 24;
        float x = (float) (-textRenderer.getWidth(text) / 2);
        textRenderer.draw(text, x, 0, -1, false, matrix4f, vertexConsumerProvider, false,
                          backgroundColor, light
        );
        matrixStack.pop();
    }

}
