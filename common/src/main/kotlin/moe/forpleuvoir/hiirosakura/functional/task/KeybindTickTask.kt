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

class KeybindTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    icon: Item,
    val keybind: Keybind,
) : IconTickTask(name, setting, executeOn, executorType, executor, icon) {

    companion object : Deserializer<KeybindTickTask> {

        val empty: KeybindTickTask
            get() = KeybindTickTask(
                "",
                TickTask.Setting(0, 1, 1),
                StartTick,
                ExecutorType.Script,
                ScriptExecutor(""),
                Items.WRITTEN_BOOK,
                Keybind()
            )

        override fun deserialization(data: SerializeElement): Result<KeybindTickTask> = DeserializationException.runCatching {
            data.checkType<SerializeObject, KeybindTickTask> {
                val type = ExecutorType.valueOf(it.requireString("executor_type"))
                KeybindTickTask(
                    name = it.requireString("name"),
                    setting = TickTask.Setting.deserialization(it.requireKey("setting")).getOrThrow(),
                    executeOn = ExecuteOn.valueOf(it.requireString("execute_on")),
                    executorType = type,
                    executor = type.deserialization(it.requireKey("executor")),
                    icon = BuiltInRegistries.ITEM.get(Identifier.parse(it.requireString("icon"))).get().value(),
                    keybind = Keybind().apply { deserialization(it["keybind"]!!) }
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

    fun copy(
        name: String = this.name,
        setting: TickTask.Setting = this.setting,
        executeOn: ExecuteOn = this.executeOn,
        executorType: ExecutorType = this.executorType,
        executor: TaskExecutor<Minecraft> = this.executor,
        icon: Item = this.icon,
        keybind: Keybind = this.keybind
    ): KeybindTickTask = KeybindTickTask(name, setting, executeOn, executorType, executor, icon, keybind)

    private fun updateKeyBindName() {
        keybind.name = HSLang.Task.manager.appendLiteral(" → ").appendLiteral(name)
    }

    override fun setting(delay: Int, period: Int, times: Int): KeybindTickTask =
        KeybindTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon, keybind)

    override fun serialization(): SerializeElement = super.serialization().apply {
        requireType<SerializeObject>()["keybind"] = keybind.serialization()
    }
}