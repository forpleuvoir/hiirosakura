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

    private final String name;
    private final String address;

    public OnServerJoinEvent(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String handlerJsonStr(String json) {
        return json.replace("${event.name}", name != null ? name : "${event.name}")
                   .replace("${event.address}", address != null ? address : "${event.address}");
    }
}
