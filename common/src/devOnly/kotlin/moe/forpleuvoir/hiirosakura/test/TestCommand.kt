package moe.forpleuvoir.hiirosakura.test

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.ibukigourd.command.clientSource
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import moe.forpleuvoir.ibukigourd.config.exportTranslateKeys
import moe.forpleuvoir.ibukigourd.event.events.client.ClientCommandRegistrationEvent
import moe.forpleuvoir.ibukigourd.lang.TranslationRecorder
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.commands.SharedSuggestionProvider
import kotlin.io.path.Path

object TestCommand : Initializable {

    override fun init() {
        ClientCommandRegistrationEvent.register {
            testCommand()
        }
    }

    context(context: CommandDispatcher<out SharedSuggestionProvider>)
    fun testCommand() = registerCommand("hstest") {
        "config_keys" {
            execute {
                val recorder = TranslationRecorder(false, keepExisting = true)
                recorder.categorizer = { "config" }
                recorder.addFilter { true }
                HSConfig.exportTranslateKeys().forEach {
                    recorder.record(it)
                }
                recorder.dump(Path("../../../common/src/devOnly/lang"))
            }
        }
    }
}