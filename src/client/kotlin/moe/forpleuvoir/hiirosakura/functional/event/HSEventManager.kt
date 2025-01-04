package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.ibukigourd.event.IbukiGourdEventManager
import moe.forpleuvoir.ibukigourd.event.events.server.ServerCommandRegisterEvent
import moe.forpleuvoir.ibukigourd.event.events.server.ServerLifecycleEvent.ServerStartedEvent
import moe.forpleuvoir.ibukigourd.event.events.server.ServerLifecycleEvent.ServerStartingEvent
import moe.forpleuvoir.ibukigourd.event.events.server.ServerLifecycleEvent.ServerStoppedEvent
import moe.forpleuvoir.ibukigourd.event.events.server.ServerLifecycleEvent.ServerStoppingEvent
import moe.forpleuvoir.ibukigourd.event.events.server.ServerSavingEvent
import moe.forpleuvoir.nebula.event.Event
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import java.util.*
import kotlin.reflect.KClass

@EventSubscriber
object HSEventManager : HiiroSakuraData {

    override val key: String get() = "event_manager"

    private val subscribers = mutableListOf<HSEventSubscriber>()

    private val unsubscribableEvents = setOf(
        ServerCommandRegisterEvent::class,
        ServerStartedEvent::class,
        ServerStartingEvent::class,
        ServerStoppedEvent::class,
        ServerStoppingEvent::class,
        ServerSavingEvent::class
    )

    val subscribableEvents by lazy {
        IbukiGourdEventManager.eventSet().filter {
            it !in unsubscribableEvents
        }.toSet()
    }

    val subscriberList: List<HSEventSubscriber> get() = subscribers.toList()

    @Subscriber(greedy = true)
    fun onEvent(event: Event) {
        subscribers.filter { it.eventType.isInstance(event) && it.enabled }
            .forEach { it.onEvent(event) }
    }

    fun add(eventSubscriber: HSEventSubscriber) {
        subscribers.add(eventSubscriber)
    }

    operator fun set(index: Int, task: HSEventSubscriber) {
        subscribers[index] = task
    }

    fun replace(origin: HSEventSubscriber, new: HSEventSubscriber) {
        subscribers.indexOf(origin).let {
            subscribers[it] = new
        }
    }

    fun remove(task: HSEventSubscriber) {
        subscribers.remove(task)
    }

    fun remove(index: Int) {
        subscribers.removeAt(index)
    }

    fun moveUp(index: Int) {
        if (index - 1 in 0..subscribers.lastIndex) {
            Collections.swap(subscribers, index, index - 1)
        }
    }

    fun moveDown(index: Int) {
        if (index + 1 in 0..subscribers.lastIndex) {
            Collections.swap(subscribers, index, index + 1)
        }
    }

    override fun serialization(): SerializeElement = serializeObject {
        "subscribers" to subscribers
    }

    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.checkType<SerializeObject, Unit> {
            subscribers.clear()
            it["subscribers"]!!.asArray.forEach { subscriber ->
                subscribers.add(HSEventSubscriber.deserialization(subscriber))
            }
        }.getOrThrow()
    }

}