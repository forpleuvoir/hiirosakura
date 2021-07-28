package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final Map<String, List<EventSubscriberBase>> datas = new ConcurrentHashMap<>();

    @Nullable
    private EventSubscriberBase selected;

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
    public EventSubscriberBase getSelected() {
        return this.selected;
    }

    public boolean isSelected(EventSubscriberBase eventSubscriberBase) {
        return eventSubscriberBase.equals(selected);
    }

    public void setSelected(@Nullable EventSubscriberBase eventSubscriberBase) {
        this.selected = eventSubscriberBase;
    }

    public void unsubscribe(EventSubscriberBase eventBase) {
        unsubscribe(eventBase.eventType, eventBase.name);
    }

    public void subscribe(Class<? extends Event> eventType, String json) {
        subscribe(getEventType(eventType), json);
    }

    public void subscribe(String eventType, String json) {
        TimeTask timeTask = TimeTaskParser.parse(JsonUtil.parseToJsonObject(json), null);
        var subscriber = new EventSubscriberBase(timeTask.getName(), json, eventType);
        if (datas.containsKey(eventType)) {
            datas.get(eventType).removeIf(eventSubscriberBase -> eventSubscriberBase.name.equals(subscriber.name));
            datas.get(eventType).add(subscriber);
        } else {
            datas.put(eventType, Lists.newArrayList(subscriber));
        }
        this.onValueChanged();
    }

    public void unsubscribe(Class<? extends Event> eventType, String name) {
        unsubscribe(getEventType(eventType), name);
    }

    public void unsubscribe(String eventType, String name) {
        if (datas.containsKey(eventType)) {
            datas.get(eventType).removeIf(eventSubscriberBase -> eventSubscriberBase.name.equals(name));
        }
        this.onValueChanged();
    }

    public boolean isEnabled(Class<? extends Event> eventType, String name) {
        AtomicBoolean returnValue = new AtomicBoolean(false);
        Optional<EventSubscriberBase> first = datas.get(getEventType(eventType)).stream()
                                                   .filter(eventSubscriberBase -> eventSubscriberBase.name.equals(name))
                                                   .findFirst();
        first.ifPresent(eventSubscriberBase -> returnValue.set(eventSubscriberBase.enabled));
        return returnValue.get();
    }

    public Collection<EventSubscriberBase> getAllEventSubscriberBase() {
        var list = new ArrayList<EventSubscriberBase>();
        datas.forEach((k, v) -> {
            list.addAll(v);
        });
        return list;
    }

    public void toggleEnabled(EventSubscriberBase eventSubscriberBase) {
        datas.get(eventSubscriberBase.eventType).stream().filter(eventSubscriberBase::equals).findFirst()
             .ifPresent(eventSubscriberBase1 -> {
                 eventSubscriberBase1.enabled = !eventSubscriberBase1.enabled;
             });
        this.onValueChanged();
    }

    public void sync() {
        EventBus.clearRunAsTimeTask();
        datas.forEach((k, v) -> {
            v.forEach(eventSubscriberBase -> {
                if (eventSubscriberBase.enabled)
                    EventBus.subscribeRunAsTimeTask(events.get(eventSubscriberBase.eventType),
                                                    eventSubscriberBase.timeTask
                    );
            });
        });
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, List<JsonObject>> saveData = JsonUtil.gson
                        .fromJson(object, new TypeToken<Map<String, List<JsonObject>>>() {
                        }.getType());
                datas.clear();
                saveData.forEach((k, v) -> {
                    var list = new ArrayList<EventSubscriberBase>();
                    for (JsonObject jsonObject : v) {
                        list.add(new EventSubscriberBase(k, jsonObject));
                    }
                    datas.put(k, list);
                });
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
        return JsonUtil.gson.toJsonTree(datas);
    }


}
