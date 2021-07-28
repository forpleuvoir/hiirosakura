package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 从服务器断开连接事件 包括主动退出服务器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnDisconnectEvent
 * <p>#create_time 2021-07-26 16:08
 */
public class OnDisconnectEvent extends Event {
    @Nullable
    public final String name;
    @Nullable
    public final String address;

    public OnDisconnectEvent(@Nullable String name, @Nullable String address) {
        this.name = name;
        this.address = address;
    }

}
