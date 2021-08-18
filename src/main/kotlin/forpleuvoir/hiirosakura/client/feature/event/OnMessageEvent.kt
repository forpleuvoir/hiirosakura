package forpleuvoir.hiirosakura.client.feature.event

import forpleuvoir.hiirosakura.client.feature.event.base.Event

/**
 * 收到消息事件
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.event
 *
 * #class_name OnMessageEvent
 *
 * #create_time 2021/7/31 22:50
 */
class OnMessageEvent(message: String, type: Int) : Event() {
	/**
	 * 收到的消息文本
	 */
	@JvmField
	val message: String

	/**
	 * 消息类型
	 */
	@JvmField
	val type: Int

	init {
		this.message = message.replace("(§.)".toRegex(), "")
		this.type = type
	}
}