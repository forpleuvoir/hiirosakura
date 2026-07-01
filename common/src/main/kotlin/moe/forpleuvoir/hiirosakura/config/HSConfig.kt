package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatConfig
import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon
import moe.forpleuvoir.hiirosakura.ui.HiiroSakuraScreen
import moe.forpleuvoir.ibukigourd.config.ClientModConfigManager
import moe.forpleuvoir.ibukigourd.config.item.configKeybind
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.ui.open
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.configBoolean
import moe.forpleuvoir.nebula.config.item.configEnum
import moe.forpleuvoir.nebula.config.persistence.yaml
import net.minecraft.client.tutorial.TutorialSteps

object HSConfig : ClientModConfigManager(HiiroSakura.MOD_ID, "config", persistence = { yaml() }) {

    private val _openScreen by configKeybind("open_screen", Keybind(Keyboard.H, Keyboard.S) {
        HiiroSakuraScreen().open()
    })

    val tutorialStep: TutorialSteps by configEnum("tutorial_step", TutorialSteps.NONE).apply {
        observe {
            mc.tutorial.setStep(it.getValue())
        }
    }

    val pickPlayerHeadOnCreative by configBoolean("pick_player_head_on_creative", false)

    init {
        addConfig(RenderInfoAddon)
        addConfig(GamePlay)
        addConfig(ChatConfig)
    }

}