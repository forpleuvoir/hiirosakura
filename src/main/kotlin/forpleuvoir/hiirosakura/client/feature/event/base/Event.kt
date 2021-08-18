package forpleuvoir.hiirosakura.client.feature.event.base

import com.google.common.collect.ImmutableMap
import forpleuvoir.hiirosakura.client.HiiroSakuraClient

/**
 * 事件
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event.base
 *
 * #class_name Event
 *
 * #create_time 2021-07-23 13:25
 */
abstract class Event {
	val hs = HiiroSakuraClient
	fun broadcastHandle(
		eventListeners: ImmutableMap<Class<out Event>, ImmutableMap<String, (Event) -> Unit>>
	) {
		if (eventListeners.containsKey(this.javaClass)) {
			for (value in eventListeners[this.javaClass]!!.values) {
				value.invoke(this)
			}
		}
	}
}