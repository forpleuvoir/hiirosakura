package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.ModInitializerEvent
import moe.forpleuvoir.ibukigourd.util.loader
import moe.forpleuvoir.nebula.event.EventBus
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.metadata.ModMetadata

object HiiroSakura : ModInitializer {

    val log = logger()

    const val MOD_ID: String = "hiirosakura"

    const val MOD_NAME: String = "Hiiro Sakura"

    val metadata: ModMetadata get() = loader.getModContainer(MOD_ID).get().metadata

    override fun onInitialize() {
        loader.getModContainer(MOD_ID).ifPresent { modContainer ->
            EventBus.broadcast(ModInitializerEvent(modContainer.metadata))
        }
    }


}