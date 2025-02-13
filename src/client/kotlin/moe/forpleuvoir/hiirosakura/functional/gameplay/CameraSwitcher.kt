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
import net.minecraft.command.CommandSource
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

    private val clientEntities
        get() = mc.world?.entities ?: emptyList()

    fun ClientCommandDispatcher.CameraSwitchCommand() =
        registerCommand("hs:camera_switch") {
            argument("target", StringArgumentType.string()) {
                suggests { _, builder ->
                    CommandSource.suggestMatching(clientEntities.map { it.name.string }, builder)
                }
                execute {
                    val playerName = StringArgumentType.getString(this, "target")
                    clientEntities
                        .find { it.name.string == playerName }
                        ?.let { switchCamera(it) }
                        ?: this.source.sendFeedback(Literal("entity not found"))
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

    fun switchCamera(target: Entity?) {
        mc.setCameraEntity(target)
    }

    fun resetCamera() {
        switchCamera(mc.player)
    }

}