package moe.forpleuvoir.hiirosakura.fabricevent

import moe.forpleuvoir.hiirosakura.functional.script.CommonScriptLoader
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

object ReloadListenerRegistry {

    fun register() {
        registerClientResource(CommonScriptLoader.RESOURCE_ID, CommonScriptLoader)
    }

    private fun registerClientResource(id: Identifier, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id, listener)
    }

    private fun registerServerData(id: Identifier, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(id, listener)
    }

}