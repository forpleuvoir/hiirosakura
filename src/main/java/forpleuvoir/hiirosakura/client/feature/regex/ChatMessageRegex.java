package forpleuvoir.hiirosakura.client.feature.regex;

import net.minecraft.text.Text;

import java.util.regex.Pattern;

/**
 * 聊天消息正则匹配
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.regex
 * <p>#class_name ChatMessageRegex
 * <p>#create_time 2021/6/23 23:36
 */
public class ChatMessageRegex {
    public static final String NAME = "name";
    public static final String MESSAGE = "message";
    private String name;
    private String message;

    public static ChatMessageRegex getInstance(Text text, String regex) {
        if (text != null && regex != null)
            return new ChatMessageRegex(text, regex);
        return null;
    }

    private ChatMessageRegex(Text text, String regex) {
        String content = preString(text);
        Pattern pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(content);
        if (matcher.find()) {
            try {
                this.name = matcher.group(NAME);
                this.message = matcher.group(MESSAGE);
            } catch (Exception ignore) {
            }
        }
    }

    public String getPlayerName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }


    private String preString(Text text) {
        return text.getString().replaceAll("(§.)", "");
    }
}
