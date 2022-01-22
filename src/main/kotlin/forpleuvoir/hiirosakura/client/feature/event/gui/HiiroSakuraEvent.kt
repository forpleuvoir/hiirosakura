package forpleuvoir.hiirosakura.client.feature.event.gui

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import java.util.*

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 HiiroSakuraEvent

 * 创建时间 2022/1/21 15:08

 * @author forpleuvoir

 */
class HiiroSakuraEvent : AbstractHiiroSakuraData("event") {
	private val log = HSLogger.getLogger(this.javaClass)

	private val data = LinkedList<EventSubscriber>()
	private val cache = LinkedHashMap<UUID, EventSubscriber>()

	override fun initialize() {

	}


	fun data(): List<EventSubscriber> {
		return data
	}

	fun add(subscriber: EventSubscriber) {
		data.add(subscriber)
		onValueChanged()
	}

	fun indexOf(subscriber: EventSubscriber): Int {
		return data.indexOf(subscriber)
	}

	fun remove(subscriber: EventSubscriber) {
		if (data.remove(subscriber)) {
			onValueChanged()
		}
	}

	fun reset(originIndex: Int, current: EventSubscriber) {
		data[originIndex] = current
		onValueChanged()
	}

	fun reset(old: EventSubscriber, new: EventSubscriber) {
		reset(indexOf(old), new)
	}

	fun moveUp(index: Int) {
		if (0 <= index - 1) {
			Collections.swap(data, index, index - 1)
			onValueChanged()
		}
	}

	fun moveDown(index: Int) {
		if (data.size - 1 >= index + 1) {
			Collections.swap(data, index, index + 1)
			onValueChanged()
		}
	}

	override val asJsonElement: JsonElement
		get() = JsonArray().apply {
			data.forEach { add(it.asJsonElement) }
			cache.forEach { EventBus.unSubscribe(it.value.event, it.key) }
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		try {
			if (jsonElement.isJsonArray) {
				val saveData = jsonElement.asJsonArray
				data.clear()
				cache.clear()
				for (item in saveData) {
					data.add(EventSubscriber().apply { setValueFromJsonElement(item) })
				}
				data.forEach {
					cache[EventBus.subscribe(it.event) { event -> it.run(mapOf("event" to event as Any)) }] = it
				}
			} else {
				log.warn(IbukiGourdLang.SetFromJsonFailed.tString(name, jsonElement))
			}
		} catch (e: Exception) {
			log.warn(IbukiGourdLang.SetFromJsonFailed.tString(name, jsonElement))
			log.error(e)
		}
	}


}