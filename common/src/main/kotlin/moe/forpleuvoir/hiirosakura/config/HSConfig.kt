package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.chataddons.ChatConfig
import moe.forpleuvoir.hiirosakura.functional.gameplay.GamePlay
import moe.forpleuvoir.hiirosakura.functional.renderaddons.RenderInfoAddon
import moe.forpleuvoir.hiirosakura.gui.HiiroSakuraScreen
import moe.forpleuvoir.ibukigourd.config.ClientModConfigManager
import moe.forpleuvoir.ibukigourd.config.ModConfig
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.config.item.impl.boolean
import moe.forpleuvoir.nebula.config.item.impl.enum
import net.minecraft.client.tutorial.TutorialSteps

@ModConfig(name = "config")
object HSConfig : ClientModConfigManager(HiiroSakura.MOD_ID, "config") {

    private val _openScreen by keyBind("open_screen", KeyBind(Keyboard.H, Keyboard.S) {
        HiiroSakuraScreen().open(mc.screen)
    })

    val tutorialStep: TutorialSteps by enum("tutorial_step", TutorialSteps.NONE).apply {
        subscribe {
            mc.tutorial.setStep(it.getValue())
        }
    }

    val pickPlayerHeadOnCreative by boolean("pick_player_head_on_creative", false)

    init {
        addConfig(RenderInfoAddon)
        addConfig(GamePlay)
        addConfig(ChatConfig)
    }

}