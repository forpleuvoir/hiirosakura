package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.KeyBindTickTask.Companion.withKeyBind
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.gapDistance
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.iconScale
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.innerRadius
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.optionRadius
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.outerRadius
import moe.forpleuvoir.hiirosakura.functional.task.QTTEConfig.singlePageMaxCount
import moe.forpleuvoir.hiirosakura.gui.widget.RouletteSelector
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.config.item.impl.keyBind
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.renderAlignmentText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.mousePress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderParent
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.execute
import moe.forpleuvoir.ibukigourd.gui.screen.BoxScreen
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.item.impl.float
import moe.forpleuvoir.nebula.config.item.impl.int


object QTTEConfig : ModConfigContainer("quick_tick_task_execute") {

    val keyBind by keyBind("key_bind", KeyBind {
        QuickTickTaskExecuteScreen().open()
    })

    val iconScale by float("icon_scale", 1f, 0.2f, 2f)

    val innerRadius by float("inner_radius", 60f, 40f, 100f)

    val outerRadius by float("outer_radius", 120f, 100f, 200f)

    val optionRadius by float("option_radius", 85f, 50f, 190f)

    val gapDistance by float("gap_distance", 2f, 0.5f, 5f)

    val singlePageMaxCount by int("single_page_max_count", 8, 4, 12)

}

fun QuickTickTaskExecuteScreen(modifier: Modifier = Modifier) = BoxScreen(
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
                }.open()
            }
        },
        onRightPressSelected = {
            it?.let { task ->
                TaskEditor(task, screenModifier = Modifier.renderParent(false)) {
                    task.fromTask(it)
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