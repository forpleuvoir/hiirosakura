package forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.feature.common.javascript.JsRunner
import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask
import forpleuvoir.hiirosakura.client.feature.timertask.executor.IExecutor
import forpleuvoir.hiirosakura.client.util.JsonUtil.ifNullOr

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor

 * 文件名 JsExecutor

 * 创建时间 2022/1/19 12:50

 * @author forpleuvoir

 */
class JsExecutor(script: String, params: MutableMap<String, Any> = HashMap()) : JsRunner(script, params), IExecutor {

	override fun execute(task: TimerTask) {
		params["_task"] = task
		run()
	}

	override val asString: String
		get() = script

	companion object {
		@JvmStatic
		fun fromJson(json: JsonObject, params: MutableMap<String, Any> = HashMap()): TimerTask? {
			return try {
				val name = json.ifNullOr("name", "timer_task")
				val delay = json.ifNullOr("delay", 0).toInt()
				val period = json.ifNullOr("period", 1).toInt()
				val times = json.ifNullOr("times", 1).toInt()
				val executor = JsExecutor(json["executor"].asString, params)
				TimerTask(name, delay, period, times, executor)
			} catch (e: Exception) {
				null
			}
		}
	}
}