package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 当从服务器断开连接
 * 不包括主动退出服务器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnDisconnectedEvent
 * <p>#create_time 2021/7/25 21:31
 */
public class OnDisconnectedEvent extends Event {
    @Nullable
    public final String name;
    @Nullable
    public final String address;

    public OnDisconnectedEvent(@Nullable String name, @Nullable String address) {
        this.name = name;
        this.address = address;
    }


}
