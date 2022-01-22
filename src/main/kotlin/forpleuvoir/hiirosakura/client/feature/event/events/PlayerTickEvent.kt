package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.hiirosakura.client.feature.event.events.api.ClientPlayerApi
import forpleuvoir.ibuki_gourd.event.Event

/**
 * 玩家刻(Tick)事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 PlayerTickEvent

 * 创建时间 2022/1/21 14:47

 * @author forpleuvoir

 */
class PlayerTickEvent(@JvmField val player: ClientPlayerApi) : Event {
}