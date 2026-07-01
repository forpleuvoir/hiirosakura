package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.functional.event.events.HSEvents
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.ibukigourd.event.events.client.input.KeyboardEvent
import moe.forpleuvoir.ibukigourd.event.events.client.input.MouseEvent
import moe.forpleuvoir.nebula.event.Event
import moe.forpleuvoir.nebula.event.Registration

interface HSEventType {
    val id: String
    val event: Event<*>
    fun subscribe(action: (Any) -> Unit): Registration
}

class EventType<T : Any>(
    override val id: String,
    override val event: Event<(T) -> Unit>,
) : HSEventType {
    override fun subscribe(action: (Any) -> Unit): Registration =
        event.register { ctx -> action(ctx) }
}

object EventTypes {

    val all: Map<String, HSEventType> = linkedMapOf<String, HSEventType>(
        "keyboard.pressed" to EventType("keyboard.pressed", KeyboardEvent.Pressed),
        "keyboard.released" to EventType("keyboard.released", KeyboardEvent.Released),
        "mouse.pressed" to EventType("mouse.pressed", MouseEvent.Pressed),
        "mouse.released" to EventType("mouse.released", MouseEvent.Released),
        "mouse.scrolling" to EventType("mouse.scrolling", MouseEvent.Scrolling),
        "mouse.moving" to EventType("mouse.moving", MouseEvent.Moving),
        "mouse.dragging" to EventType("mouse.dragging", MouseEvent.Dragging),
        "client_life_cycle.starting" to EventType("client_life_cycle.starting", ClientLifecycleEvent.Starting),
        "client_life_cycle.stopping" to EventType("client_life_cycle.stopping", ClientLifecycleEvent.Stopping),
        "client_tick.start" to EventType("client_tick.start", ClientTickEvent.TickStart),
        "client_tick.end" to EventType("client_tick.end", ClientTickEvent.TickEnd),

        "server_join" to EventType("server_join", HSEvents.ServerJoin),
        "game_join" to EventType("game_join", HSEvents.GameJoin),
        "game_exit" to EventType("game_exit", HSEvents.GameExit),
        "disconnect" to EventType("disconnect", HSEvents.Disconnect),
        "player_death" to EventType("player_death", HSEvents.PlayerDeath),
        "player_respawn" to EventType("player_respawn", HSEvents.PlayerRespawn),
        "break_block" to EventType("break_block", HSEvents.BreakBlock),
        "command_send" to EventType("command_send", HSEvents.CommandSend),
        "message_send" to EventType("message_send", HSEvents.MessageSend),
        "message_receive" to EventType("message_receive", HSEvents.MessageReceive),
        "player_attack" to EventType("player_attack", HSEvents.PlayerAttack),
        "player_pick" to EventType("player_pick", HSEvents.PlayerPick),
        "player_use" to EventType("player_use", HSEvents.PlayerUse),
        "sound_play" to EventType("sound_play", HSEvents.SoundPlay)
    )

    val ids: List<String> get() = all.keys.toList()

    operator fun get(id: String): HSEventType? = all[id]
}