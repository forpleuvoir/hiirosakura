package forpleuvoir.hiirosakura.client.feature.event.base

import com.google.common.collect.ImmutableMap
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler.Companion.INSTANCE
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskParser.parse
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import java.util.concurrent.ConcurrentHashMap

/**
 * 事件总线
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event.base
 *
 * #class_name EventBus
 *
 * #create_time 2021-07-23 13:18
 */
object EventBus {
	private val log = getLogger(EventBus::class.java)
	private val timeTaskEventListeners = ConcurrentHashMap<Class<out Event>, HashMap<String, (Event) -> Unit>>()
	private val eventListeners = ConcurrentHashMap<Class<out Event>, HashMap<String, (Event) -> Unit>>()

	@JvmStatic
	fun <E : Event> broadcast(event: E) {
		event.broadcastHandle(getEventListeners())
	}

	inline fun <reified E : Event> subscribe(listenerName: String, noinline listener: ((Event) -> Unit)) {
		subscribe(E::class.java, listenerName, listener)
	}

	fun <E : Event> subscribe(channel: Class<E>, listenerName: String, listener: ((Event) -> Unit)) {
		log.info("事件订阅({})", channel.simpleName, listenerName)
		if (eventListeners.containsKey(channel)) {
			if (!eventListeners[channel]!!.containsKey(listenerName)) {
				eventListeners[channel]!![listenerName] = listener
			}
		} else {
			val hashMap = HashMap<String, (Event) -> Unit>()
			hashMap[listenerName] = listener
			eventListeners[channel] = hashMap
		}
	}


	inline fun <reified E : Event> subscribeRunAsTimeTask(json: String) {
		subscribeRunAsTimeTask(E::class.java, json)
	}

	@JvmStatic
	fun <E : Event> subscribeRunAsTimeTask(channel: Class<E>, json: String) {
		val jsonObject = JsonUtil.parseToJsonObject(json)
		val name = jsonObject["name"].asString
		log.info("时间任务事件订阅({})", channel.simpleName, name)
		if (timeTaskEventListeners.containsKey(channel)) {
			if (!timeTaskEventListeners[channel]!!.containsKey(name)) {
				timeTaskEventListeners[channel]!![name] = {
					INSTANCE!!.addTask(
						parse(JsonUtil.parseToJsonObject(json), it)
					)
				}
			}
		} else {
			val hashMap = HashMap<String, (Event) -> Unit>()
			hashMap[name] = {
				INSTANCE!!.addTask(
					parse(JsonUtil.parseToJsonObject(json), it)
				)
			}
			timeTaskEventListeners[channel] = hashMap
		}
	}

	inline fun <reified E : Event> unsubscribeRunAsTimeTask(listenerName: String) {
		unsubscribeRunAsTimeTask(E::class.java, listenerName)
	}

	fun <E : Event?> unsubscribeRunAsTimeTask(channel: Class<E>, listenerName: String) {
		log.info("时间任务事件退订({})", channel.simpleName, listenerName)
		if (timeTaskEventListeners.containsKey(channel)) {
			timeTaskEventListeners[channel]!!.remove(listenerName)
		}
	}

	inline fun <reified E : Event> unsubscribe(listenerName: String) {
		unsubscribe(E::class.java, listenerName)
	}

	fun <E : Event?> unsubscribe(channel: Class<E>, listenerName: String) {
		log.info("事件退订({})", channel.simpleName, listenerName)
		if (eventListeners.containsKey(channel)) {
			eventListeners[channel]!!.remove(listenerName)
		}
	}

	@JvmStatic
	fun clearRunAsTimeTask() {
		timeTaskEventListeners.clear()
	}

	/**
	 * 清空所有订阅事件
	 */
	fun clear() {
		eventListeners.clear()
	}


	private fun getEventListeners(): ImmutableMap<Class<out Event>, ImmutableMap<String, (Event) -> Unit>> {
		val builder = ImmutableMap.Builder<Class<out Event>, ImmutableMap<String, (Event) -> Unit>>()
		eventListeners.forEach { (k: Class<out Event>, v: HashMap<String, (Event) -> Unit>) ->
			builder.put(k, getImmutableMap(v))
		}
		timeTaskEventListeners.forEach { (k: Class<out Event>, v: HashMap<String, (Event) -> Unit>) ->
			builder.put(k, getImmutableMap(v))
		}
		return builder.build()
	}

	private fun getImmutableMap(
		v: HashMap<String, (Event) -> Unit>
	): ImmutableMap<String, (Event) -> Unit> {
		val listenerBuilder = ImmutableMap.Builder<String, (Event) -> Unit>()
		v.forEach { (key: String, value: (Event) -> Unit) -> listenerBuilder.put(key, value) }
		return listenerBuilder.build()
	}
}