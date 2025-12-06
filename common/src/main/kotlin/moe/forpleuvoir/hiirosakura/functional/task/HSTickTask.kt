package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.executor.Executor
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.KeyCode
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

open class HSTickTask(
    var name: String,
    var setting: TickTask.Setting,
    var executeOn: ExecuteOn,
    var executorType: ExecutorType,
    var executor: TaskExecutor<Minecraft>
) : Executor {

    enum class ExecutorType {
        Command {
            override fun deserialization(serializeElement: SerializeElement): CommandExecutor =
                serializeElement.checkType<SerializePrimitive, CommandExecutor> { CommandExecutor(it.asString) }.getOrThrow()

            override fun fromString(content: String): TaskExecutor<Minecraft> = CommandExecutor(content)
        },
        Message {
            override fun deserialization(serializeElement: SerializeElement): MessageExecutor =
                serializeElement.checkType<SerializePrimitive, MessageExecutor> { MessageExecutor(it.asString) }.getOrThrow()

            override fun fromString(content: String): TaskExecutor<Minecraft> = MessageExecutor(content)
        },
        Script {
            override fun deserialization(serializeElement: SerializeElement): ScriptExecutor =
                serializeElement.checkType<SerializePrimitive, ScriptExecutor> { ScriptExecutor(it.asString) }.getOrThrow()

            override fun fromString(content: String): TaskExecutor<Minecraft> = ScriptExecutor(content)
        };

        abstract fun deserialization(serializeElement: SerializeElement): TaskExecutor<Minecraft>

        abstract fun fromString(content: String): TaskExecutor<Minecraft>
    }

    enum class ExecuteOn {
        StartTick, EndTick
    }

    companion object : Deserializer<HSTickTask> {

        val empty get() = HSTickTask("", TickTask.Setting(0, 1, 1), StartTick, ExecutorType.Script, ScriptExecutor(""))

        override fun deserialization(serializeElement: SerializeElement): HSTickTask {
            return serializeElement.checkType<HSTickTask> {
                check<SerializeObject> {
                    val type = ExecutorType.valueOf(it["executor_type"]!!.asString)
                    HSTickTask(
                        name = it["name"]!!.asString,
                        setting = TickTask.Setting.deserialization(it["setting"]!!),
                        executeOn = ExecuteOn.valueOf(it["execute_on"]!!.asString),
                        executorType = type,
                        executor = type.deserialization(it["executor"]!!)
                    )
                }
            }.getOrThrow()
        }

    }

    override fun serialization(): SerializeElement = serializeObject {
        "name" to name
        "setting" to setting.serialization()
        "execute_on" to executeOn
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

class KeyBindTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    icon: Item,
    val keyBind: KeyBind,
) : HSTickTask(name, setting, executeOn, executorType, executor) {

    companion object : Deserializer<KeyBindTickTask> {

        val empty: KeyBindTickTask
            get() = KeyBindTickTask(
                "",
                TickTask.Setting(0, 1, 1),
                StartTick,
                ExecutorType.Script,
                ScriptExecutor(""),
                Items.MELON,
                KeyBind()
            )

        fun HSTickTask.withKeyBind(vararg keyCode: KeyCode) =
            KeyBindTickTask(name, setting, executeOn, executorType, executor, if (this is KeyBindTickTask) this.icon else Items.MELON, KeyBind(*keyCode))

        override fun deserialization(serializeElement: SerializeElement): KeyBindTickTask {
            return serializeElement.checkType<SerializeObject, KeyBindTickTask> {
                val type = ExecutorType.valueOf(it["executor_type"]!!.asString)
                KeyBindTickTask(
                    name = it["name"]!!.asString,
                    setting = TickTask.Setting.deserialization(it["setting"]!!),
                    executeOn = ExecuteOn.valueOf(it["execute_on"]!!.asString),
                    executorType = type,
                    executor = type.deserialization(it["executor"]!!),
                    icon = BuiltInRegistries.ITEM.get(ResourceLocation.parse(it["icon"]!!.asString)).get().value(),
                    keyBind = KeyBind().apply { deserialization(it["key_bind"]!!) }
                )
            }.getOrThrow()
        }

    }

    init {
        keyBind.action = {
            execute()
        }
        updateKeyBindName()
    }

    private fun updateKeyBindName() {
        keyBind.name(HSLang.taskManager.appendLiteral("=>").appendLiteral(name))

    }

    var iconStack = ItemStack(icon)
        private set

    var icon: Item = icon
        set(value) {
            field = value
            iconStack = ItemStack(value)
        }

    override fun fromTask(task: HSTickTask) {
        this.name = task.name
        this.setting = task.setting
        this.executeOn = task.executeOn
        this.executorType = task.executorType
        if (task is KeyBindTickTask) this.icon = task.icon
        this.executor = task.executor
        updateKeyBindName()
    }

    override fun setting(delay: Int, period: Int, times: Int): KeyBindTickTask =
        KeyBindTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon, keyBind)

    override fun serialization(): SerializeElement = serializeObject {
        "name" to name
        "setting" to setting.serialization()
        "execute_on" to executeOn
        "executor_type" to executorType.name
        "icon" to BuiltInRegistries.ITEM.getKey(icon).toString()
        "key_bind" to keyBind.serialization()
        "executor" to executor.serialization()
    }
}