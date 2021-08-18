package forpleuvoir.hiirosakura.client.feature.task.executor.base

import forpleuvoir.hiirosakura.client.feature.task.TimeTask

/**
 * 执行器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task.executor.base
 *
 * #class_name IExecutor
 *
 * #create_time 2021-07-23 16:21
 */
interface IExecutor {
	fun execute(task: TimeTask)
	val asString: String
		get() = this.toString()
}