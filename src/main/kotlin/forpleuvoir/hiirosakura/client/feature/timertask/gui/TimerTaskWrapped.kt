package forpleuvoir.hiirosakura.client.feature.timertask.gui

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.feature.common.Runnable
import forpleuvoir.hiirosakura.client.feature.timertask.Timer
import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask
import forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JsExecutor
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 TimerTaskWrapped

 * 创建时间 2022/1/21 12:15

 * @author forpleuvoir

 */
open class TimerTaskWrapped : Runnable {
	private val log = HSLogger.getLogger(javaClass)

	lateinit var timerTask: TimerTask
	var runAt: RunAt = RunAt.StartTick

	override fun run(params: Map<String, Any>) {
		if (this::timerTask.isInitialized) {
			val executor = timerTask.executor as JsExecutor
			executor.putAll(params)
			when (runAt) {
				RunAt.StartTick -> Timer.scheduleStart(TimerTask(timerTask, executor))
				RunAt.EndTick   -> Timer.scheduleEnd(TimerTask(timerTask, executor))
			}
		}
	}

	override val asString: String
		get() {
			return if (this::timerTask.isInitialized)
				timerTask.getExecutorAsString()
			else super.asString
		}

	override val asJsonElement: JsonElement
		get() = JsonObject().apply {
			add("timerTask", timerTask.asJsonElement)
			addProperty("runAt", runAt.name)
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		try {
			if (jsonElement.isJsonObject) {
				jsonElement.asJsonObject.let {
					timerTask = JsExecutor.fromJson(it["timerTask"].asJsonObject)!!
					runAt = RunAt.valueOf(it["runAt"].asString)
				}
			} else {
				log.warn(IbukiGourdLang.SetFromJsonFailed.tString("TimerTaskWrapped", jsonElement))
			}
		} catch (e: Exception) {
			log.warn(IbukiGourdLang.SetFromJsonFailed.tString("TimerTaskWrapped", jsonElement))
			log.error(e)
		}
	}

	enum class RunAt {
		StartTick, EndTick
	}
}