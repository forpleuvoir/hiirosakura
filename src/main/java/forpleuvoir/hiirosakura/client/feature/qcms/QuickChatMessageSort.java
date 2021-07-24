package forpleuvoir.hiirosakura.client.feature.qcms;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * 快捷聊天消息发送排序
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.qcms
 * <p>#class_name QuickChatMessageSort
 * <p>#create_time 2021/7/16 23:36
 */
public class QuickChatMessageSort extends AbstractHiiroSakuraData {
    private transient static final HSLogger log = HSLogger.getLogger(QuickChatMessageSort.class);

    private final Map<String, Integer> data = new HashMap<>();

    public QuickChatMessageSort() {
        super("quick_chat_message_sort");
    }

    public void setSort(String remark, @NotNull Integer level) {
        data.put(remark, level);
        this.onValueChanged();
    }

    public void remove(String key) {
        if (data.remove(key) != null)
            this.onValueChanged();
    }

    /**
     * 获取排序等级
     *
     * @param remark 对应备注
     * @return 排序等级 未被排序时返回null
     */
    @Nullable
    public Integer getLevel(String remark) {
        return data.getOrDefault(remark, null);
    }

    public void resetLevel(String oldRemark, String newRemark, Integer level) {
        if (oldRemark == null || newRemark == null) return;
        if (data.containsKey(oldRemark)) {
            data.remove(oldRemark);
            if (level != null)
                this.setSort(newRemark, level);
            else this.onValueChanged();
        } else {
            if (level != null)
                this.setSort(newRemark, level);
        }
    }

    /**
     * @return {@link LinkedList<QuickChatMessage>}
     * @see #getSortedData(boolean)
     */
    public LinkedList<QuickChatMessage> getSortedData() {
        return getSortedData(true);
    }

    /**
     * 获取排序后的QCMS数据
     *
     * @param isReversed 是由有小到大排序
     * @return {@link LinkedList<QuickChatMessage>}
     */
    public LinkedList<QuickChatMessage> getSortedData(boolean isReversed) {
        var list = new LinkedList<QuickChatMessage>();
        Map<String, String> datas = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getData();
        if (!datas.isEmpty()) {
            Collection<Integer> values = this.data.values();
            Stream<Integer> sorted = values.stream().sorted();
            if (!values.isEmpty()) {
                sorted.forEach(value -> this.data.forEach((k, v) -> {
                    if (v.equals(value)) {
                        var quickChatMessage = new QuickChatMessage(k, datas.get(k), this.data.get(k));
                        if (!list.contains(quickChatMessage)) {
                            if (isReversed)
                                list.addLast(quickChatMessage);
                            else
                                list.addFirst(quickChatMessage);
                        }
                    }
                }));
            }
        }

        return list;
    }

    /**
     * 获取未排序的数据
     *
     * @return {@link LinkedList<QuickChatMessage>}
     */
    public LinkedList<QuickChatMessage> getUnSortedData() {
        var list = new LinkedList<QuickChatMessage>();
        Map<String, String> datas = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.getData();
        Set<String> keySet = this.data.keySet();
        Set<String> set = datas.keySet();
        set.stream().filter(s -> !keySet.contains(s))
           .forEach(key -> list.addLast(new QuickChatMessage(key, datas.get(key), null)));
        return list;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, Integer> data = JsonUtil.gson.fromJson(object, new TypeToken<Map<String, Integer>>() {
                }.getType());
                this.data.clear();
                this.data.putAll(data);
            } else {
                log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element);
            }
        } catch (Exception e) {
            log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        return JsonUtil.gson.toJsonTree(data);
    }


}
