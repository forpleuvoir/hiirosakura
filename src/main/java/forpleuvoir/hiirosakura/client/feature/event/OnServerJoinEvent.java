package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;

/**
 * 加入服务器事件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnServerJoinEvent
 * <p>#create_time 2021-07-26 13:43
 */
public class OnServerJoinEvent extends Event {

    public final String name;
    public final String address;

    public OnServerJoinEvent(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
