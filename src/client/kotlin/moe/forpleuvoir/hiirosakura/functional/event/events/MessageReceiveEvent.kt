package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个接收到消息的事件。
 *
 * 当玩家接收到一条消息时触发此事件，消息的内容通过 `message` 属性提供。
 *
 * 本事件可以被取消，取消后将阻止消息的接收。
 *
 * @property message 接收到的消息内容。
 */
class MessageReceiveEvent(
    @JvmField
    val message: String
) : CancellableEvent {
    override var canceled: Boolean = false
}