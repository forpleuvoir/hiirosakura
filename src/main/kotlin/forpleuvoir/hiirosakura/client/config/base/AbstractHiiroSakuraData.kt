package forpleuvoir.hiirosakura.client.config.base

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fi.dy.masa.malilib.config.IConfigHandler
import fi.dy.masa.malilib.util.JsonUtils
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData

/**
 * 数据抽象类
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config.base
 *
 * #class_name AbstractHiiroSakuraData
 *
 * #create_time 2021/6/16 22:25
 */
abstract class AbstractHiiroSakuraData(
	/**
	 * 获取数据的名称
	 *
	 * @return [String]
	 */
	val name: String
) {
	private val configHandler: IConfigHandler = HiiroSakuraData.configHandler

	/**
	 * 从JsonElement中获取数据
	 *
	 * @param element [JsonElement]
	 */
	abstract fun setValueFromJsonElement(element: JsonElement)

	/**
	 * 将数据转换为JsonElement类型
	 *
	 * @return [JsonElement]
	 */
	abstract val asJsonElement: JsonElement?

	fun onValueChanged() {
		configHandler.onConfigsChanged()
	}

	companion object {
		@JvmStatic
		fun writeData(root: JsonObject, category: String, data: List<AbstractHiiroSakuraData>) {
			val obj = JsonUtils.getNestedObject(root, category, true)
			if (obj != null) {
				for (item in data) {
					obj.add(item.name, item.asJsonElement)
				}
			}
		}

		@JvmStatic
		fun readData(root: JsonObject, category: String, data: List<AbstractHiiroSakuraData>) {
			val obj = JsonUtils.getNestedObject(root, category, true)
			if (obj != null) {
				for (item in data) {
					item.setValueFromJsonElement(obj[item.name])
				}
			}
		}
	}

}