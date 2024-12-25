package moe.forpleuvoir.hiirosakura.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.CommonApiLoader
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.nebula.common.color.Colors
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.slf4j.LoggerFactory
import javax.script.ScriptEngineManager


private val log = LoggerFactory.getLogger("${HiiroSakura.MOD_NAME}[ScriptCommand]")

fun CommandDispatcher<FabricClientCommandSource>.ScriptCommand() =
    registerCommand("hs:script") {
        argument("script", StringArgumentType.string()) {
            execute {
                val engine = ScriptEngineManager().getEngineByName("nashorn")
                val script = StringArgumentType.getString(this, "script")
                this.source.client.executeSync {
                    runCatching {
                        CommonApiLoader.eval(engine)
                        engine.eval(script)
                    }.onFailure {
                        Toast.showToast(
                            duration = Toast.LONG_DURATION,
                            text = Literal(it.message ?: "unknown").withColor(Colors.RED)
                        )
                        log.error(it.message, it)
                    }
                }
            }
        }
    }


