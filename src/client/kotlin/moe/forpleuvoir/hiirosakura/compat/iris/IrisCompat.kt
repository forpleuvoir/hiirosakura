package moe.forpleuvoir.hiirosakura.compat.iris

import moe.forpleuvoir.hiirosakura.mixin.client.compat.BufferSourceWrapperAccessor
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.util.loader
import net.irisshaders.iris.Iris
import net.irisshaders.iris.layer.BufferSourceWrapper
import net.minecraft.client.render.OutlineVertexConsumerProvider
import net.minecraft.client.render.VertexConsumerProvider
import java.util.function.Consumer

object IrisCompat {

    private val log = logger()

    val isIrisLoaded by lazy {
        runCatching {
            val hasIrisMod = loader.allMods.any { it.metadata.id == Iris.MODID }
            if (hasIrisMod) {
                log.info("{} mod is loaded", Iris.MODID)
            }
            hasIrisMod
        }.getOrDefault(false)
    }

    @JvmStatic
    fun getImmediate(vertexConsumerProvider: VertexConsumerProvider): VertexConsumerProvider.Immediate? {
        when(vertexConsumerProvider){
            is VertexConsumerProvider.Immediate -> return vertexConsumerProvider
            is OutlineVertexConsumerProvider -> return vertexConsumerProvider.parent
        }
        if (isIrisLoaded) {
            if (vertexConsumerProvider is BufferSourceWrapper) {
                val provider = vertexConsumerProvider.run {
                    (this as BufferSourceWrapperAccessor).`hiirosakura$getBufferSource`()
                }
                return when (provider) {
                    is VertexConsumerProvider.Immediate -> provider
                    is OutlineVertexConsumerProvider    -> provider.parent
                    else                                -> null
                }
            }
        }
        return null
    }

    @JvmStatic
    fun isImmediate(vertexConsumerProvider: VertexConsumerProvider, action: Consumer<VertexConsumerProvider.Immediate>) {
        getImmediate(vertexConsumerProvider)?.let {
            action.accept(it)
        }
    }

}