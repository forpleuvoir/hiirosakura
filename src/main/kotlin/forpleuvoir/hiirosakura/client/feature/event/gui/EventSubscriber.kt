package forpleuvoir.hiirosakura.client.feature.event.gui

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.feature.common.Runnable
import forpleuvoir.hiirosakura.client.feature.common.javascript.JsRunner
import forpleuvoir.hiirosakura.client.feature.timertask.gui.TimerTaskWrapped
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.event.Event
import forpleuvoir.ibuki_gourd.event.events.Events
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang

/**
 * 事件订阅者

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 EventSubscriber

 * 创建时间 2022/1/21 15:16

 * @author forpleuvoir

 */
class EventSubscriber : Runnable {
	private val log = HSLogger.getLogger(this.javaClass)

	lateinit var name: String

	lateinit var event: Class<out Event>

	var enabled: Boolean = true

	lateinit var runnerType: RunnerType

	lateinit var runner: Runnable

	override fun run(params: Map<String, Any>) {
		if (enabled)
			runner.run(params)
	}

	override val asString: String
		get() {
			return if (this::runner.isInitialized)
				runner.asString
			else super.asString
		}

	override val asJsonElement: JsonElement
		get() = JsonObject().apply {
			addProperty("name", name)
			addProperty("event", event.simpleName)
			addProperty("enabled", enabled)
			addProperty("runnerType", runnerType.name)
			add("runner", runner.asJsonElement)
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		try {
			if (jsonElement.isJsonObject) {
				jsonElement.asJsonObject.let {
					name = it["name"].asString
					event = Events.getEventByName(it["event"].asString)!!
					enabled = it["enabled"].asBoolean
					runnerType = RunnerType.valueOf(it["runnerType"].asString)
					runner = when (runnerType) {
						RunnerType.TimerTask -> TimerTaskWrapped().apply { setValueFromJsonElement(it["runner"].asJsonObject) }
						RunnerType.JsRunner  -> JsRunner.fromJson(it["runner"].asJsonPrimitive)
					}
				}
			} else {
				log.warn(IbukiGourdLang.SetFromJsonFailed.tString("EventSubscriber", jsonElement))
			}
		} catch (e: Exception) {
			log.warn(IbukiGourdLang.SetFromJsonFailed.tString("EventSubscriber", jsonElement))
			log.error(e)
		}
	}

	enum class RunnerType {
		TimerTask, JsRunner
	}
}