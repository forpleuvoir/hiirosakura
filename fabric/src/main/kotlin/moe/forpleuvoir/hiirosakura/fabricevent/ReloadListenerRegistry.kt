package moe.forpleuvoir.hiirosakura.fabricevent

import moe.forpleuvoir.hiirosakura.functional.script.CommonApiLoader
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

object ReloadListenerRegistry {

    fun register() {
        registerClientResource(CommonApiLoader.RESOURCE_ID, CommonApiLoader)
    }

    private fun registerClientResource(id: ResourceLocation, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id, listener)
    }

    private fun registerServerData(id: ResourceLocation, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(id, listener)
    }

}