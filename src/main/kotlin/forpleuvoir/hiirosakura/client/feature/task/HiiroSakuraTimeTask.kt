package forpleuvoir.hiirosakura.client.feature.task

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * 时间任务

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.task

 * 文件名 HiiroSakuraTimeTask

 * 创建时间 2021/8/23 21:53

 * @author forpleuvoir

 */
class HiiroSakuraTimeTask : AbstractHiiroSakuraData("timeTask") {
	private val log = HSLogger.getLogger(javaClass)

	private val data = ConcurrentHashMap<String, TimeTaskBase>()

	var selected: TimeTaskBase? = null


	fun add(timeTaskBase: TimeTaskBase) {
		data[timeTaskBase.name]
		this.onValueChanged()
	}

	fun remove(name: String) {
		data.remove(name)
		this.onValueChanged()
	}

	fun reset(originName: String, current: TimeTaskBase) {
		if (data.containsKey(originName)) {
			data.remove(originName)
			add(current)
		}
	}


	fun isSelected(timeTaskBase: TimeTaskBase?): Boolean {
		return timeTaskBase?.let { it == selected } == true
	}

	fun sortList(): List<TimeTaskBase> {
		val sortList = LinkedList<Int>()
		val map = HashMap<String, Int>()
		for (value in data.values) {
			sortList.add(value.sort)
			map[value.name] = value.sort
		}
		sortList.sort()
		val list = LinkedList<TimeTaskBase>()
		for (item in sortList) {
			map.forEach { (v, k) ->
				if (k == item) {
					list.addLast(data[v])
				}
			}
		}
		return list
	}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonObject) {
				val obj = element.asJsonObject
				val saveData = JsonUtil.gson.fromJson<Map<String, JsonObject>>(
					obj,
					object : TypeToken<Map<String, JsonObject>>() {}.type
				)
				data.clear()
				for (item in saveData) {
					val timeTask = TimeTaskParser.parse(item.value["task"].asJsonObject)
					data[item.key] = TimeTaskBase(timeTask, item.value["sort"].asInt)
				}
			} else {
				log.warn("{}无法从JsonElement{}中读取数据", name, element)
			}
		} catch (e: Exception) {
			log.warn("{}无法从JsonElement{}中读取数据", name, element)
		}

	}

	override val asJsonElement: JsonElement
		get() = JsonUtil.gson.toJsonTree(data)


}