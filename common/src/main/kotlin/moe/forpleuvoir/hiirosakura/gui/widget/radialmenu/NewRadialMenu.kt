package moe.forpleuvoir.hiirosakura.gui.widget.radialmenu

import moe.forpleuvoir.hiirosakura.gui.widget.radialmenu.RadialSectorRenderState.Companion.pushRadialSector
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
import moe.forpleuvoir.ibukigourd.input.Mouse.*
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import org.joml.Vector2f
import org.joml.Vector2fc

fun <T> ContainerScope.NewRadialMenu(
    options: List<T>,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 85f,
    startAngleDegree: Float = 0f,
    gapAngleDegree: Float = 2f,
    optionCount: Int = 8,// 单页最多选项数量
    selectedColor: State<ARGBColor> = stateOf(Colors.YELLOW),
    idleColor: State<ARGBColor> = stateOf(Colors.BLACK.alpha(0.5f)),
    modifier: Modifier = Modifier,
    onLeftPressSelected: GuiWidget.(option: T?) -> Unit = {},
    onRightPressSelected: GuiWidget.(option: T?) -> Unit = {},
    onMiddlePressSelected: GuiWidget.(option: T?) -> Unit = {},
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

    val sectors = calculateAnnularSectors(startAngleDegree, gapAngleDegree, optionCount)

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
            }
            .mouseScrolling { event ->
                event.tryUse(transform.isMouseOvered(event.position)).onSuccess {
                    currentPageIndex = (currentPageIndex - event.verticalAmount.toInt().coerceIn(-1..1)).coerceIn(0, maxPage)
                }
                onMouseScrolling(event)
            }
            .mousePress { event ->
                event.tryUse(Vector2f(event.x, event.y).distance(center) in innerRadius..outerRadius && event.button in listOf(LEFT, RIGHT, MIDDLE))
                    .onSuccess {
                        val selected = getPage(options, currentOptions, currentPageIndex).getOrNull(selectedIndex)
                        when (event.button) {
                            LEFT   -> onLeftPressSelected(selected)
                            RIGHT  -> onRightPressSelected(selected)
                            MIDDLE -> onMiddlePressSelected(selected)
                            else   -> Unit
                        }
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

                    sectors.forEachIndexed { index, sector ->
                        if (index == 0) {
                            pushRadialSector(
                                center,
                                sector.start,
                                sector.arch,
                                innerRadius,
                                outerRadius,
                                idleColor.getValue(),
                                idleColor.getValue()
                            )
                        }
//                        if (index == selectedIndex) {
//                            pushRadialSector(
//                                center,
//                                sector.start,
//                                sector.arch,
//                                innerRadius,
//                                outerRadius,
//                                Color.ofARGB(selectedColor.getValue().argb).opacity(0.5f),
//                                selectedColor.getValue()
//                            )
//                        } else {
//                            pushRadialSector(
//                                center,
//                                sector.start,
//                                sector.arch,
//                                innerRadius,
//                                outerRadius,
//                                idleColor.getValue(),
//                                idleColor.getValue()
//                            )
//                        }
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
                        calculatePointPosition(center.x(), center.y(), startAngle, optionRadius),
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
    val centerAngle: Float get() = (start + end) / 2f
    val arch: Float get() = (end - start).let { if (it < 0) it + 360f else it }
}

/**
 * 计算轮盘扇形的角度数据（0-360度）
 *
 * @param startAngleDegree 第一个扇区的中心点角度（度，0为正上方）
 * @param gapAngleDegree 间隔角度（度）
 * @param optionCount 选项数量
 * @return Array<Sector> 包含每个扇区的起始和结束角度（度）
 */
private fun calculateAnnularSectors(
    startAngleDegree: Float = 0f,
    gapAngleDegree: Float,
    optionCount: Int
): Array<Sector> {
    if (optionCount <= 0) return emptyArray()

    val angleStep = 360f / optionCount
    val sectorWidth = angleStep - gapAngleDegree

    // 计算第一个扇区的起始边缘角度
    val firstSectorStart = startAngleDegree - (sectorWidth / 2f)

    return Array(optionCount) { i ->
        val start = firstSectorStart + i * angleStep
        val end = start + sectorWidth
        // 保持在 0-360 范围内以便调试
        Sector(normalizeDegree(start), normalizeDegree(end))
    }
}

private fun normalizeDegree(degree: Float): Float = (degree % 360f + 360f) % 360f

/**
 * 获取选中的扇区索引
 * @param sectors 扇形数据
 * @param center 轮盘中心位置（判定角度需要相对于中心点）
 * @param mouse 鼠标当前位置
 * @return 选中的扇区索引，如果没有选中的扇区，则返回-1
 */
private fun getSelectedSector(sectors: Array<Sector>, center: Vector2fc, mouseX: Float, mouseY: Float): Int {
    val dx = mouseX - center.x()
    val dy = mouseY - center.y()

    // atan2 得到的是：右=0, 下=90, 左=180, 上=-90
    // 我们加上 90 度，使其变为：上=0, 右=90, 下=180, 左=270
    var mouseDeg = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
    mouseDeg = normalizeDegree(mouseDeg)

    sectors.forEachIndexed { index, sector ->
        var m = mouseDeg
        // 处理跨越 360° 边界的循环判定（例如扇区在 350~10 度）
        while (m < sector.start) m += 360f
        while (m >= sector.start + 360f) m -= 360f

        if (m >= sector.start && m <= sector.end) return index
    }
    return -1
}

