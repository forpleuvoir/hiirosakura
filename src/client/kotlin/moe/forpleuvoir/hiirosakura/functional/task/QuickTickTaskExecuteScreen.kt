package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.KeyBindTickTask.Companion.withKeyBind
import moe.forpleuvoir.hiirosakura.gui.widget.RouletteSelector
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderAlignmentText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.mousePress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderParent
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.execute
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetContainer
import moe.forpleuvoir.ibukigourd.gui.screen.BoxScreen
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.color
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.config.item.impl.int

object QuickTickTaskExecuteScreen : ModConfigContainer("quick_tick_task_execute") {

    val keyBind by keyBind("key_bind", KeyBind {
        screen().open()
    })

    val rouletteColor by color("roulette_color", Colors.BLACK.alpha(0.5f))

    val rouletteSelectedColor by color("roulette_selected_color", Color("#FFD4FF00"))

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
                it.tryUse(it.button == Mouse.RIGHT).onSuccess {
                    mc.scheduleStartTick(1) { task, client ->
                        client.currentScreen?.close()
                    }
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
                    mc.currentScreen?.close()
                    it.execute()
                } ?: run {
                    TaskEditor(KeyBindTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
                        TaskManager.add(task.withKeyBind())
                        this.execute {
                            (this.parent() as? WidgetContainer)?.recompose()
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
                            this.execute {
                                (this.parent() as? WidgetContainer)?.recompose()
                            }
                            mc.currentScreen?.close()
                        }
                    ) {
                        TextLabel(Literal(task.name), Modifier.minWidth(120f))
                    }.open()
                }
            },
            selectedRenderer = { item, context, position, mouseX, mouseY, delta ->
                context.renderAlignmentText(
                    item?.name?.let { Literal(it) } ?: HSLang.taskUnSelected,
                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
                    color = Colors.WHITE
                )
            },
        ) { task, context, selected, position, mouseX, mouseY, delta ->
            context.useMatrixStack {
                it.scale(scale, scale, 1f)
                it.translate((position.x() - width / 2f) * (1f / scale), (position.y() - height / 2f) * (1f / scale), 0f)
                drawItem(task.iconStack, 0, 0)
            }
        }

    }
}

