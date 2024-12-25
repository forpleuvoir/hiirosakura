package moe.forpleuvoir.hiirosakura.command

import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegisterEvent
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber

@EventSubscriber
object HiirosakuraCommand {

    @Subscriber
    fun register(event: ClientCommandRegisterEvent) {
        event.dispatcher.apply {
            ScriptCommand()

        }
    }


}