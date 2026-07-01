package moe.forpleuvoir.hiirosakura.functional.gameplay

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import moe.forpleuvoir.ibukigourd.command.clientSource
import moe.forpleuvoir.ibukigourd.command.dsl.registerCommand
import moe.forpleuvoir.ibukigourd.config.item.configKeybind
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configBoolean
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.world.entity.Entity

object CameraSwitcher : ConfigGroup("camera_switcher") {

    private val _shortcutKey by configKeybind("shortcut_key", Keybind {
        switchToTarget()
    })

    val blockPlayerActions by configBoolean("block_player_actions", false)

    @JvmStatic
    val isSwitched: Boolean get() = mc.cameraEntity != mc.player

    @JvmStatic
    val shouldBlockActions: Boolean get() = isSwitched && blockPlayerActions

    private val clientEntities
        get() = mc.level?.entitiesForRendering() ?: emptyList()

    context(context: CommandDispatcher<out SharedSuggestionProvider>)
    fun CameraSwitchCommand() = registerCommand("hs:camera_switch") {
        argument("target", StringArgumentType.string()) {
            suggests { _, builder ->
                SharedSuggestionProvider.suggest(clientEntities.map { "\"${it.name.string}[${it.stringUUID}]\"" }, builder)
            }
            execute {
                val playerName = StringArgumentType.getString(this, "target")
                clientEntities
                    .find { "${it.name.string}[${it.stringUUID}]" == playerName }
                    ?.let { switchCamera(it) }
                    ?: this.source.clientSource.sendFeedback(Literal("entity not found"))
            }
        }
        literal("reset") {
            execute {
                resetCamera()
            }
        }
    }


    fun switchToTarget() {
        mc.crosshairPickEntity?.let { switchCamera(it) } ?: resetCamera()
    }

    fun switchCamera(target: Entity?) {
        mc.setCameraEntity(target)
    }

    fun resetCamera() {
        switchCamera(mc.player)
    }

}