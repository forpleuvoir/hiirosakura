package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

class KeyBindTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    icon: Item,
    val keyBind: KeyBind,
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
                KeyBind()
            )

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
        keyBind.name(HSLang.taskManager.appendLiteral("→").appendLiteral(name))

    }

    override fun fromTask(task: HSTickTask) {
        super.fromTask(task)
        updateKeyBindName()
    }

    override fun setting(delay: Int, period: Int, times: Int): KeyBindTickTask =
        KeyBindTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon, keyBind)

    override fun serialization(): SerializeElement = super.serialization().apply {
        asObject["key_bind"] = keyBind.serialization()
    }
}