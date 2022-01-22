package forpleuvoir.hiirosakura.client.feature.timertask.executor

import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask
import java.util.function.Consumer

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.executor

 * 文件名 SimpleExecutor

 * 创建时间 2022/1/19 12:39

 * @author forpleuvoir

 */
class SimpleExecutor(private val executor: Consumer<TimerTask>) : IExecutor {
	override fun execute(task: TimerTask) {
		executor.accept(task)
	}
}