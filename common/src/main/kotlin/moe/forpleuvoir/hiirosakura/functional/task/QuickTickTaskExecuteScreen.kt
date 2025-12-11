package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.KeyBindTickTask.Companion.withKeyBind
import moe.forpleuvoir.hiirosakura.gui.widget.RouletteSelector
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.extensions.guigraphics.useMatrixStack
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
import moe.forpleuvoir.ibukigourd.input.*
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.enableReturnHotkey
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.returnHotkeyKeycode
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.util.NextAction
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.config.item.impl.int

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

    val rouletteColor by color("roulette_color", Colors.BLACK.alpha(0.5f))

    val rouletteSelectedColor by color("roulette_selected_color", Color.ofRGB(0xD4FF00))

    val iconScale by float("icon_scale", 1f, 0.2f, 2f)

    val innerRadius by float("inner_radius", 60f, 40f, 100f)

    val outerRadius by float("outer_radius", 120f, 100f, 200f)

    val optionRadius by float("option_radius", 85f, 50f, 190f)

    val gapDistance by float("gap_distance", 2f, 0.5f, 5f)

    val singlePageMaxCount by int("single_page_max_count", 8, 4, 12)

    fun screen(modifier: Modifier = Modifier) = BoxScreen(
        modifier.attachLeft {
            mousePress {
                onMousePress(it)
                it.tryUse(it.button == Mouse.RIGHT || (enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode))).onSuccess {
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
        RouletteSelector(
            TaskManager.taskList,
            unselectedColor = stateOf(rouletteColor),
            selectedColor = stateOf(rouletteSelectedColor),
            innerRadius = innerRadius,
            outerRadius = outerRadius,
            optionRadius = optionRadius,
            gapDistance = gapDistance,
            maxOptions = singlePageMaxCount,
            onLeftPressSelected = {
                it?.let {
                    closeScreen()
                    it.execute()
                } ?: run {
                    TaskEditor(KeyBindTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
                        TaskManager.add(task.withKeyBind())
                        parent()?.let { p ->
                            if (p is GuiWidgetContainer) p.executeRecompose()
                        }
                    }.open()
                }
            },
            onRightPressSelected = {
                it?.let { task ->
                    TaskEditor(task, screenModifier = Modifier.renderParent(false)) { tickTask ->
                        task.fromTask(tickTask)
                    }.open()
                }
            },
            onMiddlePressSelected = {
                it?.let { task ->
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
            },
            selectedRenderer = { item, guiGraphics, position, _, _, _ ->
                guiGraphics.pushAlignmentText(
                    item?.name?.let { InlineStyleText(it) } ?: HSLang.taskUnSelected,
                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
                    color = Colors.WHITE
                )
            },
        ) { task, guiGraphics, _, position, _, _, _ ->
            guiGraphics.useMatrixStack {
                pushItem(task.iconStack, (position.x() - width / 2f), position.y() - height / 2f, scale)
            }
        }

    }
}

