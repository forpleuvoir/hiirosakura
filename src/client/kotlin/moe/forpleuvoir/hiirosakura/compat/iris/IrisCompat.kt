package moe.forpleuvoir.hiirosakura.compat.iris

import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.util.loader
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import net.irisshaders.iris.Iris
import net.irisshaders.iris.layer.BufferSourceWrapper
import net.minecraft.client.render.VertexConsumerProvider
import java.util.function.Consumer

@EventSubscriber
object IrisCompat {

    private val log = loader.logger()

    var isIrisLoaded = false
        private set


    @Subscriber
    fun init(event: ClientLifecycleEvent.ClientStartedEvent) {
        runCatching {
            log.info("{} mod is loaded", Iris.MODID)
            isIrisLoaded = true
        }
    }

    @JvmStatic
    fun getImmediate(vertexConsumerProvider: VertexConsumerProvider): VertexConsumerProvider.Immediate? {
        if (vertexConsumerProvider is VertexConsumerProvider.Immediate) return vertexConsumerProvider
        if (isIrisLoaded) {
            if (vertexConsumerProvider is BufferSourceWrapper) {
                return vertexConsumerProvider.runCatching {
                    val filed = this::class.java.declaredFields[0]
                    filed.isAccessible = true
                    val vertexConsumerProvider = filed.get(this)
                    vertexConsumerProvider as? VertexConsumerProvider.Immediate
                }.getOrNull()
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