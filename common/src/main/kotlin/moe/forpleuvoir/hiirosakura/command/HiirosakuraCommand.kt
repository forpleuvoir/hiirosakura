package moe.forpleuvoir.hiirosakura.command

import moe.forpleuvoir.hiirosakura.functional.gameplay.CameraSwitcher.CameraSwitchCommand
import moe.forpleuvoir.hiirosakura.functional.script.ScriptCommand
import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegistrationEvent
import moe.forpleuvoir.nebula.common.api.Initializable

object HiirosakuraCommand : Initializable {

    override fun init() {
        ClientCommandRegistrationEvent.register {
            ScriptCommand()
            CameraSwitchCommand()
        }
    }

}
