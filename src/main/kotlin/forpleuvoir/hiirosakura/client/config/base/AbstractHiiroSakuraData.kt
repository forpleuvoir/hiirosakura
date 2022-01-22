package forpleuvoir.hiirosakura.client.config.base

import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.ibuki_gourd.common.IJsonData
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.config.IConfigHandler

/**
 * 数据抽象类
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.config.base
 *
 * 文件名 AbstractHiiroSakuraData
 *
 * 创建时间 2021/6/16 22:25
 */
abstract class AbstractHiiroSakuraData(
	/**
	 * 获取数据的名称
	 *
	 * @return [String]
	 */
	val name: String
) : IJsonData, IModInitialize {
	private val configHandler: IConfigHandler = HiiroSakuraData
	open fun onValueChanged() {
		configHandler.onConfigChange()
	}

	companion object {
		@JvmStatic
		fun writeData(root: JsonObject, data: List<AbstractHiiroSakuraData>) {
			for (item in data) {
				root.add(item.name, item.asJsonElement)
			}
		}

		@JvmStatic
		fun readData(root: JsonObject, data: List<AbstractHiiroSakuraData>) {
			for (item in data) {
				root[item.name]?.let { item.setValueFromJsonElement(it) }
			}
		}
	}

}