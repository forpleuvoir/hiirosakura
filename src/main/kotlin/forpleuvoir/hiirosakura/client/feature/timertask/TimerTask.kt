package forpleuvoir.hiirosakura.client.feature.timertask

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.feature.timertask.executor.IExecutor
import forpleuvoir.hiirosakura.client.feature.timertask.executor.SimpleExecutor
import forpleuvoir.ibuki_gourd.common.IJsonData
import forpleuvoir.ibuki_gourd.task.ITask
import forpleuvoir.ibuki_gourd.utils.clamp
import java.util.*
import java.util.function.Consumer

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask

 * 文件名 TimerTask

 * 创建时间 2022/1/19 11:31

 * @author forpleuvoir

 */
class TimerTask(
	@JvmField val name: String = "timer_task",
	delay: Int = 0,
	period: Int = 1,
	times: Int = 1,
	val executor: IExecutor
) : ITask, IJsonData {

	/**
	 * 运行时唯一标识符,不应该用来储存
	 */
	@JvmField
	val uuid: String = UUID.randomUUID().toString()

	@JvmField
	val delay: Int

	@JvmField
	val period: Int

	@JvmField
	val times: Int

	constructor(task: TimerTask) : this(task.name, task.delay, task.period, task.times, task.executor)

	constructor(task: TimerTask, executor: IExecutor) : this(task.name, task.delay, task.period, task.times, executor)

	constructor(executor: IExecutor) : this(name = "timer_task", executor = executor)

	constructor(executor: Consumer<TimerTask>) : this(executor = SimpleExecutor(executor))

	constructor(
		delay: Int = 0,
		period: Int = 1,
		times: Int = 1,
		executor: IExecutor
	) : this(name = "timer_task", delay, period, times, executor)


	init {
		this.delay = delay.clamp(0, Int.MAX_VALUE).toInt()
		this.period = period.clamp(1, Int.MAX_VALUE).toInt()
		this.times = times.clamp(1, Int.MAX_VALUE).toInt()
	}

	var counter: Int = 0
		private set(value) {
			field = value.clamp(0, times).toInt()
		}

	private var tickCounter: Int = 0

	private val shouldExecute: Boolean
		get() {
			tickCounter++
			return if (counter == 0) {
				tickCounter >= delay
			} else {
				tickCounter >= period
			}
		}

	val isOver: Boolean get() = counter >= times


	override fun execute() {
		if (isOver || !shouldExecute) return
		executor.execute(this)
		counter++
		tickCounter = 0
	}

	fun getExecutorAsString(): String {
		return this.executor.asString
	}

	override val asJsonElement: JsonElement
		get() = JsonObject().apply {
			addProperty("name", name)
			addProperty("delay", delay)
			addProperty("period", period)
			addProperty("times", times)
			addProperty("executor", executor.asString)
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
	}


}