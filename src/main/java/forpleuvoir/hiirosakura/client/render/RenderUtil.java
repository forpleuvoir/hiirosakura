package forpleuvoir.hiirosakura.client.render;


import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name RenderUtil
 * <p>#create_time 2021/6/22 0:10
 */
public class RenderUtil {
    public static void drawTexture(Identifier texture, MatrixStack matrices, int x, int y, int z, float u, float v,
                                   int width, int height, int textureWidth, int textureHeight
    ) {
        RenderSystem.setShaderTexture(0, texture);
        drawTexture(matrices, x, y, z, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(Identifier texture, MatrixStack matrices, int x, int y, float z, int u, int v,
                                   int width, int height
    ) {
        RenderSystem.setShaderTexture(0, texture);
        RenderUtils.drawTexturedRect(x, y, u, v, width, height, z);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height,
                                   int textureWidth, int textureHeight
    ) {
        drawTexture(matrices, x, y, z, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, int width, int height, float u, float v,
                                   int regionWidth, int regionHeight, int textureWidth, int textureHeight
    ) {
        drawTexture(matrices, x, x + width, y, y + height, z, regionWidth, regionHeight, u, v, textureWidth,
                    textureHeight
        );
    }

    private static void drawTexture(MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth,
                                    int regionHeight, float u, float v, int textureWidth, int textureHeight
    ) {
        drawTexturedQuad(matrices.peek().getModel(), x0, y0, x1, y1, z, (u + 0.0F) / (float) textureWidth,
                         (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight,
                         (v + (float) regionHeight) / (float) textureHeight
        );
    }

    private static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1,
                                         float v0, float v1
    ) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        int temp;
        if (left < right) {
            temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }

        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.clearColor(red, green, blue, alpha);
        buffer.begin (VertexFormat.DrawMode.LINES, VertexFormats.POSITION );
        buffer.vertex(left, bottom, 0.0D).next();
        buffer.vertex(right, bottom, 0.0D).next();
        buffer.vertex(right, top, 0.0D).next();
        buffer.vertex(left, top, 0.0D).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
