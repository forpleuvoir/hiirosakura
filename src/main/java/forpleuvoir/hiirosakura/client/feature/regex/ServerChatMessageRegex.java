package forpleuvoir.hiirosakura.client.feature.regex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 聊天消息正则匹配
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.regex
 * <p>#class_name ServerChatMessageRegex
 * <p>#create_time 2021/6/23 23:53
 */
public class ServerChatMessageRegex extends AbstractHiiroSakuraData {
    private transient static final HSLogger log = HSLogger.getLogger(ServerChatMessageRegex.class);
    private final Map<String, String> regex = new HashMap<>();
    public static String VANILLA_REGEX = Configs.Values.CHAT_MESSAGE_DEFAULT_REGEX.getStringValue();


    public ServerChatMessageRegex() {
        super("serverChatMessageRegex");
    }

    public boolean put(String regexString) {
        if (getAddress() == null) {
            log.warn("{}获取服务器地址失败", this.getName());
            return false;
        }
        regex.put(getAddress(), regexString);
        this.onValueChanged();
        return true;
    }

    public boolean remove() {
        if (getAddress() == null) {
            log.warn("{}获取服务器地址失败", this.getName());
            return false;
        }
        regex.remove(getAddress());
        return true;
    }

    public String getRegex() {
        String regex;
        regex = this.regex.get(getAddress());
        if (StringUtil.isEmpty(regex)) {
            regex = getDefRegex();
        }
        return regex;
    }

    public ChatMessageRegex chatMessageRegex(Text text) {
        return ChatMessageRegex.getInstance(text, getRegex());
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, String> data = JsonUtil.gson.fromJson(object, new TypeToken<Map<String, String>>() {
                }.getType());
                regex.clear();
                regex.putAll(data);
            } else {
                log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element);
            }
        } catch (Exception e) {
            log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        return JsonUtil.gson.toJsonTree(regex);
    }

    public static String getAddress() {
        try {
            return Objects.requireNonNull(MinecraftClient.getInstance().getCurrentServerEntry()).address;
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public static String getDefRegex() {
        return VANILLA_REGEX;
    }
}
