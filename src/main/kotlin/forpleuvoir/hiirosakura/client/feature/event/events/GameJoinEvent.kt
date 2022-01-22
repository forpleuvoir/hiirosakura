package forpleuvoir.hiirosakura.client.feature.event.events

import forpleuvoir.ibuki_gourd.event.Event

/**
 * 游戏加入事假

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.events

 * 文件名 GameJoinEvent

 * 创建时间 2022/1/21 12:32

 * @author forpleuvoir

 */
class GameJoinEvent(@JvmField val serverName: String?, @JvmField val serverAddress: String?) : Event {
}