package forpleuvoir.hiirosakura.client.feature.task

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap

/**
 * 定时任务处理器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task
 *
 * #class_name TimeTaskHandler
 *
 * #create_time 2021/7/22 23:00
 */
class TimeTaskHandler {

	private val timeTaskHashMap: ConcurrentMap<String, TimeTask> = ConcurrentHashMap()
	private val removeList = ConcurrentLinkedQueue<String>()
	val keys: Set<String>
		get() {
			val strings: MutableSet<String> = HashSet()
			timeTaskHashMap.keys.forEach { strings.add("\"$it\"") }
			return strings
		}

	fun addTask(name: String, task: TimeTask) {
		timeTaskHashMap[name] = task
	}

	fun addTask(task: TimeTask) {
		timeTaskHashMap[task.name] = task
	}

	fun removeTask(name: String) {
		removeList.add(name)
	}

	fun clear() {
		timeTaskHashMap.clear()
	}

	private fun handle() {
		removeHandler()
		val iterator: MutableIterator<Map.Entry<String, TimeTask>> = timeTaskHashMap.entries.iterator()
		while (iterator.hasNext()) {
			val value = iterator.next().value
			if (value.isOver) {
				iterator.remove()
			} else {
				value.executes()
			}
		}
	}

	private fun removeHandler() {
		removeList.forEach { timeTaskHashMap.remove(it) }
		removeList.clear()
	}

	companion object {
		private val hs = HiiroSakuraClient

		@JvmStatic
		var INSTANCE: TimeTaskHandler? = null
			private set

		fun initialize() {
			INSTANCE = TimeTaskHandler()
			hs.addTickHandler {
				INSTANCE!!.handle()
			}
		}
	}
}