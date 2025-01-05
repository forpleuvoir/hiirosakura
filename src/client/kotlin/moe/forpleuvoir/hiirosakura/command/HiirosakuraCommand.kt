package moe.forpleuvoir.hiirosakura.command

import com.mojang.brigadier.CommandDispatcher
import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher.CameraSwitchCommand
import moe.forpleuvoir.hiirosakura.functional.script.ScriptCommand
import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegisterEvent
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@EventSubscriber
object HiirosakuraCommand {

    @Subscriber
    fun register(event: ClientCommandRegisterEvent) {
        event.dispatcher.apply {
            ScriptCommand()
            CameraSwitchCommand()
        }
    }

}

typealias ClientCommandDispatcher = CommandDispatcher<FabricClientCommandSource>