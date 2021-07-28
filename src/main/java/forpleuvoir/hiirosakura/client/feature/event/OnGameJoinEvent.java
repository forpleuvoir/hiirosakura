package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 游戏加入事件
 * 本地游戏加入时不会有名称和地址
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnGameJoinEvent
 * <p>#create_time 2021-07-23 13:42
 */
public class OnGameJoinEvent extends Event {
    /**
     * 服务器名称
     */
    @Nullable
    public final String name;
    /**
     * 服务器地址
     */
    @Nullable
    public final String address;

    public OnGameJoinEvent(@Nullable String name, @Nullable String address) {
        this.name = name;
        this.address = address;
    }

}
