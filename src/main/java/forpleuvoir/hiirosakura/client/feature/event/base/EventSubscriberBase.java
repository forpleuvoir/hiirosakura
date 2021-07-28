package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;

import java.util.List;
import java.util.Objects;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.base
 * <p>#class_name EventBase
 * <p>#create_time 2021-07-28 18:25
 */
public class EventSubscriberBase {

    @SerializedName("name")
    public String name;
    public transient String eventType;
    @SerializedName("enabled")
    public boolean enabled;
    /**
     * TimeTask json字符串
     */
    @SerializedName("timeTask")
    public String timeTask;

    public EventSubscriberBase(String name, String timeTask, String eventType) {
        this.name = name;
        this.timeTask = timeTask;
        this.eventType = eventType;
        this.enabled = true;
    }


    public EventSubscriberBase(String eventType, JsonObject object) {
        this.eventType = eventType;
        fromJson(object);
    }

    public List<String> getWidgetHoverLines() {
        return Lists.newArrayList(
                String.format("%s : §b%s", "name", name),
                String.format("%s : %s", "event", eventType),
                String.format("%s : %s", "enabled", enabled ? "§a" + true : "§c" + false),
                String.format("§b%s : §6%s", "timeTask", timeTask)
        );
    }

    public void toggleEnable() {
        HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.toggleEnabled(this);
    }

    public JsonObject toJsonObject() {
        var obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("enabled", enabled);
        obj.addProperty("timeTask", timeTask);
        return obj;
    }

    public void fromJson(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.enabled = object.get("enabled").getAsBoolean();
        this.timeTask = object.get("timeTask").getAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSubscriberBase that = (EventSubscriberBase) o;
        return enabled == that.enabled && Objects.equals(name, that.name) && Objects
                .equals(eventType, that.eventType) && Objects.equals(timeTask, that.timeTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, eventType, enabled, timeTask);
    }
}
