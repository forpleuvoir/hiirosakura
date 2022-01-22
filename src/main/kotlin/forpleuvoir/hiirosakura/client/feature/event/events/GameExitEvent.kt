package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.Event

/**
 * 玩家退出游戏事件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 GameExitEvent

 * 创建时间 2022/1/21 13:46

 * @author forpleuvoir

 */
class GameExitEvent(@JvmField val serverName: String?, @JvmField val serverAddress: String?) : Event {
}