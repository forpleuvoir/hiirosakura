package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.functional.event.events.*
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.event.events.client.ClientLifecycleEvent
import moe.forpleuvoir.ibukigourd.event.events.client.ClientTickEvent
import moe.forpleuvoir.ibukigourd.event.events.client.input.KeyboardEvent
import moe.forpleuvoir.ibukigourd.event.events.client.input.MouseEvent
import moe.forpleuvoir.nebula.event.Event
import moe.forpleuvoir.nebula.event.EventPriority
import moe.forpleuvoir.nebula.event.EventSubscriber
import moe.forpleuvoir.nebula.event.Subscriber
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import java.util.*

@EventSubscriber
object HSEventManager : HiiroSakuraData {

    private val log = logger()

    override val key: String get() = "event_manager"

    private val subscribers = mutableListOf<HSEventSubscriber>()

    val subscribableEvents = listOf(
        ServerJoinEvent::class,
        GameJoinEvent::class,
        GameExitEvent::class,
        DisconnectEvent::class,
        SoundPlayEvent::class,
        CommandSendEvent::class,
        MessageSendEvent::class,
        MessageReceiveEvent::class,
        BreakBlockEvent::class,
        PlayerAttackEvent::class,
        PlayerPickEvent::class,
        PlayerUseEvent::class,
        PlayerDeathEvent::class,
        PlayerRespawnEvent::class,
        KeyboardEvent.KeyPressEvent::class,
        KeyboardEvent.KeyReleaseEvent::class,
        MouseEvent.MousePressEvent::class,
        MouseEvent.MouseReleaseEvent::class,
        MouseEvent.MouseScrollEvent::class,
        MouseEvent.MouseMoveEvent::class,
        MouseEvent.MouseDraggingEvent::class,
        ClientLifecycleEvent.ClientStartedEvent::class,
        ClientLifecycleEvent.ClientStartingEvent::class,
        ClientLifecycleEvent.ClientStopEvent::class,
        ClientTickEvent.ClientTickEndEvent::class,
        ClientTickEvent.ClientTickStartEvent::class
    )

    val subscriberList: List<HSEventSubscriber> get() = subscribers.toList()

    @Subscriber(greedy = true, priority = EventPriority.HIGHEST)
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
                runCatching {
                    subscribers.add(HSEventSubscriber.deserialization(subscriber))
                }.onFailure {
                    log.warn(it)
                }
            }
        }.getOrThrow()
    }

}