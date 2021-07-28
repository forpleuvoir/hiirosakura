package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.feature.event.OnDisconnectEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnDisconnectedEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnGameJoinEvent;
import forpleuvoir.hiirosakura.client.feature.event.OnServerJoinEvent;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MOD事件集合
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.base
 * <p>#class_name HiiroSakuraEvents
 * <p>#create_time 2021-07-23 13:44
 */
public class HiiroSakuraEvents extends AbstractHiiroSakuraData {
    private static transient final HSLogger log = HSLogger.getLogger(HiiroSakuraEvents.class);
    public static final Map<String, Class<? extends Event>> events = ImmutableMap.of(
            "OnGameJoin", OnGameJoinEvent.class,
            "OnServerJoin", OnServerJoinEvent.class,
            "OnDisconnected", OnDisconnectedEvent.class,
            "OnDisconnect", OnDisconnectEvent.class
    );

    /**
     * key:事件名
     * <p>value:订阅该事件 的task json数据 集合
     */
    private final Map<String, Map<String, String>> data = new ConcurrentHashMap<>();


    public static String getEventType(Class<? extends Event> eventType) {
        for (Map.Entry<String, Class<? extends Event>> next : events.entrySet()) {
            if (next.getValue().equals(eventType)) {
                return next.getKey();
            }
        }
        return null;
    }

    public HiiroSakuraEvents() {
        super("event");
    }

    @Nullable
    public Map<String,String> getEventData(String eventName){
        return data.get(eventName);
    }

    public void unsubscribe(EventBase eventBase){

    }

    public void subscriber(Class<? extends Event> eventType, String json) {
        subscriber(getEventType(eventType), json);
    }

    public void subscriber(String eventType, String json) {
        TimeTask timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(json), null);
        if (data.containsKey(eventType)) {
            data.get(eventType).put(timeTask.getName(), json);
        } else {
            data.put(eventType, Map.of(timeTask.getName(), json));
        }
        this.onValueChanged();
    }

    public void unsubscribe(Class<? extends Event> eventType, String name) {
        unsubscribe(getEventType(eventType), name);
    }

    public void unsubscribe(String eventType, String name) {
        if (data.containsKey(eventType))
            data.get(eventType).remove(name);
        EventBus.unsubscribeRunAsTimeTask(events.get(eventType), name);
        this.onValueChanged();
    }

    public void sync() {
        EventBus.clearRunAsTimeTask();
        data.forEach((k, v) ->
                             v.forEach((name, task) ->
                                               EventBus.subscribeRunAsTimeTask(events.get(k), task)
                             )
        );
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, Map<String, String>> saveData = JsonUtil.gson
                        .fromJson(object, new TypeToken<Map<String, Map<String, String>>>() {
                        }.getType());
                this.data.clear();
                this.data.putAll(saveData);
                sync();
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
