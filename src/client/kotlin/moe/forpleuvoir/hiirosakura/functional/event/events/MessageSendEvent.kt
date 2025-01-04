package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个发送消息的事件。
 *
 * 当玩家尝试发送一条消息时触发此事件，消息的内容可通过 `message` 属性获取和修改。
 *
 * 本事件可以被取消，取消后将阻止消息的发送。
 *
 * @property message 待发送的消息内容。
 */
class MessageSendEvent(
    @JvmField
    var message: String
) : CancellableEvent {
    override var canceled: Boolean = false
}