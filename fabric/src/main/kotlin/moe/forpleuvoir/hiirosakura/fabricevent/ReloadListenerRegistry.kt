package moe.forpleuvoir.hiirosakura.fabricevent

import moe.forpleuvoir.hiirosakura.functional.script.CommonScriptLoader
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object ReloadListenerRegistry {

    fun register() {
        registerClientResource(CommonScriptLoader.RESOURCE_ID, CommonScriptLoader)
    }

    private fun registerClientResource(id: ResourceLocation, listener: PreparableReloadListener) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(IGResourceReloadListener(id, listener))
    }

    private fun registerServerData(id: ResourceLocation, listener: PreparableReloadListener) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(IGResourceReloadListener(id, listener))

    }

    private class IGResourceReloadListener(
        val id: ResourceLocation,
        val listener: PreparableReloadListener
    ) : IdentifiableResourceReloadListener {
        override fun getFabricId(): ResourceLocation = id

        override fun reload(
            barrier: PreparableReloadListener.PreparationBarrier,
            manager: ResourceManager,
            backgroundExecutor: Executor,
            gameExecutor: Executor
        ): CompletableFuture<Void> {
            return listener.reload(barrier, manager, backgroundExecutor, gameExecutor)
        }
    }

}