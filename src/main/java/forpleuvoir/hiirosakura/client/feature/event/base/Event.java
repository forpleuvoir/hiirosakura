package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.ImmutableMap;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.util.function.Consumer;

/**
 * 事件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.base
 * <p>#class_name Event
 * <p>#create_time 2021-07-23 13:25
 */
public abstract class Event {
    public final HiiroSakuraClient hs = HiiroSakuraClient.getInstance();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void broadcastHandle(
            ImmutableMap<Class<? extends Event>, ImmutableMap<String, Consumer<? extends Event>>> eventListeners
    ) {
        if (eventListeners.containsKey(this.getClass())) {
            for (Consumer value : eventListeners.get(this.getClass()).values()) {
                value.accept(this);
            }
        }
    }
}
