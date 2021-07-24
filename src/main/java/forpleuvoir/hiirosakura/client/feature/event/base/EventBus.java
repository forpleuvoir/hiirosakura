package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.ImmutableMap;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 事件总线
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name EventBus
 * <p>#create_time 2021-07-23 13:18
 */
public class EventBus {
    private static transient final HSLogger log = HSLogger.getLogger(EventBus.class);
    private transient static final ConcurrentHashMap<Class<? extends Event>, HashMap<String, Consumer<? extends Event>>>
            eventListeners = new ConcurrentHashMap<>();

    public static <E extends Event> void broadcast(E event) {
        event.broadcastHandle(getEventListeners());
    }

    public static <E extends Event> void subscribe(Class<E> channel, String listenerName, Consumer<E> listener) {
        log.info("事件订阅({},{})", channel.getSimpleName(),listenerName);
        if (eventListeners.containsKey(channel)) {
            if (!eventListeners.get(channel).containsKey(listenerName)) {
                eventListeners.get(channel).put(listenerName, listener);
            }
        } else {
            HashMap<String, Consumer<? extends Event>> hashMap = new HashMap<>();
            hashMap.put(listenerName, listener);
            eventListeners.put(channel, hashMap);
        }
    }

    public static <E extends Event> void subscribeRunAsTimeTask(Class<E> channel, String json) {
        var jsonObject = JsonUtil.parseToJsonObject(json);
        subscribe(channel, jsonObject.get("name").getAsString(), e ->
                TimeTaskHandler.getInstance().addTask(TimeTaskParser.parse(JsonUtil.parseToJsonObject(e.handlerJsonStr(json))))
        );
    }

    /**
     * 清空所有订阅事件
     */
    public static void clear() {
        eventListeners.clear();
    }

    public static <E extends Event> void unsubscribe(Class<E> channel, String listenerName) {
        log.info("事件退订({}:{})", channel.getSimpleName(),listenerName);
        if (eventListeners.containsKey(channel)) {
            eventListeners.get(channel).remove(listenerName);
        }
    }

    private static ImmutableMap<Class<? extends Event>, ImmutableMap<String, Consumer<? extends Event>>> getEventListeners() {
        var builder = new ImmutableMap.Builder<Class<? extends Event>, ImmutableMap<String, Consumer<? extends Event>>>();
        eventListeners.forEach((k, v) -> {
            var listenerBuilder = new ImmutableMap.Builder<String, Consumer<? extends Event>>();
            v.forEach(listenerBuilder::put);
            builder.put(k, listenerBuilder.build());
        });
        return builder.build();
    }
}
