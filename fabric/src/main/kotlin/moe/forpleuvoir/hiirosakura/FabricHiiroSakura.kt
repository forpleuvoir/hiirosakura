package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.fabricevent.ReloadListenerRegistry
import net.fabricmc.api.ModInitializer

object FabricHiiroSakura : ModInitializer{
    override fun onInitialize() {
        HiiroSakura.init()
        ReloadListenerRegistry.register()
    }
}