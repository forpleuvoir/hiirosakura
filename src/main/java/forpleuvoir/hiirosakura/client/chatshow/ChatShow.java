package forpleuvoir.hiirosakura.client.chatshow;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static forpleuvoir.hiirosakura.client.chatshow.HiiroSakuraChatShow.CHAT_SHOW_CONFIG;

/**
 * 聊天消息显示
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.chatshow
 * <p>#class_name ChatShow
 * <p>#create_time 2021/6/12 21:47
 */
public class ChatShow {
    private transient static final Logger log = LoggerFactory.getLogger(ChatShow.class);
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
     * @param player                 玩家
     * @param dispatcher             {@link EntityRenderDispatcher}
     * @param textRenderer           {@link TextRenderer}
     * @param matrixStack            {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light                  亮度
     */
    public void render(AbstractClientPlayerEntity player, EntityRenderDispatcher dispatcher, TextRenderer textRenderer,
                       MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light
    ) {
        if (timer <= HiiroSakuraClient.getTrackingTick()) {
            HiiroSakuraChatShow.INSTANCE.remove(uuid);
            return;
        }
        if (!player.getUuid().equals(uuid)) {
            return;
        }
        List<Text> texts = textHandler(text);
        int textRows = texts.size();
        double height = textRows * CHAT_SHOW_CONFIG.lineSpacing;
        for (Text text1 : texts) {
            height -= CHAT_SHOW_CONFIG.lineSpacing;
            renderText(height, text1, player, dispatcher, textRenderer, matrixStack, vertexConsumerProvider, light);
        }
    }

    /**
     * 渲染单行文本
     *
     * @param textHeight             文本高度
     * @param text                   文本
     * @param player                 玩家
     * @param dispatcher             {@link EntityRenderDispatcher}
     * @param textRenderer           {@link TextRenderer}
     * @param matrixStack            {@link MatrixStack}
     * @param vertexConsumerProvider {@link VertexConsumerProvider}
     * @param light                  亮度
     */
    public void renderText(double textHeight, Text text, AbstractClientPlayerEntity player,
                           EntityRenderDispatcher dispatcher,
                           TextRenderer textRenderer,
                           MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light
    ) {
        //渲染高度
        double height = player.getHeight() + CHAT_SHOW_CONFIG.height + textHeight;
        matrixStack.push();
        matrixStack.translate(0.0D, height, 0.0D);
        matrixStack.multiply(dispatcher.getRotation());
        //缩放
        Vec3f scale = CHAT_SHOW_CONFIG.scale;
        matrixStack.scale(-scale.getX(), -scale.getY(), scale.getZ());
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float h = (float) (-textRenderer.getWidth(text) / 2);
        textRenderer.draw(text, h, 0f, CHAT_SHOW_CONFIG.textColor, true, matrix4f, vertexConsumerProvider, false,
                          CHAT_SHOW_CONFIG.backgroundColor, light
        );
        matrixStack.pop();

    }

    /**
     * 处理文本
     * 多字符换行并去掉玩家名
     * @param text 原文本
     * @return 处理之后的文本 {@link List}
     */
    private List<Text> textHandler(Text text) {
        List<Text> list = new LinkedList<>();
        try {
            Object message = ((TranslatableText) text).getArgs()[1];
            for (String s : StringUtil.strSplit((String) message, CHAT_SHOW_CONFIG.width)) {
                list.add(new LiteralText(s));
            }
            return list;
        }catch (Exception ignored){}

        String content = text.getString().replaceFirst(CHAT_SHOW_CONFIG.playerNameRegex, "");
        for (String s : StringUtil.strSplit(content, CHAT_SHOW_CONFIG.width)) {
            list.add(new LiteralText(s));
        }
        return list;
    }

}
