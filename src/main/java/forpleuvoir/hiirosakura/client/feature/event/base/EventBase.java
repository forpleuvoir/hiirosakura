package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.base
 * <p>#class_name EventBase
 * <p>#create_time 2021-07-28 18:25
 */
public class EventBase {

    public String name;
    /**
     * TimeTask json字符串
     */
    public String data;
    public String eventType;
    public boolean enabled;

    public List<String> getWidgetHoverLines() {
        return Lists.newArrayList(
                String.format("%s:§a%s", "name", name),
                String.format("%s:§b%s", "event", eventType),
                String.format("%s:§c%s", "data", data)
        );
    }

    public void toggleEnable() {
        //todo toggleEnable

    }
}
