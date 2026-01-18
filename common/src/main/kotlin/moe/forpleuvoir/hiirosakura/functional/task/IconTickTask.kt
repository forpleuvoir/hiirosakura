package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.ibukigourd.task.TaskExecutor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

open class IconTickTask(
    name: String,
    setting: TickTask.Setting,
    executeOn: ExecuteOn,
    executorType: ExecutorType,
    executor: TaskExecutor<Minecraft>,
    icon: Item,
) : HSTickTask(name, setting, executeOn, executorType, executor) {

    companion object : Deserializer<IconTickTask> {

        val empty: IconTickTask
            get() = IconTickTask(
                "",
                TickTask.Setting(0, 1, 1),
                StartTick,
                ExecutorType.Script,
                ScriptExecutor(""),
                Items.AIR,
            )

        fun HSTickTask.withIcon(icon: Item) = IconTickTask(name, setting, executeOn, executorType, executor, icon)

        override fun deserialization(serializeElement: SerializeElement): IconTickTask {
            return serializeElement.checkType<SerializeObject, IconTickTask> {
                val type = ExecutorType.valueOf(it["executor_type"]!!.asString)
                IconTickTask(
                    name = it["name"]!!.asString,
                    setting = TickTask.Setting.deserialization(it["setting"]!!),
                    executeOn = ExecuteOn.valueOf(it["execute_on"]!!.asString),
                    executorType = type,
                    executor = type.deserialization(it["executor"]!!),
                    icon = BuiltInRegistries.ITEM.get(Identifier.parse(it["icon"]!!.asString)).get().value(),
                )
            }.getOrThrow()
        }

    }

    var iconStack = ItemStack(icon)
        private set

    var icon: Item = icon
        set(value) {
            field = value
            iconStack = ItemStack(value)
        }

    override fun fromTask(task: HSTickTask) {
        super.fromTask(task)
        if (task is IconTickTask) this.icon = task.icon
    }

    override fun setting(delay: Int, period: Int, times: Int): IconTickTask =
        IconTickTask(name, TickTask.Setting(delay, period, times), executeOn, executorType, executor, icon)

    override fun serialization(): SerializeElement = super.serialization().apply {
        asObject["icon"] = icon.serialization
    }
}