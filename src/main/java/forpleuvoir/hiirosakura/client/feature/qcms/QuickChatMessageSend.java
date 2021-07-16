package forpleuvoir.hiirosakura.client.feature.qcms;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import net.minecraft.text.*;

import java.util.*;

/**
 * 快捷发送聊天消息
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.qcms
 * <p>#class_name QuickChatMessageSend
 * <p>#create_time 2021/6/16 22:12
 */
public class QuickChatMessageSend extends AbstractHiiroSakuraData {
    private transient static final HSLogger log = HSLogger.getLogger(QuickChatMessageSend.class);
    private final Map<String, String> datas = new HashMap<>();

    public QuickChatMessageSend() {
        super("quick_chat_message_send");
    }

    public boolean add(String remark, String messageStr) {
        if (datas.containsKey(remark)) return false;
        return put(remark, messageStr);
    }

    public Set<String> getKeySet() {
        Set<String> set = new HashSet<>();
        datas.keySet().forEach(key -> set.add(String.format("\"%s\"", key)));
        return set;
    }

    public boolean put(String remark, String messageStr) {
        if (getKeyLength(remark) < 1) return false;
        datas.put(remark, messageStr);
        this.onValueChanged();
        return true;
    }

    public void remove(String remark) {
        if (datas.remove(remark) != null)
            this.onValueChanged();
    }

    public void reset(String oldRemark, String newRemark, String newValue) {
        if (oldRemark == null || newRemark == null || newValue == null) return;
        if (datas.containsKey(oldRemark)) {
            if (!oldRemark.equals(newRemark)) {
                rename(oldRemark, newRemark);
            }
            this.put(newRemark, newValue);
        }
    }

    public void rename(String oldRemark, String newRemark) {
        if (datas.containsKey(oldRemark)) {
            String value = datas.get(oldRemark);
            datas.remove(oldRemark);
            datas.put(newRemark, value);
            this.onValueChanged();
        }
    }

    public LinkedList<QuickChatMessage> getTextDatas() {
        LinkedList<QuickChatMessage> sortedData = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.getSortedData();
        HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.getUnSortedData().forEach(sortedData::addLast);
        return sortedData;
    }

    public Map<String, String> getDatas() {
        return this.datas;
    }

    public Text getAsText() {
        if (datas.isEmpty()) {
            return new TranslatableText(String.format("%s.feature.qcms.data.empty", HiiroSakuraClient.MOD_ID));
        }
        MutableText text = new LiteralText("");
        Iterator<QuickChatMessage> iterator = getTextDatas().iterator();
        while (iterator.hasNext()) {
            QuickChatMessage next = iterator.next();
            text.append(
                    Texts.bracketed(
                            new LiteralText("")
                                    .append(next.remark().replace("&", "§"))
                                    .styled(style ->
                                                    style.withClickEvent(new ClickEvent(
                                                            ClickEvent.Action.RUN_COMMAND, next.message()))
                                                         .withHoverEvent(new HoverEvent(
                                                                 HoverEvent.Action.SHOW_TEXT,
                                                                 new LiteralText(next.message())
                                                         ))
                                    )
                    )
            );
            if (iterator.hasNext())
                text.append(new LiteralText(",  "));
        }
        return text;
    }

    public static int getKeyLength(String str) {
        return str.replaceAll("(&.)", "").length();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, String> data = JsonUtil.gson.fromJson(object, new TypeToken<Map<String, String>>() {
                }.getType());
                datas.clear();
                datas.putAll(data);
            } else {
                log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element);
            }
        } catch (Exception e) {
            log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        return JsonUtil.gson.toJsonTree(datas);
    }
}
