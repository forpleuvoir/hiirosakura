package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.Event

/**
 * 玩家死亡事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 PlayerDeathEvent

 * 创建时间 2022/1/21 14:22

 * @author forpleuvoir

 */
class PlayerDeathEvent(@JvmField val killerName: String?, @JvmField val message: String?) : Event {
}