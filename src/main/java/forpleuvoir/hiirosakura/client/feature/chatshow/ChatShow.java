package forpleuvoir.hiirosakura.client.feature.chatshow;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.awt.image.RenderedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static forpleuvoir.hiirosakura.client.feature.chatshow.HiiroSakuraChatShow.CHAT_SHOW_CONFIG;

/**
 * 聊天消息显示
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.chatshow
 * <p>#class_name ChatShow
 * <p>#create_time 2021/6/12 21:47
 */
public class ChatShow {
    public static final Identifier BACKGROUND_TEXTURE = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                       "texture/gui/feature/chatshow/bubble.png"
    );

    /**
     * 文本
     */
    private final Text text;
    /**
     * 发送者的UUID
     */
    private final UUID uuid;
    /**
     * 显示时间
     */
    private final long timer;


    public ChatShow(Text text, UUID uuid, int time) {
        this.text = text;
        this.uuid = uuid;
        timer = HiiroSakuraClient.getTrackingTick() + time;
    }

    /**
     * 渲染文本
     *
     * @param player       玩家
     * @param dispatcher   {@link EntityRenderDispatcher}
     * @param textRenderer {@link TextRenderer}
     * @param matrixStack  {@link MatrixStack}
     */
    public void render(AbstractClientPlayerEntity player, EntityRenderDispatcher dispatcher, TextRenderer textRenderer,
                       MatrixStack matrixStack
    ) {
        if (timer <= HiiroSakuraClient.getTrackingTick()) {
            HiiroSakuraChatShow.INSTANCE.remove(uuid);
            return;
        }
        if (!player.getUuid().equals(uuid)) {
            return;
        }
        int maxWidth = 90;
        var list = textHandler(textRenderer, maxWidth);
        int width = getMaxWidth(textRenderer, maxWidth);
        int spacing = 5;

        int count = list.size();
        int lineSpacing = 3;
        int textHeight = 9;
        int height = getHeight(count, textHeight, lineSpacing);

        matrixStack.push();
        matrixStack.translate(0.0D, player.getHeight() + 1f, 0.0D);
        float scale = -0.05f;
        matrixStack.scale(scale, scale, scale);
        matrixStack.multiply(dispatcher.getRotation());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        DrawableHelper
                .drawTexture(matrixStack, -(width / 2) - spacing, -height - lineSpacing, 0.0F, 0.0F,
                             width + 2 * spacing,
                             height + 2 * lineSpacing, width + 2 * spacing,
                             height + 2 * lineSpacing
                );
        int index = 0;
        for (Text text : list) {
            textRenderer
                    .draw(matrixStack, text, -(int) (width / 2),
                          -height + getHeight(count, textHeight, lineSpacing, index),
                          CHAT_SHOW_CONFIG.textColor
                    );
            index++;
        }
        matrixStack.pop();
    }

    /**
     * 处理文本
     * 多字符换行并去掉玩家名
     *
     * @return 处理之后的文本 {@link List}
     */
    private List<Text> textHandler(TextRenderer textRenderer, int maxWidth) {
        List<Text> list = new LinkedList<>();
        try {
            String message = (String) ((TranslatableText) text).getArgs()[1];
            StringBuilder sb = new StringBuilder();
            for (char c : message.toCharArray()) {
                sb.append(c);
                if (textRenderer.getWidth(sb.toString()) >= maxWidth) {
                    list.add(new LiteralText(sb.toString()));
                    sb = new StringBuilder();
                }
            }
            list.add(new LiteralText(sb.toString()));
            return list;
        } catch (Exception ignored) {
        }

        String content = text.getString().replaceFirst(CHAT_SHOW_CONFIG.playerNameRegex, "");
        for (String s : StringUtil.strSplit(content, CHAT_SHOW_CONFIG.width)) {
            list.add(new LiteralText(s));
        }
        return list;
    }

    private int getMaxWidth(TextRenderer textRenderer, int maxWidth) {
        int max = 0;
        for (Text text : textHandler(textRenderer, maxWidth)) {
            if (max < textRenderer.getWidth(text)) {
                max = textRenderer.getWidth(text);
            }
        }
        return max;
    }

    private int getHeight(int count, int textHeight, int lineSpacing) {
        int height = textHeight;
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                height += textHeight + lineSpacing;
            }
        }
        return height;
    }

    private int getHeight(int count, int textHeight, int lineSpacing, int index) {
        int height = 0;
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                height += textHeight + lineSpacing;
            }
            if (i == index) {
                return height;
            }
        }
        return height;
    }
}
