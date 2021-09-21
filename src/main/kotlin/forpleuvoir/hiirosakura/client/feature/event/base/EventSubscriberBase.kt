package forpleuvoir.hiirosakura.client.feature.event.base

import com.google.common.collect.Lists
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.task.TimeTask
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser.parse
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.StringUtil.tooLongOmitted
import java.util.*

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event.base
 *
 * #class_name EventBase
 *
 * #create_time 2021-07-28 18:25
 */
class EventSubscriberBase {
    @JvmField
    @SerializedName("name")
    var name: String? = null

    @JvmField
    @Transient
    val eventType: String

    @JvmField
    @SerializedName("enabled")
    var enabled = false

    /**
     * TimeTask json字符串
     */
    @JvmField
    @SerializedName("timeTask")
    var timeTask: String? = null

    constructor(name: String?, timeTask: String?, eventType: String) {
        this.name = name
        this.timeTask = timeTask
        this.eventType = eventType
        enabled = true
    }

    constructor(eventType: String, obj: JsonObject) {
        this.eventType = eventType
        fromJson(obj)
    }

    val widgetHoverLines: List<String>
        get() {
            val timeTask = getTimeTask()
            return Lists.newArrayList(
                String.format("%s : §b%s", "name", timeTask.data.name),
                String.format("%s : %s", "startTime", timeTask.data.startTime),
                String.format("%s : %s", "cycles", timeTask.data.cycles),
                String.format("%s : %s", "cyclesTime", timeTask.data.cyclesTime),
                String.format(
                    "§b%s : §6%s", "script",
                    tooLongOmitted(timeTask.executorAsString.replace("(\\n)".toRegex(), "⏎"), 45, "...", false)
                )
            )
        }

    fun getTimeTask(): TimeTask {
        return parse(JsonUtil.parseToJsonObject(timeTask), null)
    }

    val script: String
        get() {
            return JsonUtil.parseToJsonObject(timeTask)["script"].asString
        }

    fun toggleEnable() {
        HiiroSakuraData.HIIRO_SAKURA_EVENTS.toggleEnabled(this)
    }

    fun toJsonObject(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("name", name)
        obj.addProperty("enabled", enabled)
        obj.addProperty("timeTask", timeTask)
        return obj
    }

    private fun fromJson(obj: JsonObject) {
        name = obj["name"].asString
        enabled = obj["enabled"].asBoolean
        timeTask = obj["timeTask"].asString
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as EventSubscriberBase
        return enabled == that.enabled && name == that.name && eventType == that.eventType && timeTask == that.timeTask
    }

    override fun hashCode(): Int {
        return Objects.hash(name, eventType, enabled, timeTask)
    }
}