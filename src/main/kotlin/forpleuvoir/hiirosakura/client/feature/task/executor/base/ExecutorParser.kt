package forpleuvoir.hiirosakura.client.feature.task.executor.base

import forpleuvoir.hiirosakura.client.feature.event.base.Event
import forpleuvoir.hiirosakura.client.feature.task.executor.JavaScriptExecutor

/**
 * 执行器解析器
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task.executor.base
 *
 * #class_name ExecutorParser
 *
 * #create_time 2021-07-23 16:14
 */
object ExecutorParser {
	fun parse(script: String, event: Event? = null): IExecutor {
		return JavaScriptExecutor(script, event)
	}
}