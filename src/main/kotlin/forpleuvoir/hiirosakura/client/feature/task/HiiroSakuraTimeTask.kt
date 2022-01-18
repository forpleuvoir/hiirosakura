package forpleuvoir.hiirosakura.client.feature.task

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger
import java.util.*


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

	private val data = LinkedList<TimeTaskBase>()

	var selected: TimeTaskBase? = null

	val allTimeTaskBase: Collection<TimeTaskBase>
		get() = data


	fun add(timeTaskBase: TimeTaskBase) {
		data.add(timeTaskBase)
		this.onValueChanged()
	}

	fun remove(name: String) {
		data.removeIf {
			it.name == name
		}
		this.onValueChanged()
	}

	fun reset(originName: String, current: TimeTaskBase) {
		data.find {
			it.name == originName
		}?.let {
			remove(it.name)
			add(current)
		}
	}

	fun get(name: String): TimeTaskBase? {
		return data.find {
			it.name == name
		}
	}


	fun isSelected(timeTaskBase: TimeTaskBase?): Boolean {
		return timeTaskBase?.let { it == selected } == true
	}

	fun sortList(): List<TimeTaskBase> {
		val sortList = LinkedList<Int>()
		val map = HashMap<String, Int>()
		for (value in data) {
			sortList.add(value.sort)
			map[value.name] = value.sort
		}
		sortList.sort()
		val list = LinkedList<TimeTaskBase>()
		for (item in sortList) {
			map.forEach { (k, v) ->
				if (v == item && !list.contains(get(k))) {
					list.addLast(get(k))
				}
			}
		}
		return list
	}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonArray) {
				val saveData = element.asJsonArray
				data.clear()
				for (item in saveData) {
					data.add(TimeTaskBase.fromJson(item.asJsonObject))
				}
			} else {
				log.warn("{}无法从JsonElement{}中读取数据", name, element)
			}
		} catch (e: Exception) {
			log.warn("{}无法从JsonElement{}中读取数据", name, element)
		}

	}

	override val asJsonElement: JsonElement
		get() {
			val arr = JsonArray()
			data.forEach {
				arr.add(it.toJson())
			}
			return arr
		}


}