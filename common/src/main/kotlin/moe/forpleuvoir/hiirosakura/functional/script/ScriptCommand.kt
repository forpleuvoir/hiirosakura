package moe.forpleuvoir.hiirosakura.functional.script

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.command.clientSource
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import net.minecraft.commands.SharedSuggestionProvider

fun CommandDispatcher<out SharedSuggestionProvider>.ScriptCommand() =
    registerCommand("hs:script") {
        argument("script", StringArgumentType.string()) {
            execute {
                val script = StringArgumentType.getString(this, "script")
                this.source.clientSource.client.executeIfPossible {
                    ScriptExecutor(script).execute()
                }
            }
        }
    }


