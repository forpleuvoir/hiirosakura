package moe.forpeluvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.HiiroSakura
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(HiiroSakura.MOD_ID)
class NeoforgeHiirosakura(eventBus: IEventBus, modContainer: ModContainer) {
    init {
        HiiroSakura.init()
    }
}