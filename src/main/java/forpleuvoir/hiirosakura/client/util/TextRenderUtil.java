package forpleuvoir.hiirosakura.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.awt.*;
import java.util.List;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name TextRenderUtil
 * <p>#create_time 2021/6/13 12:43
 */
public class TextRenderUtil {

    public static int ageColor(final float age, final float maxAge) {
        if (age < 0) return new Color(0, 0, 0).getRGB();
        int green = (int) ((((maxAge - age) / maxAge)) * 255);
        int red = (int) ((1f - ((maxAge - age) / maxAge)) * 255);
        return new Color(red, green, 0).getRGB();
    }

    public static int ageColor(final int age) {
        return ageColor(age, 6000);
    }


    /**
     * 在实体上方渲染文本
     *
     * @param entity                 目标实体
     * @param text                   需要渲染的文本
     * @param dispatcher             {@link EntityRenderDispatcher}
     * @param textRenderer           {@link TextRenderer}
     * @param matrixStack            {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light                  亮度
     */
    public static void renderEntityText(Entity entity, Text text, EntityRenderDispatcher dispatcher,
                                        TextRenderer textRenderer,
                                        MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                        int light
    ) {
        renderEntityText(entity, 0, text, dispatcher, textRenderer,
                         matrixStack, vertexConsumerProvider, light
        );
    }


    /**
     * 在实体上方渲染文本
     *
     * @param entity                 目标实体
     * @param textHeight             高度
     * @param text                   需要渲染的文本
     * @param dispatcher             {@link EntityRenderDispatcher}
     * @param textRenderer           {@link TextRenderer}
     * @param matrixStack            {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light                  亮度
     */
    public static void renderEntityText(Entity entity, double textHeight, Text text, EntityRenderDispatcher dispatcher,
                                        TextRenderer textRenderer,
                                        MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                        int light
    ) {
        double height = entity.getHeight() + 0.5f + textHeight;
        matrixStack.push();
        matrixStack.translate(0.0D, height, 0.0D);
        matrixStack.multiply(dispatcher.getRotation());
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

    /**
     * 在实体上方渲染多行文本
     *
     * @param entity                 目标实体
     * @param text                   需要渲染的文本列表
     * @param dispatcher             {@link EntityRenderDispatcher}
     * @param textRenderer           {@link TextRenderer}
     * @param matrixStack            {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light                  亮度
     */
    public static void renderEntityMultiText(Entity entity, List<Text> text, EntityRenderDispatcher dispatcher,
                                             TextRenderer textRenderer,
                                             MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                             int light
    ) {
        int textRows = text.size();
        double height = textRows * 0.25f;
        for (Text item : text) {
            height -= 0.25f;
            renderEntityText(entity, height, item, dispatcher, textRenderer,
                             matrixStack, vertexConsumerProvider, light
            );
        }
    }

}
