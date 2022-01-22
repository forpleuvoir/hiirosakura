package forpleuvoir.hiirosakura.client.feature.timertask.gui

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger
import forpleuvoir.ibuki_gourd.common.IModInitialize
import forpleuvoir.ibuki_gourd.event.EventBus
import forpleuvoir.ibuki_gourd.event.events.ClientEndTickEvent
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import java.util.*

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 HiiroSakuraTimerTask

 * 创建时间 2022/1/19 18:08

 * @author forpleuvoir

 */
class HiiroSakuraTimerTask : AbstractHiiroSakuraData("timeTask"), IModInitialize {
	private val log = HSLogger.getLogger(javaClass)

	private val data = LinkedList<TimerTaskWrappedWithKeyBind>()

	override fun initialize() {
		EventBus.subscribe<ClientEndTickEvent> {
			data.forEach { task -> task.handlerKey() }
		}
	}

	fun data(): List<TimerTaskWrappedWithKeyBind> {
		return data
	}

	fun add(task: TimerTaskWrappedWithKeyBind) {
		data.add(task)
		onValueChanged()
	}

	fun indexOf(task: TimerTaskWrappedWithKeyBind): Int {
		return data.indexOf(task)
	}

	fun reset(originIndex: Int, current: TimerTaskWrappedWithKeyBind) {
		data[originIndex] = current
		onValueChanged()
	}

	fun reset(old: TimerTaskWrappedWithKeyBind, current: TimerTaskWrappedWithKeyBind) {
		reset(data.indexOf(old), current)
	}

	fun remove(task: TimerTaskWrappedWithKeyBind) {
		if (data.remove(task)) {
			this.onValueChanged()
		}
	}

	fun remove(index: Int) {
		data.removeAt(index)
		this.onValueChanged()
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
		}

	override fun setValueFromJsonElement(jsonElement: JsonElement) {
		try {
			if (jsonElement.isJsonArray) {
				val saveData = jsonElement.asJsonArray
				data.clear()
				for (item in saveData) {
					data.add(TimerTaskWrappedWithKeyBind().apply { setValueFromJsonElement(item) })
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