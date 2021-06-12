package forpleuvoir.hiirosakura.client.chatshow;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 聊天消息显示
 * <p>玩家发送聊天消息时 会在头上显示文本内容
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.chatshow
 * <p>#class_name HiiroSakuraChatShow
 * <p>#create_time 2021/6/12 21:01
 */
public class HiiroSakuraChatShow {
    private transient static final Logger log = LoggerFactory.getLogger(HiiroSakuraChatShow.class);
    public static HiiroSakuraChatShow INSTANCE;
    public static ChatShowConfig CHAT_SHOW_CONFIG;
    private final Map<UUID, ChatShow> chatShows = new ConcurrentHashMap<>();
    private final Queue<UUID> removeList = new ConcurrentLinkedQueue<>();


    /**
     * 初始化
     */
    public static void initialize() {
        INSTANCE = new HiiroSakuraChatShow();
        CHAT_SHOW_CONFIG = new ChatShowConfig();
    }

    public void addChatShow(Text text, UUID uuid) {
        this.chatShows.put(uuid, new ChatShow(text, uuid, CHAT_SHOW_CONFIG.time));
    }

    public void render(AbstractClientPlayerEntity player, EntityRenderDispatcher dispatcher,
                       TextRenderer textRenderer,
                       MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light
    ) {
        removeList.forEach(chatShows::remove);
        removeList.clear();
        chatShows.forEach((uuid, chatShow) -> chatShow
                .render(player, dispatcher, textRenderer, matrixStack, vertexConsumerProvider, light));
    }

    public void remove(UUID uuid) {
        removeList.add(uuid);
    }


}
