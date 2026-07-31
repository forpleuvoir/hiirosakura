package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
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

open class IconTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    val icon: Item,
) : HSTickTask(name, setting, executeOn, executorType, executor) {

    companion object : Deserializer<IconTickTask> {

        val empty: IconTickTask
            get() = IconTickTask(
                "",
                TickTask.Setting(0, 1, 1),
                StartTick,
                ExecutorType.Script,
                ScriptExecutor(""),
                Items.WRITTEN_BOOK,
            )

        fun HSTickTask.withIcon(icon: Item) = IconTickTask(name, setting, executeOn, executorType, executor, icon)

        override fun deserialization(data: SerializeElement): Result<IconTickTask> = DeserializationException.runCatching {
            data.checkType<SerializeObject, IconTickTask> {
                val type = ExecutorType.valueOf(it.requireString("executor_type"))
                IconTickTask(
                    name = it.requireString("name"),
                    setting = TickTask.Setting.deserialization(it.requireKey("setting")).getOrThrow(),
                    executeOn = ExecuteOn.valueOf(it.requireString("execute_on")),
                    executorType = type,
                    executor = type.deserialization(it.requireKey("executor")),
                    icon = BuiltInRegistries.ITEM.get(Identifier.parse(it.requireString("icon"))).get().value(),
                )
            }
        }

    }

    override fun setting(delay: Int, period: Int, times: Int): IconTickTask =
        IconTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon)

    override fun serialization(): SerializeElement = super.serialization().apply {
        requireType<SerializeObject>()["icon"] = icon.serialization
    }
}