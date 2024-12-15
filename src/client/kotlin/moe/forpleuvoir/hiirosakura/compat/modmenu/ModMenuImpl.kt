package moe.forpleuvoir.hiirosakura.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import moe.forpleuvoir.hiirosakura.gui.HiiroSakuraScreen

object ModMenuImpl :ModMenuApi{

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        return ConfigScreenFactory { HiiroSakuraScreen().apply { parentScreen = it } }
    }

}