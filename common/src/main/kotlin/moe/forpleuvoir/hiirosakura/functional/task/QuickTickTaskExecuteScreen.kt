package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.ibukigourd.config.item.configKeybind
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configColor
import moe.forpleuvoir.nebula.config.item.configFloat
import moe.forpleuvoir.nebula.config.item.configInt

object QuickTickTaskExecuteScreen : ConfigGroup("quick_tick_task_execute") {

    val keybind by configKeybind(
        "keybind", Keybind(
            defaultSetting = KeybindSetting(
                passthrough = true,
                strict = false,
                trigger = KeyTriggerTiming.Press,
            )
        ) {
//            screen().open()
        })

    val innerColor by configColor("inner_color", Color.fromARGB(0x33000000))

    val outerColor by configColor("outer_color", Color.fromARGB(0x7F000000))

    val innerSelectedColor by configColor("inner_selected_color", Color.fromARGB(0x33FF8899))

    val outerSelectedColor by configColor("outer_selected_color", Color.fromARGB(0xFFFF8899))

    val iconScale by configFloat("icon_scale", 1f, 0.2f, 2f)

    val innerRadius by configFloat("inner_radius", 60f, 40f, 100f)

    val outerRadius by configFloat("outer_radius", 120f, 100f, 200f)

    val optionRadius by configFloat("option_radius", 90f, 50f, 190f)

    val gapDistance by configFloat("gap_distance", 2f, 0.5f, 5f)

    val singlePageMaxCount by configInt("single_page_max_count", 8, 4, 12)

}

