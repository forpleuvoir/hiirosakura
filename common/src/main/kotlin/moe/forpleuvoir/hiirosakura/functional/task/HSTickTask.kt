package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.enum
import moe.forpleuvoir.nebula.serialization.codec.serialization
import moe.forpleuvoir.nebula.serialization.extensions.requireString
import net.minecraft.client.Minecraft

open class HSTickTask(
    name: String,
    var setting: TickTask.Setting,
    var executeOn: ExecuteOn,
    var executorType: ExecutorType,
    var executor: TaskExecutor<Minecraft>
) : Executor {

    enum class ExecutorType {
        Command {
            override fun deserialization(data: SerializeElement): CommandExecutor =
                data.checkType<SerializePrimitive, CommandExecutor> { CommandExecutor(it.value.requireType()) }

            override fun fromString(content: String): TaskExecutor<Minecraft> = CommandExecutor(content)
        },
        Message {
            override fun deserialization(data: SerializeElement): MessageExecutor =
                data.checkType<SerializePrimitive, MessageExecutor> { MessageExecutor(it.value.requireType()) }

            override fun fromString(content: String): TaskExecutor<Minecraft> = MessageExecutor(content)
        },
        Script {
            override fun deserialization(data: SerializeElement): ScriptExecutor =
                ScriptExecutor.deserialization(data).getOrThrow()

            override fun fromString(content: String): TaskExecutor<Minecraft> = ScriptExecutor(content)
        };

        abstract fun deserialization(data: SerializeElement): TaskExecutor<Minecraft>

        abstract fun fromString(content: String): TaskExecutor<Minecraft>
    }

    enum class ExecuteOn {
        StartTick, EndTick
    }

    companion object : Deserializer<HSTickTask> {

        val empty get() = HSTickTask("", TickTask.Setting(0, 1, 1), StartTick, ExecutorType.Script, ScriptExecutor(""))

        override fun deserialization(data: SerializeElement): Result<HSTickTask> = DeserializationException.runCatching {
            data.checkType<SerializeObject, HSTickTask> {
                val type = ExecutorType.valueOf(it.requireString("executor_type"))
                HSTickTask(
                    name = it.requireString("name"),
                    setting = TickTask.Setting.deserialization(it.requireKey("setting")).getOrThrow(),
                    executeOn = ExecuteOn.valueOf(it.requireString("execute_on")),
                    executorType = type,
                    executor = type.deserialization(it.requireKey("executor"))
                )
            }
        }

    }

    var name = name
        set(value) {
            field = value
            nameAsInlineStyleText = InlineStyleText(value)
        }

    var nameAsInlineStyleText = InlineStyleText(name)
        private set

    override fun serialization(): SerializeElement = SerializeObject.build {
        "name" to name
        "setting" to setting.serialization(TickTask.Setting)
        "execute_on" to Codec.enum<ExecuteOn>().serialization(executeOn)
        "executor_type" to executorType.name
        "executor" to executor.serialization()
    }

    open fun fromTask(task: HSTickTask) {
        this.name = task.name
        this.setting = task.setting
        this.executeOn = task.executeOn
        this.executorType = task.executorType
        this.executor = task.executor
    }

    fun asTickTask() = TickTask(setting, executor)

    override fun execute() {
        HSTickTaskScheduler.execute(this)
    }

    open fun setting(delay: Int = setting.delay, period: Int = setting.period, times: Int = setting.times) =
        HSTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor)

}