package moe.forpleuvoir.hiirosakura.config

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.gui.HiiroSakuraScreen
import moe.forpleuvoir.ibukigourd.config.ClientModConfigManager
import moe.forpleuvoir.ibukigourd.config.ModConfig
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.util.mc

@ModConfig(name = "config")
object HSConfig : ClientModConfigManager(HiiroSakura.metadata, "config") {

    val openScreen by keyBind("open_screen", KeyBind(Keyboard.H,Keyboard.S){
        HiiroSakuraScreen().open(mc.currentScreen)
    })

    val renderInfoAddon = addConfig(RenderInfoAddon)

}