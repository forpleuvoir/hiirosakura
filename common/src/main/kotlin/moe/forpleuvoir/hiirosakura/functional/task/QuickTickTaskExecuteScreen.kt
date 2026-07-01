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

//    fun screen(modifier: Modifier = Modifier) = BoxScreen(
//        modifier.attachLeft {
//            mousePress {
//                onMousePress(it)
//                it.tryUse(it.button == RIGHT || (enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode))).onSuccess {
//                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
//                }
//            }.keyPress {
//                onKeyPress(it)
//                it.tryUse(enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode)).onSuccess {
//                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
//                }
//            }
//        }
//    ) {
//        val scale = iconScale
//        val (width, height) = 16f * scale to 16f * scale
//        RadialMenu(
//            TaskManager.taskList,
//            idleInnerColor = stateOf(innerColor),
//            idleOuterColor = stateOf(outerColor),
//            selectedInnerColor = stateOf(innerSelectedColor),
//            selectedOuterColor = stateOf(outerSelectedColor),
//            innerRadius = innerRadius,
//            outerRadius = outerRadius,
//            optionRadius = optionRadius,
//            gap = gapDistance,
//            optionCount = singlePageMaxCount,
//            onMousePress = { mouse, task ->
//                when (mouse) {
//                    LEFT   -> task?.let {
//                        closeScreen()
//                        task.execute()
//                    } ?: run {
//                        TaskEditor(KeyBindTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
//                            TaskManager.add(task as KeyBindTickTask)
//                            parent()?.let { p ->
//                                if (p is GuiWidgetContainer) p.executeRecompose()
//                            }
//                        }.open()
//                    }
//
//                    RIGHT  -> task?.let {
//                        TaskEditor(task, screenModifier = Modifier.renderParent(false)) { tickTask ->
//                            task.fromTask(tickTask)
//                        }.open()
//                    }
//
//                    MIDDLE -> task?.let { task ->
//                        ConfirmDialog(
//                            stateOf(IGLang.remove),
//                            onConfirm = {
//                                TaskManager.remove(task)
//                                parent()?.let { p ->
//                                    if (p is GuiWidgetContainer) p.executeRecompose()
//                                }
//                                closeScreen()
//                            }
//                        ) {
//                            Text(InlineStyleText(task.name), Modifier.minWidth(120f))
//                        }.open()
//                    }
//
//                    else   -> Unit
//                }
//            },
//            selectedRenderer = { task, guiGraphics, position, _, _, _ ->
//                guiGraphics.pushAlignmentText(
//                    task?.nameAsInlineStyleText ?: HSLang.taskUnSelected,
//                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
//                    color = Colors.WHITE
//                )
//            },
//        ) { task, guiGraphics, selected, position, _, _, _ ->
//            guiGraphics {
//                if (task.icon != Items.AIR) {
//                    val s = selected.either(1.2f, 1f)
//                    pushItem(task.iconStack, (position.x() - (width * s) / 2f), position.y() - (height * s) / 2f, scale * s)
//                } else {
//                    val size = task.nameAsInlineStyleText.size
//                    pushAlignmentText(
//                        task.nameAsInlineStyleText,
//                        Box(position.x() - size.halfWidth, position.y() - size.halfHeight, size),
//                        color = Colors.WHITE
//                    )
//                }
//            }
//        }
//    }
}

