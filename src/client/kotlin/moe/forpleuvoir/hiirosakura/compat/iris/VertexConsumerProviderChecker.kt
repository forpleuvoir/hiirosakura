package moe.forpleuvoir.hiirosakura.compat.iris

import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.util.loader
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.irisshaders.iris.layer.BufferSourceWrapper
import net.minecraft.client.render.VertexConsumerProvider
import java.util.function.Consumer

@EventSubscriber
object VertexConsumerProviderChecker {

    private val log = loader.logger()

    private const val IRIS_MOD_ID = "iris"

    var isIrisLoaded = false
        private set


    @Subscriber
    fun init(event: ClientLifecycleEvent.ClientStartedEvent) {
        loader.allMods
            .any { it.metadata.id == IRIS_MOD_ID }
            .let {
                isIrisLoaded = it
                log.info("iris mod is loaded")
            }
    }

    @JvmStatic
    fun getImmediate(vertexConsumerProvider: VertexConsumerProvider): VertexConsumerProvider.Immediate? {
        if (vertexConsumerProvider is VertexConsumerProvider.Immediate) return vertexConsumerProvider
        if (isIrisLoaded) {
            if (vertexConsumerProvider is BufferSourceWrapper) {
                vertexConsumerProvider.getImmediate()?.let { return it }
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

    fun BufferSourceWrapper.getImmediate(): VertexConsumerProvider.Immediate? {
        return runCatching {
            val filed = this::class.java.declaredFields[0]
            filed.isAccessible = true
            val vertexConsumerProvider = filed.get(this)
            vertexConsumerProvider as? VertexConsumerProvider.Immediate
        }.getOrNull()
    }

}