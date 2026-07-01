package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.requireString
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

class KeyBindTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    icon: Item,
    val keybind: Keybind,
) : IconTickTask(name, setting, executeOn, executorType, executor, icon) {

    companion object : Deserializer<KeyBindTickTask> {

        val empty: KeyBindTickTask
            get() = KeyBindTickTask(
                "",
                TickTask.Setting(0, 1, 1),
                StartTick,
                ExecutorType.Script,
                ScriptExecutor(""),
                Items.AIR,
                Keybind()
            )

        override fun deserialization(data: SerializeElement): Result<KeyBindTickTask> = DeserializationException.runCatching {
            data.checkType<SerializeObject, KeyBindTickTask> {
                val type = ExecutorType.valueOf(it.requireString("executor_type"))
                KeyBindTickTask(
                    name = it.requireString("name"),
                    setting = TickTask.Setting.deserialization(it.requireKey("setting")).getOrThrow(),
                    executeOn = ExecuteOn.valueOf(it.requireString("execute_on")),
                    executorType = type,
                    executor = type.deserialization(it.requireKey("executor")),
                    icon = BuiltInRegistries.ITEM.get(Identifier.parse(it.requireString("icon"))).get().value(),
                    keybind = Keybind().apply { deserialization(it["key_bind"]!!) }
                )
            }
        }

    }

    init {
        keybind.action = {
            execute()
        }
        updateKeyBindName()
    }

    private fun updateKeyBindName() {
        keybind.name = HSLang.taskManager.appendLiteral(" → ").appendLiteral(name)

    }

    override fun fromTask(task: HSTickTask) {
        super.fromTask(task)
        updateKeyBindName()
    }

    override fun setting(delay: Int, period: Int, times: Int): KeyBindTickTask =
        KeyBindTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon, keybind)

    override fun serialization(): SerializeElement = super.serialization().apply {
        requireType<SerializeObject>()["key_bind"] = keybind.serialization()
    }
}