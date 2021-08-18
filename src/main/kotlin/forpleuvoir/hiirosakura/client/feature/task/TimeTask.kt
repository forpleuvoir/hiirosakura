package forpleuvoir.hiirosakura.client.feature.task

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor

/**
 * 定时任务
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task
 *
 * #class_name TimeTask
 *
 * #create_time 2021/7/22 22:27
 */
class TimeTask(private val executor: IExecutor, val data: TimeTaskData) {
	var counter = 0
		private set
	private var timeCounter = 0

	@JvmField
	val hs = HiiroSakuraClient

	fun executes() {
		if (isOver || !shouldExecute()) return
		executor.execute(this)
		counter++
		timeCounter = 0
	}

	private fun shouldExecute(): Boolean {
		timeCounter++
		return if (counter == 0) {
			timeCounter >= data.startTime
		} else {
			timeCounter >= data.cyclesTime
		}
	}

	val isOver: Boolean
		get() = counter >= data.cycles
	val name: String
		get() = data.name
	val executorAsString: String
		get() = executor.asString

	companion object {
		@JvmStatic
		fun once(executor: IExecutor, startTime: Int = 0, name: String): TimeTask {
			return TimeTask(executor, TimeTaskData(name, startTime, 1, 0))
		}
	}
}