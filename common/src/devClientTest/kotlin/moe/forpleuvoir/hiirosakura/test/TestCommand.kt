package moe.forpleuvoir.hiirosakura.test

import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegisterEvent
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber

@EventSubscriber
object TestCommand {

    @Subscriber
    fun register(event: ClientCommandRegisterEvent) {
        event.dispatcher.apply {
        }
    }


}