package forpleuvoir.hiirosakura.client.feature.task

import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.feature.event.base.Event
import forpleuvoir.hiirosakura.client.feature.task.executor.base.ExecutorParser

/**
 * 定时任务解析器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task
 *
 * #class_name TimeTaskParser
 *
 * #create_time 2021-07-23 15:42
 */
object TimeTaskParser {
	@JvmStatic
	fun parse(obj: JsonObject, event: Event?): TimeTask {
		val startTime = if (obj["startTime"] != null) obj["startTime"].asInt else 0
		val cycles = if (obj["cycles"] != null) obj["cycles"].asInt else 1
		val cyclesTime = if (obj["cyclesTime"] != null) obj["cyclesTime"].asInt else 0
		val name = obj["name"].asString
		val timeTaskData = TimeTaskData(name, startTime, cycles, cyclesTime)
		val executor = ExecutorParser.parse(obj["script"].asString, event)
		return TimeTask(executor, timeTaskData)
	}
}