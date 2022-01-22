package forpleuvoir.hiirosakura.client.feature.common

import forpleuvoir.ibuki_gourd.common.IJsonData

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.common

 * 文件名 Runnable

 * 创建时间 2022/1/21 15:24

 * @author forpleuvoir

 */
interface Runnable : IJsonData {
	fun run(params: Map<String, Any> = HashMap())
	val asString: String
		get() = this.javaClass.simpleName
}