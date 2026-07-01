package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.common.util.checkType

object HSEventManager : HiiroSakuraData {

    private val log = logger()

    override val key: String get() = "event_manager"

    private val subscribers = mutableListOf<HSEventSubscriber>()

    /** 对外数据访问点(下拉框用),返回当前可订阅事件类型 id 列表。 */
    val subscribableEvents: List<String> get() = EventTypes.ids

    val subscriberList: List<HSEventSubscriber> get() = subscribers.toList()

    fun add(eventSubscriber: HSEventSubscriber) {
        eventSubscriber.unsubscribe()
        subscribers.add(eventSubscriber)
        eventSubscriber.subscribe()
    }

    operator fun set(index: Int, task: HSEventSubscriber) {
        subscribers[index].unsubscribe()
        subscribers[index] = task
        task.subscribe()
    }

    fun replace(origin: HSEventSubscriber, new: HSEventSubscriber) {
        origin.unsubscribe()
        subscribers.indexOf(origin).let {
            subscribers[it] = new
        }
        new.subscribe()
    }

    fun remove(task: HSEventSubscriber) {
        task.unsubscribe()
        subscribers.remove(task)
    }

    fun remove(index: Int) {
        subscribers[index].unsubscribe()
        subscribers.removeAt(index)
    }

    fun subscribeAll() {
        subscribers.forEach { it.subscribe() }
    }

    fun unsubscribeAll() {
        subscribers.forEach { it.unsubscribe() }
    }

    override fun serialization(): SerializeElement = SerializeObject.build {
        "subscribers" arr {
            subscribers.forEach { HSEventSubscriber.serialization(it) }
        }
    }

    override fun deserialization(data: SerializeElement) {
        data.checkType<SerializeObject, Unit> {
            unsubscribeAll()
            subscribers.clear()
            it["subscribers"]!!.asArray!!.forEach { subscriber ->
                runCatching {
                    subscribers.add(HSEventSubscriber.deserialization(subscriber).getOrThrow().also { s -> s.subscribe() })
                }.onFailure {
                    log.warn(it)
                }
            }
        }
    }

}