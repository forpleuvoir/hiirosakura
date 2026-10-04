package moe.forpleuvoir.hiirosakura.functional.event

import androidx.compose.runtime.mutableStateListOf
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build

object HSEventManager : HiiroSakuraData {

    private val log = logger()

    override val key: String get() = "event_manager"

    private var nextKey = 0L

    val subscribers: List<Keyed<HSEventSubscriber>>
        field = mutableStateListOf()

    fun add(eventSubscriber: HSEventSubscriber) {
        eventSubscriber.unsubscribe()
        subscribers.add(Keyed(nextKey++, eventSubscriber))
        eventSubscriber.subscribe()
    }

    operator fun set(index: Int, task: HSEventSubscriber) {
        subscribers.getOrNull(index)?.value?.unsubscribe()
        subscribers[index] = Keyed(nextKey++, task)
        task.subscribe()
    }

    /**
     * 切换启用状态并同步事件注册：关闭时注销，开启时重新注册。
     *
     * [HSEventSubscriber.subscribe] 内部先注销、再按当前状态决定是否注册，两个方向都只需调它。
     */
    fun setEnabled(subscriber: HSEventSubscriber, enabled: Boolean) {
        subscriber.enabled = enabled
        subscriber.subscribe()
    }

    /** 按下标切换启用状态，见 [setEnabled]。 */
    fun setEnabled(index: Int, enabled: Boolean) {
        subscribers.getOrNull(index)?.value?.let { setEnabled(it, enabled) }
    }

    fun moveElement(fromIndex: Int, toIndex: Int) {
        subscribers.moveElement(fromIndex, toIndex)
    }

    fun remove(index: Int) {
        subscribers.getOrNull(index)?.value?.unsubscribe()
        subscribers.removeAt(index)
    }

    fun subscribeAll() {
        subscribers.values().forEach { it.subscribe() }
    }

    fun unsubscribeAll() {
        subscribers.values().forEach { it.unsubscribe() }
    }

    override fun serialization(): SerializeElement = SerializeObject.build {
        "subscribers" arr {
            subscribers.values().forEach {
                add(HSEventSubscriber.serialization(it))
            }
        }
    }

    override fun deserialization(data: SerializeElement) {
        data.checkType<SerializeObject, Unit> {
            unsubscribeAll()
            subscribers.clear()
            it["subscribers"]!!.asArray!!.forEach { subscriber ->
                runCatching {
                    add(HSEventSubscriber.deserialization(subscriber).getOrThrow())
                }.onFailure {
                    log.warn(it)
                }
            }
        }
    }

}