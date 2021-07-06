package forpleuvoir.hiirosakura.client.feature.chatshow;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;


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
    private transient static final HSLogger log = HSLogger.getLogger(ChatShow.class);
    public static final Identifier BACKGROUND_TEXTURE1 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/1.png"
    );
    public static final Identifier BACKGROUND_TEXTURE2 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/2.png"
    );
    public static final Identifier BACKGROUND_TEXTURE3 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/3.png"
    );
    public static final Identifier BACKGROUND_TEXTURE4 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/4.png"
    );
    public static final Identifier BACKGROUND_TEXTURE5 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/5.png"
    );
    public static final Identifier BACKGROUND_TEXTURE6 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/6.png"
    );
    public static final Identifier BACKGROUND_TEXTURE7 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/7.png"
    );
    public static final Identifier BACKGROUND_TEXTURE8 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/8.png"
    );
    public static final Identifier BACKGROUND_TEXTURE9 = new Identifier(HiiroSakuraClient.MOD_ID,
                                                                        "texture/gui/feature/chatshow/bubbles/9.png"
    );

    public static final List<Identifier> TEXTURES = ImmutableList.of(
            BACKGROUND_TEXTURE1, BACKGROUND_TEXTURE2, BACKGROUND_TEXTURE3, BACKGROUND_TEXTURE4, BACKGROUND_TEXTURE5,
            BACKGROUND_TEXTURE6, BACKGROUND_TEXTURE8, BACKGROUND_TEXTURE9
    );

    /**
     * 文本
     */
    private final String text;
    /**
     * 显示时间
     */
    private final long timer;
    private final List<Text> list;
    private final TextRenderer textRenderer;
    private final String playerName;

    public static ChatShow getInstance(ChatMessageRegex chatMessageRegex) {
        if (chatMessageRegex == null) return null;
        try {
            return new ChatShow(chatMessageRegex.getMessage(), chatMessageRegex.getPlayerName());
        } catch (Exception ignored) {
            return null;
        }
    }

    public ChatShow(String text, String playerName) {
        this.text = text;
        this.playerName = playerName;
        this.timer = HiiroSakuraClient.getTickCounter() + Configs.Values.CHAT_SHOW_TIME.getIntegerValue();
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.list = textHandler(Configs.Values.CHAT_SHOW_TEXT_MAX_WIDTH.getIntegerValue());
    }

    /**
     * 渲染文本
     *
     * @param player      玩家
     * @param dispatcher  {@link EntityRenderDispatcher}
     * @param matrixStack {@link MatrixStack}
     */
    public void render(AbstractClientPlayerEntity player, EntityRenderDispatcher dispatcher, MatrixStack matrixStack
    ) {
        if (timer <= HiiroSakuraClient.getTickCounter()) {
            HiiroSakuraChatShow.INSTANCE.remove(playerName);
            return;
        }
        if (playerName != null) {
            if (!player.getEntityName().equals(playerName)) return;
        } else return;
        int width = getMaxWidth();
        int count = list.size();
        int lineSpacing = 4;
        int textHeight = 9;
        int height = getHeight(count, textHeight, lineSpacing);
        matrixStack.push();
        matrixStack.translate(0.0D, player.getHeight() + Configs.Values.CHAT_SHOW_HEIGHT.getDoubleValue(), 0.0D);
        float scale = (-0.025f) * (float) Configs.Values.CHAT_SHOW_SCALE.getDoubleValue();
        matrixStack.scale(scale, scale, scale);
        matrixStack.multiply(dispatcher.getRotation());
        renderBackground(matrixStack, width + 5, height + lineSpacing);
        int index = 0;
        for (Text text : list) {
            textRenderer
                    .draw(matrixStack, text, -(int) (width / 2),
                          -height + getHeight(count, textHeight, lineSpacing, index),
                          Configs.Values.CHAT_SHOW_TEXT_COLOR.getIntegerValue()
                    );
            index++;
        }
        matrixStack.pop();

    }

    private void renderBackground(MatrixStack matrixStack, int width, int height) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderUtil.drawTexture(BACKGROUND_TEXTURE1, matrixStack, -(width / 2) - 12, -height - 12, -1, 0.0F, 0.0F,
                               12, 12, 12, 12
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE2, matrixStack, -(width / 2), -height - 12, -1, 0.0F, 0.0F,
                               width, 12, width, 12
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE3, matrixStack, width / 2, -height - 12, -1, 0.0F, 0.0F,
                               12, 12, 12, 12
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE4, matrixStack, -(width / 2) - 12, -height, -1, 0.0F, 0.0F,
                               12, height, 12, height
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE5, matrixStack, -(width / 2), -height, -1, 0.0F, 0.0F,
                               width, height, width, height
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE6, matrixStack, width / 2, -height, -1, 0.0F, 0.0F,
                               12, height, 12, height
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE7, matrixStack, -(width / 2) - 12, 0, -1, 0.0F, 0.0F,
                               12, 12, 12, 12
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE8, matrixStack, -(width / 2), 0, -1, 0.0F, 0.0F,
                               width, 12, width, 12
        );
        RenderUtil.drawTexture(BACKGROUND_TEXTURE9, matrixStack, width / 2, 0, -1, 0.0F, 0.0F,
                               12, 12, 12, 12
        );
        RenderSystem.disableDepthTest();
    }


    /**
     * 处理文本
     * 多字符换行并去掉玩家名
     *
     * @return 处理之后的文本 {@link List}
     */
    private List<Text> textHandler(int maxWidth) {
        List<Text> list = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (textRenderer.getWidth(sb.toString()) > maxWidth) {
                list.add(new LiteralText(sb.toString()));
                sb = new StringBuilder();
            }
            sb.append(c);
        }
        list.add(new LiteralText(sb.toString()));
        return list;

    }

    private int getMaxWidth() {
        int max = 0;
        for (Text text : list) {
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
        int height = -lineSpacing / 2;
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
