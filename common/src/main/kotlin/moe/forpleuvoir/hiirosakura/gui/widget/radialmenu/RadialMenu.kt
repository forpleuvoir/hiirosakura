package moe.forpleuvoir.hiirosakura.gui.widget.radialmenu

import moe.forpleuvoir.hiirosakura.gui.extensions.Quadrilateral
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidgetImpl
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import org.joml.Vector2f
import org.joml.Vector2fc
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun <T> ContainerScope.RadialMenu(
    options: List<T>,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 85f,
    startAngleDegree: Float = -90f,
    gap: Float = 2f,
    optionCount: Int = 8,// 单页最多选项数量
    selectedOuterColor: State<ARGBColor> = stateOf(Colors.YELLOW),
    selectedInnerColor: State<ARGBColor> = stateOf(Colors.YELLOW.alpha(.25f)),
    idleOuterColor: State<ARGBColor> = stateOf(Colors.BLACK.alpha(0.5f)),
    idleInnerColor: State<ARGBColor> = stateOf(Colors.BLACK.alpha(0.15f)),
    modifier: Modifier = Modifier,
    onMousePress: GuiWidget.(mouse: Mouse, option: T?) -> Unit = { _, _ -> },
    selectedRenderer: (option: T?, context: IGGuiGraphics, position: Vector2fc, mouseX: Float, mouseY: Float, delta: Float) -> Unit,
    optionRenderer: (option: T, context: IGGuiGraphics, selected: Boolean, position: Vector2fc, mouseX: Float, mouseY: Float, delta: Float) -> Unit,
): GuiWidgetImpl {
    fun getPage(
        all: List<T>,
        pageSize: Int,
        currentPageIndex: Int,
    ): List<T> {
        val startIndex = (currentPageIndex) * pageSize
        val endIndex = minOf(startIndex + pageSize, all.size)
        if (startIndex >= endIndex) return emptyList()
        return all.subList(startIndex, endIndex)
    }

    val currentOptions = options.size.coerceIn(3, optionCount)

    val sectors = calculateAnnularSectors(startAngleDegree, optionCount)

    var quads: List<List<Quadrilateral>> = emptyList()

    var center: Vector2fc = Vector2f()

    var selectedIndex = 0

    val maxPage = if (options.size % currentOptions > 0) {
        (options.size / currentOptions)
    } else {
        (options.size / currentOptions) - 1
    }

    var currentPageIndex = 0


    return Widget(modifier.attachLeft {
        size(outerRadius * 2, outerRadius * 2)
            .placeCompletion {
                center = this.transform.worldCenter
                quads = sectors.map { sector ->
                    getRadialSectorQuads(
                        center,
                        sector,
                        innerRadius,
                        outerRadius,
                        gap
                    )
                }
            }
            .mouseScrolling { event ->
                event.tryUse(transform.isMouseOvered(event.position)).onSuccess {
                    currentPageIndex = (currentPageIndex - event.verticalAmount.toInt().coerceIn(-1..1)).coerceIn(0, maxPage)
                }
                onMouseScrolling(event)
            }
            .mousePress { event ->
                event.tryUse(Vector2f(event.x, event.y).distance(center) in innerRadius..outerRadius)
                    .onSuccess {
                        onMousePress(event.button, getPage(options, currentOptions, currentPageIndex).getOrNull(selectedIndex))
                    }
                onMousePress(event)
            }
            .renderBackground { guiGraphics, x, y, delta ->
                guiGraphics {
                    selectedIndex = (Vector2f(x, y).distance(center) in innerRadius..outerRadius) //是否在环形区域内
                        .either(
                            { getSelectedSector(sectors, center, x, y) },
                            { -1 }
                        )

                    quads.forEachIndexed { index, quads ->
                        val innerColor = if (index == selectedIndex) selectedInnerColor.getValue() else idleInnerColor.getValue()
                        val outerColor = if (index == selectedIndex) selectedOuterColor.getValue() else idleOuterColor.getValue()
                        pushRadialSectorQuads(quads.asIterable(), innerColor, outerColor)
                    }
                }
            }
            .render { guiGraphics, x, y, delta ->
                val page = getPage(options, currentOptions, currentPageIndex)
                //渲染选中项,并不是轮盘部分而是渲染在中心
                selectedRenderer(page.getOrNull(selectedIndex), guiGraphics, center, x, y, delta)

                val angleStep = (2 * Math.PI).toFloat() / optionCount

                var startAngle = startAngleDegree

                //渲染选项
                page.forEachIndexed { index, entry ->
                    optionRenderer(
                        entry,
                        guiGraphics,
                        index == selectedIndex,
                        calculatePointPosition(center, optionRadius, sectors[index].centerAngle),
                        x,
                        y,
                        delta
                    )
                    startAngle += angleStep
                }
                //渲染当前页面的横条
                if (maxPage > 0) {
                    val box = Box(center.x() - 30f, center.y() + 15f, Size(60f, 2f))
                    val width = (box.width - maxPage + 1) / (maxPage + 1)
                    val xs = Arrangement.spacedBy(1f).arrange(box.width, buildList {
                        repeat(maxPage + 1) {
                            add(width)
                        }
                    })
                    guiGraphics {
                        xs.forEachIndexed { index, x ->
                            pushBox(
                                box.copy(x = box.left + xs[index], width = width),
                                if (index == currentPageIndex) Colors.WHITE else Colors.WHITE.alpha(.35f)
                            )
                        }
                    }
                }
            }
    })

}

data class Sector(val start: Float, val end: Float) {

    companion object {
        fun calculateArchAngle(start: Float, end: Float): Float {
            return (end - start).let { if (it < 0) it + 360f else it }
        }
    }

    val centerAngle: Float
            by lazy(LazyThreadSafetyMode.NONE) { normalizeDegree(start + arch * 0.5f) }

    val arch: Float
            by lazy(LazyThreadSafetyMode.NONE) { calculateArchAngle(start, end) }

    operator fun contains(angle: Float): Boolean {
        // 处理普通情况（不跨越360°边界）
        if (start <= end) {
            return angle in start..end
        }
        // 处理跨越360°边界的情况（例如 350° -> 10°）
        return angle >= start || angle <= end
    }
}

/**
 * 计算轮盘扇形的角度数据（0-360度）
 *
 * @param startAngleDegree 第一个扇区的中心点角度
 * @param optionCount 选项数量
 * @return Array<Sector> 包含每个扇区的起始和结束角度（度）
 */
private fun calculateAnnularSectors(
    startAngleDegree: Float,
    optionCount: Int
): Array<Sector> {
    if (optionCount <= 0) return emptyArray()

    val angleStep = 360f / optionCount

    // 计算第一个扇区的起始边缘角度
    val firstSectorStart = startAngleDegree - (angleStep / 2f)

    return Array(optionCount) { i ->
        val start = firstSectorStart + i * angleStep
        val end = start + angleStep
        // 保持在 0-360 范围内以便调试
        Sector(normalizeDegree(start), normalizeDegree(end))
    }
}

private fun calculatePointPosition(center: Vector2fc, radius: Float, angle: Float): Vector2f {
    val rad = Math.toRadians(angle.toDouble()).toFloat()
    val x = radius * cos(rad)
    val y = radius * sin(rad)
    return Vector2f(center.x() + x, center.y() + y)
}

private fun normalizeDegree(degree: Float): Float = (degree % 360f + 360f) % 360f

/**
 * 获取选中的扇区索引
 * @param sectors 扇形数据
 * @param center 轮盘中心位置（判定角度需要相对于中心点）
 * @param mouseX 鼠标当前X位置
 * @param mouseY 鼠标当前Y位置
 * @return 选中的扇区索引，如果没有选中的扇区，则返回-1
 */
private fun getSelectedSector(sectors: Array<Sector>, center: Vector2fc, mouseX: Float, mouseY: Float): Int {
    val dx = mouseX - center.x()
    val dy = mouseY - center.y()

    val angle = normalizeDegree(Math.toDegrees(atan2(dy, dx).toDouble()).toFloat())

    sectors.forEachIndexed { index, sector ->
        if (angle in sector) {
            return index
        }
    }

    return -1
}

