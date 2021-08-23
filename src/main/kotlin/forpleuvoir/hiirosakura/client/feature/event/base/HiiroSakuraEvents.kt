package forpleuvoir.hiirosakura.client.feature.event.base

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.event.*
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus.clearRunAsTimeTask
import forpleuvoir.hiirosakura.client.feature.event.base.EventBus.subscribeRunAsTimeTask
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser.parse
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * MOD事件集合
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event.base
 *
 * #class_name HiiroSakuraEvents
 *
 * #create_time 2021-07-23 13:44
 */
class HiiroSakuraEvents : AbstractHiiroSakuraData("event") {
	/**
	 * key:事件名
	 *
	 * value:订阅该事件 的task json数据 集合
	 */
	private val data: MutableMap<String, MutableList<EventSubscriberBase>> = ConcurrentHashMap()
	var selected: EventSubscriberBase? = null
	fun update(old: EventSubscriberBase, current: EventSubscriberBase) {
		if (old == current) return
		unsubscribe(old)
		subscribe(current)
	}

	fun isSelected(eventSubscriberBase: EventSubscriberBase?): Boolean {
		return eventSubscriberBase == selected
	}

	fun unsubscribe(eventBase: EventSubscriberBase) {
		unsubscribe(eventBase.eventType, eventBase.name!!)
	}

	fun subscribe(eventBase: EventSubscriberBase) {
		subscribe(eventBase.eventType, eventBase.timeTask!!)
	}

	fun subscribe(eventType: Class<out Event>, json: String) {
		subscribe(getEventType(eventType)!!, json)
	}

	private fun subscribe(eventType: String, json: String) {
		val timeTask = parse(JsonUtil.parseToJsonObject(json), null)
		val subscriber = EventSubscriberBase(timeTask.name, json, eventType)
		if (data.containsKey(eventType)) {
			data[eventType]!!.removeIf { eventSubscriberBase: EventSubscriberBase -> eventSubscriberBase.name == subscriber.name }
			data[eventType]!!.add(subscriber)
		} else {
			data[eventType] = Lists.newArrayList(subscriber)
		}
		onValueChanged()
	}

	fun unsubscribe(eventType: Class<out Event>, name: String) {
		unsubscribe(getEventType(eventType)!!, name)
	}

	private fun unsubscribe(eventType: String, name: String) {
		if (data.containsKey(eventType)) {
			data[eventType]!!.removeIf { eventSubscriberBase: EventSubscriberBase -> eventSubscriberBase.name == name }
		}
		onValueChanged()
	}

	fun isEnabled(eventType: Class<out Event?>, name: String): Boolean {
		val returnValue = AtomicBoolean(false)
		val first = data[getEventType(eventType)]!!
			.stream()
			.filter { eventSubscriberBase: EventSubscriberBase -> eventSubscriberBase.name == name }
			.findFirst()
		first.ifPresent { eventSubscriberBase: EventSubscriberBase -> returnValue.set(eventSubscriberBase.enabled) }
		return returnValue.get()
	}

	val allEventSubscriberBase: Collection<EventSubscriberBase>
		get() {
			val list = ArrayList<EventSubscriberBase>()
			data.forEach { (_: String?, v: List<EventSubscriberBase>?) ->
				list.addAll(
					v
				)
			}
			return list
		}

	fun getAllEventSubscriberBase(event: String?): Collection<EventSubscriberBase> {
		return if (data[event] != null) data[event]!! else ArrayList()
	}

	fun toggleEnabled(eventSubscriberBase: EventSubscriberBase) {
		data[eventSubscriberBase.eventType]!!
			.stream().filter { eventSubscriberBase == it }.findFirst()
			.ifPresent {
				it.enabled = !it.enabled
			}
		onValueChanged()
	}

	private fun sync() {
		clearRunAsTimeTask()
		data.forEach { (_: String, v: List<EventSubscriberBase>) ->
			v.forEach {
				if (it.enabled) {
					subscribeRunAsTimeTask(
						events[it.eventType]!!,
						it.timeTask!!
					)
				}
			}
		}
	}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonObject) {
				val obj = element.asJsonObject
				val saveData = JsonUtil.gson
					.fromJson<Map<String, JsonArray>>(
						obj,
						object : TypeToken<Map<String, JsonArray>>() {}.type
					)
				data.clear()
				saveData.forEach { (k: String, v: JsonArray) ->
					val list = ArrayList<EventSubscriberBase>()
					for (jsonElement in v) {
						list.add(EventSubscriberBase(k, jsonElement.asJsonObject))
					}
					data[k] = list
				}
				sync()
			} else {
				log.warn("{}无法从JsonElement{}中读取数据", name, element)
			}
		} catch (e: Exception) {
			log.warn("{}无法从JsonElement{}中读取数据", name, element, e)
		}
	}

	override val asJsonElement: JsonElement
		get() = JsonUtil.gson.toJsonTree(data)

	companion object {
		@Transient
		private val log = getLogger(HiiroSakuraEvents::class.java)

		@JvmField
		val events: Map<String, Class<out Event>> = mapOf(
			"onGameJoin" to OnGameJoinEvent::class.java,
			"onServerJoin" to OnServerJoinEvent::class.java,
			"onDisconnected" to OnDisconnectedEvent::class.java,
			"onDisconnect" to OnDisconnectEvent::class.java,
			"onMessage" to OnMessageEvent::class.java,
			"onDeath" to OnDeathEvent::class.java,
			"onPlayerTick" to OnPlayerTickEvent::class.java,
			"doAttack" to DoAttackEvent::class.java,
			"doItemPick" to DoItemPickEvent::class.java,
			"doItemUse" to DoItemUseEvent::class.java
		)

		fun getEventType(eventType: Class<out Event>): String? {
			for ((key, value) in events) {
				if (value == eventType) {
					return key
				}
			}
			return null
		}
	}
}