package forpleuvoir.hiirosakura.client.feature.chatmessage;

import net.minecraft.text.Text;

import java.util.regex.Pattern;

import static forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_FILTER_REGEX;

/**
 * 聊天消息过滤
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.chatmessage
 * <p>#class_name ChatMessageFilter
 * <p>#create_time 2021/7/17 12:13
 */
public class ChatMessageFilter {
    public static final ChatMessageFilter INSTANCE = new ChatMessageFilter();

    public boolean needToFilter(Text message) {
        boolean matched = false;
        for (String regex : CHAT_MESSAGE_FILTER_REGEX.getStrings()) {
            if (Pattern.matches(regex, message.getString())) {
                matched = true;
            }
        }
        return matched;
    }
}
