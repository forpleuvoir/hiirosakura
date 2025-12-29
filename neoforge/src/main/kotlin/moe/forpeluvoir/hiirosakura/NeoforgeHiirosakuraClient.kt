package moe.forpeluvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.HiiroSakuraClient
import moe.forpleuvoir.hiirosakura.gui.HiiroSakuraScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod(HiiroSakura.MOD_ID, dist = [Dist.CLIENT])
class NeoforgeHiirosakuraClient(eventBus: IEventBus, modContainer: ModContainer) {
    init {
        //模组菜单配置接口
        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, modListScreen -> HiiroSakuraScreen().apply { parentScreen = modListScreen } }
        )
        eventBus.addListener(::setup)
    }

    private fun setup(event: FMLClientSetupEvent) {
        HiiroSakuraClient.init()
    }
}