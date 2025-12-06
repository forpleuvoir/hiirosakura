package moe.forpeluvoir.hiirosakura.neoforgeevent

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.CommonApiLoader
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent

@EventBusSubscriber(modid = HiiroSakura.MOD_ID)
object ReloadListenerRegistry {

    @SubscribeEvent
    fun onAddClientReloadListeners(event: AddClientReloadListenersEvent) {
        event.addListener(CommonApiLoader.RESOURCE_ID, CommonApiLoader)
    }

}