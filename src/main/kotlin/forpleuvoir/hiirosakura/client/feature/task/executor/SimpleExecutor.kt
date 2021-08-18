package forpleuvoir.hiirosakura.client.feature.task.executor

import forpleuvoir.hiirosakura.client.feature.task.TimeTask
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor

/**
 * 执行器简单实现
 *
 *
 * 项目名 hiirosakura
 *
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.task.executor
 *
 *
 * 文件名 SimpleExecutor
 *
 *
 * 创建时间 2021-08-18 16:12
 *
 * @author forpleuvoir
 */
class SimpleExecutor(val executor: (TimeTask) -> Unit) : IExecutor {
	override fun execute(task: TimeTask) {
		executor.invoke(task)
	}

	override val asString: String
		get() = "SimpleExecutor"

}