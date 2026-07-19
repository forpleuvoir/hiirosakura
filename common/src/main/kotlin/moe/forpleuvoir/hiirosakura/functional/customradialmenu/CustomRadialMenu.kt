package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.rememberRadialMenuState
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.world.item.ItemStack

class CustomRadialMenu(
    shortcuts: Keybind = Keybind(
        defaultSetting = KeybindSetting(
            passthrough = true,
            strict = false,
            trigger = KeyTriggerTiming.Press,
        )
    ),
    var setting: RadialMenuSetting = RadialMenuSetting(),
    tasks: List<IconTickTask>
) {

    companion object : Codec<CustomRadialMenu> {
        override fun deserialization(data: SerializeElement): Result<CustomRadialMenu> = DeserializationException.runCatching {
            data.checkType<SerializeObject, CustomRadialMenu> {
                CustomRadialMenu(
                    Keybind(
                        defaultSetting = KeybindSetting(
                            passthrough = true,
                            strict = false,
                            trigger = KeyTriggerTiming.Press,
                        )
                    ).apply { deserialization(it["shortcuts"]!!) },
                    RadialMenuSetting.deserialization(it.requireKey("setting")).getOrThrow(),
                    it.requireKey("tasks").requireType<SerializeArray>().map { task -> IconTickTask.deserialization(task).getOrThrow() }
                )
            }
        }

        override fun serialization(target: CustomRadialMenu): SerializeElement = SerializeObject.build {
            "shortcuts" to target.shortcuts.serialization()
            "setting" to RadialMenuSetting.serialization(target.setting)
            "tasks" arr {
                target.tasks.forEach { add(it.serialization()) }
            }
        }

    }

    val shortcuts: Keybind = shortcuts.apply { action = { open() } }

    val tasks: ArrayList<IconTickTask> = ArrayList(tasks)

    fun load() {
        InputHandler.register(shortcuts)
    }

    fun unload() {
        InputHandler.unregister(shortcuts)
    }

    fun open() = openComposeScreen {
        val s = setting
        RadialMenu(
            options = tasks.toList(),
            state = rememberRadialMenuState(),
            innerRadius = s.innerRadius.dp,
            outerRadius = s.outerRadius.dp,
            optionRadius = s.optionRadius.dp,
            startAngleDegree = -90f,
            gap = s.gap,
            optionsPerPage = s.pageSize,
            idleOuterColor = s.outerColor.toComposeColor,
            idleInnerColor = s.innerColor.toComposeColor,
            selectedOuterColor = s.outerSelectedColor.toComposeColor,
            selectedInnerColor = s.innerSelectedColor.toComposeColor,
            onOptionClick = { task, _ -> task?.execute() },
            optionContent = { task, _ ->
                val stack = remember(task.icon) { ItemStack(task.icon) }
                if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
            },
            centerContent = { task ->
                if (task != null) Text(task.nameAsInlineStyleText)
            },
        )
    }

}