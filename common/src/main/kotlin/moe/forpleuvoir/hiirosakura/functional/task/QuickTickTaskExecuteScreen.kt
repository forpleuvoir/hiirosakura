package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.keyPress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.mousePress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderParent
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidgetContainer
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.screen.BoxScreen
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.KeyBindSetting
import moe.forpleuvoir.ibukigourd.input.KeyTriggerMode
import moe.forpleuvoir.ibukigourd.input.Mouse.*
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.enableReturnHotkey
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.returnHotkeyKeycode
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.util.NextAction
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.config.item.impl.int
import net.minecraft.world.item.Items

object QuickTickTaskExecuteScreen : ModConfigContainer("quick_tick_task_execute") {

    val keyBind by keyBind(
        "key_bind", KeyBind(
            defaultSetting = KeyBindSetting {
                nextAction = NextAction.Continue
                exactMatch = false
                triggerMode = KeyTriggerMode.OnPress
            }
        ) {
            screen().open()
        })

    val innerColor by color("inner_color", Color.ofARGB(0x33000000))

    val outerColor by color("outer_color", Color.ofARGB(0x7F000000))

    val innerSelectedColor by color("inner_selected_color", Color.ofARGB(0x33FF8899))

    val outerSelectedColor by color("outer_selected_color", Color.ofARGB(0xFFFF8899))

    val iconScale by float("icon_scale", 1f, 0.2f, 2f)

    val innerRadius by float("inner_radius", 60f, 40f, 100f)

    val outerRadius by float("outer_radius", 120f, 100f, 200f)

    val optionRadius by float("option_radius", 90f, 50f, 190f)

    val gapDistance by float("gap_distance", 2f, 0.5f, 5f)

    val singlePageMaxCount by int("single_page_max_count", 8, 4, 12)

    fun screen(modifier: Modifier = Modifier) = BoxScreen(
        modifier.attachLeft {
            mousePress {
                onMousePress(it)
                it.tryUse(it.button == RIGHT || (enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode))).onSuccess {
                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
                }
            }.keyPress {
                onKeyPress(it)
                it.tryUse(enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode)).onSuccess {
                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
                }
            }
        }
    ) {
        val scale = iconScale
        val (width, height) = 16f * scale to 16f * scale
        RadialMenu(
            TaskManager.taskList,
            idleInnerColor = stateOf(innerColor),
            idleOuterColor = stateOf(outerColor),
            selectedInnerColor = stateOf(innerSelectedColor),
            selectedOuterColor = stateOf(outerSelectedColor),
            innerRadius = innerRadius,
            outerRadius = outerRadius,
            optionRadius = optionRadius,
            gap = gapDistance,
            optionCount = singlePageMaxCount,
            onMousePress = { mouse, task ->
                when (mouse) {
                    LEFT   -> task?.let {
                        closeScreen()
                        task.execute()
                    } ?: run {
                        TaskEditor(KeyBindTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
                            TaskManager.add(task as KeyBindTickTask)
                            parent()?.let { p ->
                                if (p is GuiWidgetContainer) p.executeRecompose()
                            }
                        }.open()
                    }

                    RIGHT  -> task?.let {
                        TaskEditor(task, screenModifier = Modifier.renderParent(false)) { tickTask ->
                            task.fromTask(tickTask)
                        }.open()
                    }

                    MIDDLE -> task?.let { task ->
                        ConfirmDialog(
                            stateOf(IGLang.remove),
                            onConfirm = {
                                TaskManager.remove(task)
                                parent()?.let { p ->
                                    if (p is GuiWidgetContainer) p.executeRecompose()
                                }
                                closeScreen()
                            }
                        ) {
                            Text(InlineStyleText(task.name), Modifier.minWidth(120f))
                        }.open()
                    }

                    else   -> Unit
                }
            },
            selectedRenderer = { task, guiGraphics, position, _, _, _ ->
                guiGraphics.pushAlignmentText(
                    task?.nameAsInlineStyleText ?: HSLang.taskUnSelected,
                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
                    color = Colors.WHITE
                )
            },
        ) { task, guiGraphics, selected, position, _, _, _ ->
            guiGraphics {
                if (task.icon != Items.AIR) {
                    val s = selected.either(1.2f, 1f)
                    pushItem(task.iconStack, (position.x() - (width * s) / 2f), position.y() - (height * s) / 2f, scale * s)
                } else {
                    val size = task.nameAsInlineStyleText.size
                    pushAlignmentText(
                        task.nameAsInlineStyleText,
                        Box(position.x() - size.halfWidth, position.y() - size.halfHeight, size),
                        color = Colors.WHITE
                    )
                }
            }
        }
    }
}

