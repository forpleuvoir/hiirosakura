package moe.forpleuvoir.hiirosakura.functional.gameplay

import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.hiirosakura.command.ClientCommandDispatcher
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBindBoolean
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.entity.Entity

object CameraSwitcher {

    object Config : ModConfigContainer("camera_switcher") {

        val shortcutKey by keyBind("shortcut_key", KeyBind {
            switchToTarget()
        })

        val blockPlayerActions by keyBindBoolean("block_player_actions", true)

    }

    @JvmStatic
    val isSwitched: Boolean get() = mc.cameraEntity != mc.player

    @JvmStatic
    val shouldBlockActions: Boolean get() = isSwitched && Config.blockPlayerActions.value

    private val clientPlayers
        get() = buildList {
            mc.world?.entities?.forEach {
                add(it)
            }
        }

    fun ClientCommandDispatcher.CameraSwitchCommand() =
        registerCommand("hs:camera_switch") {
            argument("target", StringArgumentType.string()) {
                suggests(*clientPlayers.map { it.name.string }.toTypedArray())
                execute {
                    val playerName = StringArgumentType.getString(this, "target")
                    clientPlayers
                        .find { it.name.string == playerName }
                        ?.let { switchCamera(it) }
                        ?: this.source.sendFeedback(Literal("player not found"))
                }
            }
            literal("reset") {
                execute {
                    resetCamera()
                }
            }
        }


    fun switchToTarget() {
        mc.targetedEntity?.let { switchCamera(it) } ?: resetCamera()
    }

    fun switchCamera(target: Entity) {
        mc.cameraEntity = target
    }

    fun resetCamera() {
        mc.cameraEntity = mc.player
    }

}