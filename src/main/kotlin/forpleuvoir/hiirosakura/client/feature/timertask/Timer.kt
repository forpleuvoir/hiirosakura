package forpleuvoir.hiirosakura.client.feature.timertask

import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.event.events.ClientEndTickEvent
import forpleuvoir.ibuki_gourd.event.events.ClientStartTickEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask

 * 文件名 Timer

 * 创建时间 2022/1/19 12:59

 * @author forpleuvoir

 */
object Timer {

	private val startTasks = ConcurrentHashMap<String, TimerTask>()
	private val startRemoveList = ConcurrentLinkedQueue<String>()
	private val endTasks = ConcurrentHashMap<String, TimerTask>()
	private val endRemoveList = ConcurrentLinkedQueue<String>()

	val keys: ArrayList<String>
		get() {
			val list = ArrayList<String>()
			startTasks.forEach {
				list.add("\"${it.value.name}:${it.key}\"")
			}
			endTasks.forEach {
				list.add("\"${it.value.name}:${it.key}\"")
			}
			return list
		}

	init {
		EventBus.subscribe<ClientStartTickEvent> { startTick() }
		EventBus.subscribe<ClientEndTickEvent> { endTick() }
	}

	@JvmStatic
	fun scheduleStart(task: TimerTask) {
		startTasks[task.uuid] = task
	}

	@JvmStatic
	fun scheduleStart(executor: Consumer<TimerTask>) {
		val task = TimerTask(executor)
		startTasks[task.uuid] = task
	}

	@JvmStatic
	fun scheduleEnd(task: TimerTask) {
		endTasks[task.uuid] = task
	}

	@JvmStatic
	fun scheduleEnd(executor: Consumer<TimerTask>) {
		val task = TimerTask(executor)
		endTasks[task.uuid] = task
	}

	@JvmStatic
	fun remove(uuid: String) {
		startRemoveList.add(uuid)
		endRemoveList.add(uuid)
	}

	@JvmStatic
	fun clear() {
		startTasks.clear()
		endTasks.clear()
	}

	private fun endRemoveHandler() {
		endRemoveList.forEach {
			endTasks.remove(it)
		}
		endRemoveList.clear()
	}

	private fun startRemoveHandler() {
		startRemoveList.forEach {
			startTasks.remove(it)
		}
		startRemoveList.clear()
	}


	private fun startTick() {
		startRemoveHandler()
		val iterator = startTasks.iterator()
		while (iterator.hasNext()) {
			val value = iterator.next().value
			if (value.isOver) {
				iterator.remove()
			} else {
				value.execute()
			}
		}
	}

	private fun endTick() {
		endRemoveHandler()
		val iterator = endTasks.iterator()
		while (iterator.hasNext()) {
			val value = iterator.next().value
			if (value.isOver) {
				iterator.remove()
			} else {
				value.execute()
			}
		}
	}

}