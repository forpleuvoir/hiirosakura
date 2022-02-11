package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.CancelableEvent

/**
 * 收到消息事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 MessageEvent

 * 创建时间 2022/1/21 14:17

 * @author forpleuvoir

 */
class MessageEvent(@JvmField val messageType: String, @JvmField val message: String, @JvmField val senderUUID: String) :
    CancelableEvent() {
}