package forpleuvoir.hiirosakura.client.util

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 日志辅助类
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name HSLogger
 *
 * #create_time 2021/6/16 22:41
 */
class HSLogger(clazz: Class<*>) {

	val log: Logger

	init {
		log = LoggerFactory.getLogger("${HiiroSakuraClient.MOD_NAME.replace(" ", "")}[${clazz.simpleName}]")
	}

	fun info(str: String, vararg params: Any?) {
		log.info(str, *params)
	}

	fun error(str: String, vararg params: Any?) {
		log.error(str, *params)
	}

	fun error(e: Exception) {
		log.error(e.message, e)
	}

	fun warn(str: String, vararg params: Any?) {
		log.warn(str, *params)
	}

	companion object {
		@JvmStatic
		fun getLogger(clazz: Class<*>): HSLogger {
			return HSLogger(clazz)
		}
	}
}