package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.nebula.event.Event
import moe.forpleuvoir.nebula.event.eventName
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import kotlin.reflect.KClass

class HSEventSubscriber(
    var name: String,
    var enabled: Boolean,
    var eventType: KClass<out Event>,
    var executorType: ExecutorType,
    var executor: Executor
) : Serializable {

    enum class ExecutorType {
        Command {
            override fun deserialization(serializeElement: SerializeElement): CommandExecutor =
                serializeElement.checkType<SerializePrimitive, CommandExecutor> { CommandExecutor(it.asString) }.getOrThrow()
        },
        Message {
            override fun deserialization(serializeElement: SerializeElement): MessageExecutor =
                serializeElement.checkType<SerializePrimitive, MessageExecutor> { MessageExecutor(it.asString) }.getOrThrow()
        },
        Script {
            override fun deserialization(serializeElement: SerializeElement): ScriptExecutor =
                serializeElement.checkType<SerializePrimitive, ScriptExecutor> { ScriptExecutor(it.asString) }.getOrThrow()
        },
        TickTask {
            override fun deserialization(serializeElement: SerializeElement): HSTickTask =
                HSTickTask.deserialization(serializeElement)
        };

        abstract fun deserialization(serializeElement: SerializeElement): Executor
    }

    fun onEvent(event: Event) {
        when (executorType) {
            ExecutorType.Message, ExecutorType.Command -> executor.execute()
            ExecutorType.Script                        -> {
                (executor as ScriptExecutor).let {
                    it["event"] = event
                    executor.execute()
                }
            }

            ExecutorType.TickTask                      -> {
                (executor as HSTickTask).let { task ->
                    if (task.executorType == HSTickTask.ExecutorType.Script) {
                        (task.executor as ScriptExecutor).let {
                            it["event"] = event
                        }
                    }
                    task.execute()
                }
            }
        }

    }

    fun fromEventSubscriber(eventSubscriber: HSEventSubscriber) {
        this.name = eventSubscriber.name
        this.enabled = eventSubscriber.enabled
        this.eventType = eventSubscriber.eventType
        this.executorType = eventSubscriber.executorType
        this.executor = eventSubscriber.executor
    }

    companion object : Deserializer<HSEventSubscriber> {

        val empty get() = HSEventSubscriber("", true, HSEventManager.subscribableEvents.first(), ExecutorType.Script, ScriptExecutor(""))

        override fun deserialization(serializeElement: SerializeElement): HSEventSubscriber =
            serializeElement.checkType<SerializeObject, HSEventSubscriber> {
                val type = ExecutorType.valueOf(it["executor_type"]!!.asString)
                HSEventSubscriber(
                    name = it["name"]!!.asString,
                    enabled = it["enabled"]!!.asBoolean,
                    eventType = HSEventManager.subscribableEvents.first { event -> event.eventName == it["event_type"]!!.asString },
                    executorType = ExecutorType.valueOf(it["executor_type"]!!.asString),
                    executor = type.deserialization(it["executor"]!!)
                )
            }.getOrThrow()

    }

    override fun serialization(): SerializeElement = serializeObject {
        "name" to name
        "enabled" to enabled
        "event_type" to eventType.eventName
        "executor_type" to executorType.name
        "executor" to executor.serialization()
    }

}

