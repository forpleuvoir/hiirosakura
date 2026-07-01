package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec

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
        TODO("打开屏幕")
    }

}