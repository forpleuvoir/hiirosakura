package moe.forpleuvoir.hiirosakura.functional.script

import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.hiirosakura.command.ClientCommandDispatcher
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand

fun ClientCommandDispatcher.ScriptCommand() =
    registerCommand("hs:script") {
        argument("script", StringArgumentType.string()) {
            execute {
                val script = StringArgumentType.getString(this, "script")
                this.source.client.executeSync {
                    ScriptExecutor(script).execute()
                }
            }
        }
    }


