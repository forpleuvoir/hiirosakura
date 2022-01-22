package forpleuvoir.hiirosakura.client.util

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.ibuki_gourd.common.ModLogger

/**
 * 日志辅助类
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.util
 *
 * 文件名 HSLogger
 *
 * 创建时间 2021/6/16 22:41
 */
class HSLogger(clazz: Class<*>) : ModLogger(clazz, HiiroSakuraClient) {

	companion object {
		@JvmStatic
		fun getLogger(clazz: Class<*>): HSLogger {
			return HSLogger(clazz)
		}
	}
}