package forpleuvoir.hiirosakura.client.feature.task

import com.google.common.collect.Lists
import forpleuvoir.hiirosakura.client.util.StringUtil


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.task

 * 文件名 TimeTaskBase

 * 创建时间 2021/8/23 22:07

 * @author forpleuvoir

 */
class TimeTaskBase(var timeTask: TimeTask, var sort: Int) {
	var name: String = timeTask.name
	val script: String
		get() {
			return timeTask.executorAsString
		}

	val widgetHoverLines: List<String>
		get() {
			return Lists.newArrayList(
				"name : ${timeTask.name}",
				"startTime : ${timeTask.data.startTime}",
				"cycles : ${timeTask.data.cycles}",
				"cyclesTime : ${timeTask.data.cyclesTime}",
				"§bscript §r: §6${
					StringUtil.tooLongOmitted(
						script.replace("(\\n)".toRegex(), "⏎"),
						45,
						"...",
						false
					)
				}"
			)
		}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as TimeTaskBase

		if (timeTask != other.timeTask) return false
		if (sort != other.sort) return false
		if (name != other.name) return false

		return true
	}

	override fun hashCode(): Int {
		var result = timeTask.hashCode()
		result = 31 * result + sort
		result = 31 * result + name.hashCode()
		return result
	}


}
