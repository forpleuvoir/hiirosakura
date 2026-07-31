package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.event.Registration
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.extensions.requireBoolean
import moe.forpleuvoir.nebula.serialization.extensions.requireString

class HSEventSubscriber(
    var name: String,
    var enabled: Boolean,
    var eventTypeId: String,
    var executorType: ExecutorType,
    var executor: Executor
) {

    private var registration: Registration? = null

    enum class ExecutorType {
        Command {
            override fun deserialization(serializeElement: SerializeElement): CommandExecutor =
                serializeElement.checkType<SerializePrimitive, CommandExecutor> { CommandExecutor(it.value.requireType()) }
        },
        Message {
            override fun deserialization(serializeElement: SerializeElement): MessageExecutor =
                serializeElement.checkType<SerializePrimitive, MessageExecutor> { MessageExecutor(it.value.requireType()) }
        },
        Script {
            override fun deserialization(serializeElement: SerializeElement): ScriptExecutor =
                serializeElement.checkType<SerializePrimitive, ScriptExecutor> { ScriptExecutor(it.value.requireType()) }
        },
        TickTask {
            override fun deserialization(serializeElement: SerializeElement): HSTickTask =
                HSTickTask.deserialization(serializeElement).getOrThrow()
        };

        abstract fun deserialization(serializeElement: SerializeElement): Executor
    }

    fun onEvent(eventContext: Any) {
        if(!enabled) return
        when (executorType) {
            ExecutorType.Message, ExecutorType.Command -> executor.execute()
            ExecutorType.Script                        -> {
                (executor as ScriptExecutor).let {
                    it["eventContext"] = eventContext
                    executor.execute()
                }
            }

            ExecutorType.TickTask                      -> {
                (executor as HSTickTask).let { task ->
                    if (task.executorType == HSTickTask.ExecutorType.Script) {
                        (task.executor as ScriptExecutor).let {
                            it["eventContext"] = eventContext
                        }
                    }
                    task.execute()
                }
            }
        }

    }

    fun subscribe() {
        unsubscribe()
        if (!enabled) return
        val type = EventTypes[eventTypeId]
        if (type == null) {
            log.warn("Unknown event type: {}", eventTypeId)
            return
        }
        registration = type.subscribe { onEvent(it) }
    }

    fun unsubscribe() {
        registration?.unregister()
        registration = null
    }

    companion object : Codec<HSEventSubscriber> {

        private val log = logger()

        val empty get() = HSEventSubscriber("", true, EventTypes.ids.first(), ExecutorType.Script, ScriptExecutor(""))

        private fun resolveEventTypeId(raw: String): String =
            EventTypes[raw]?.let { raw } ?: EventTypes.ids.first().also {
                log.warn("Unknown event type '{}' found in configuration, falling back to '{}'", raw, it);
            }

        override fun deserialization(data: SerializeElement): Result<HSEventSubscriber> = DeserializationException.runCatching {
            data.checkType<SerializeObject, HSEventSubscriber> {
                val type = ExecutorType.valueOf(it.requireString("executor_type"))
                HSEventSubscriber(
                    name = it.requireString("name"),
                    enabled = it.requireBoolean("enabled"),
                    eventTypeId = resolveEventTypeId(it.requireString("event_type")),
                    executorType = type,
                    executor = type.deserialization(it.requireKey("executor"))
                )
            }
        }

        override fun serialization(target: HSEventSubscriber): SerializeElement = SerializeObject.build {
            "name"(target.name)
            "enabled"(target.enabled)
            "event_type"(target.eventTypeId)
            "executor_type"(target.executorType.name)
            "executor"(target.executor)
        }
    }
}