package forpleuvoir.hiirosakura.client.feature.timertask.executor

import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.executor

 * 文件名 IExecutor

 * 创建时间 2022/1/19 12:37

 * @author forpleuvoir

 */
interface IExecutor {
	fun execute(task: TimerTask)
	val asString: String get() = this.javaClass.simpleName
}