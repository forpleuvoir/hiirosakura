package forpleuvoir.hiirosakura.client.feature.chatmessage;

import forpleuvoir.hiirosakura.client.config.Configs;

import java.util.regex.Pattern;

import static forpleuvoir.hiirosakura.client.config.Configs.Toggles.*;
import static forpleuvoir.hiirosakura.client.config.Configs.Values.*;

/**
 * 聊天消息注入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.chatmessage
 * <p>#class_name ChatMessageInject
 * <p>#create_time 2021/6/24 22:16
 */
public class ChatMessageInject {
    public static ChatMessageInject INSTANCE = new ChatMessageInject();

    public String handlerMessage(String message) {
        if (message.startsWith("/")) return message;
        //添加前缀以及后缀
        if (needInject(message))
            message = setFix(message);
        return message;
    }

    private boolean needInject(String message) {
        boolean returnValue = true;
        if (ENABLE_CHAT_MESSAGE_INJECT_REGEX.getBooleanValue()) {
            boolean matched = false;
            for (String regex : CHAT_MESSAGE_INJECT_REGEX.getStrings()) {
                if (Pattern.matches(regex, message)) {
                    matched = true;
                }
            }
            if (REVERSE_CHAT_MESSAGE_INJECT_REGEX.getBooleanValue()) matched = !matched;
            returnValue = !matched;
        }
        return returnValue;
    }

    private String setFix(String message) {
        if (Configs.Toggles.CHAT_MESSAGE_INJECT.getBooleanValue())
            return String.format("%s%s%s", CHAT_MESSAGE_INJECT_PREFIX.getStringValue(), message,
                                 CHAT_MESSAGE_INJECT_SUFFIX.getStringValue()
            );
        return message;
    }
}
