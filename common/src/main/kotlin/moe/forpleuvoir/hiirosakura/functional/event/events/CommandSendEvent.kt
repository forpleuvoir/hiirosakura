package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个触发命令发送行为的事件。
 *
 * 当玩家尝试发送命令时触发该事件。事件携带要发送的命令内容，可通过 `command` 属性获取和修改。
 *
 * 本事件可以被取消，取消后将阻止命令的发送。
 *
 * @property command 待发送的命令字符串，可以对其进行修改以改变执行的命令。
 */
class CommandSendEvent(
    @JvmField
    var command: String
) : CancellableEvent {
    override var canceled: Boolean = false
}