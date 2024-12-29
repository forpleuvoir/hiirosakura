package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.gui.extensions.Quadrilateral
import moe.forpleuvoir.hiirosakura.gui.extensions.pushQuad
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.IGDrawContext
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.input.Mouse
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import org.joml.Vector2f
import org.joml.Vector2fc
import kotlin.math.cos
import kotlin.math.sin

fun <T> WidgetContainerScope.RouletteSelector(
    options: List<T>,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 85f,
    gapDistance: Float = 2f,
    maxOptions: Int = 8,// 单页最多选项数量
    selectedColor: State<ARGBColor> = stateOf(Colors.YELLOW),
    unselectedColor: State<ARGBColor> = stateOf(Colors.BLACK.alpha(0.5f)),
    modifier: Modifier = Modifier,
    onSelected: (option: T?) -> Unit = {},
    selectedRenderer: (option: T?, context: IGDrawContext, position: Vector2fc, mouseX: Float, mouseY: Float, delta: Float) -> Unit,
    optionRenderer: (option: T, context: IGDrawContext, selected: Boolean, position: Vector2fc, mouseX: Float, mouseY: Float, delta: Float) -> Unit,
) {

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

    val currentOptions = options.size.coerceIn(3, maxOptions)

    val quads = mutableListOf<Quadrilateral>()

    var center: Vector2fc = Vector2f()

    var selectedIndex = 0

    val maxPage = if (options.size % currentOptions > 0) {
        (options.size / currentOptions).toInt()
    } else {
        (options.size / currentOptions).toInt() - 1
    }

    var currentPageIndex = 0

    val angleStep = (2 * Math.PI).toFloat() / currentOptions

    Widget(modifier.attachLeft {
        size(outerRadius * 2, outerRadius * 2)
            .placeCompletion {
                quads.clear()
                center = this.transform.worldCenter
                quads.addAll(calculateQuadrilaterals(center.x(), center.y(), innerRadius, outerRadius, maxOptions, gapDistance))
            }
            .mouseScrolling { event ->
                event.tryUse(transform.isMouseOvered(event.position)).onSuccess {
                    currentPageIndex = (currentPageIndex - event.verticalAmount.toInt().coerceIn(-1..1)).coerceIn(0, maxPage)
                }
                onMouseScrolling(event)
            }
            .mousePress { event ->
                event.tryUse(transform.isMouseOvered(event.position) && event.button == Mouse.LEFT)
                    .onSuccess { onSelected(getPage(options, currentOptions, currentPageIndex).getOrNull(selectedIndex)) }
                onMousePress(event)
            }
            .renderBackground { context, x, y, delta ->
                context.batchRenderBox {
                    quads.forEachIndexed { index, quad ->
                        if (Vector2f(x, y).distance(center) in innerRadius..outerRadius) {
                            if (Vector2f(x, y) in quad) {
                                selectedIndex = index
                            }
                        } else {
                            selectedIndex = -1
                        }
                        if (index == selectedIndex)
                            pushQuad(quad, selectedColor.getValue())
                        else
                            pushQuad(quad, unselectedColor.getValue())
                    }
                }
            }
            .render { context, x, y, delta ->
                selectedRenderer(getPage(options, currentOptions, currentPageIndex).getOrNull(selectedIndex), context, center, x, y, delta)
                var startAngle = angleStep -(Math.PI / 2).toFloat() - if (currentOptions % 2 == 0) 0f else angleStep * 0.25f
                getPage(options, currentOptions, currentPageIndex)
                    .forEachIndexed { index, entry ->
                        optionRenderer(
                            entry,
                            context,
                            index == selectedIndex,
                            calculatePointPosition(center.x(), center.y(), startAngle, optionRadius),
                            x,
                            y,
                            delta
                        )
                        startAngle += angleStep
                    }

                val box = Box(center.x() - 20f, center.y() + 15f, Size(40f, 2f))
                val width = (40f - maxPage + 1) / (maxPage + 1)
                val xs = Arrangement.spacedBy(1f).arrange(box.width, buildList {
                    repeat(maxPage + 1) {
                        add(width)
                    }
                })
                context.batchRenderBox {
                    xs.forEachIndexed { index, x ->
                        pushBox(box.copy(x = box.left + xs[index], width = width), if (index == currentPageIndex) Colors.WHITE else Colors.WHITE.alpha(.35f))
                    }
                }
            }
    })

}

fun calculateQuadrilaterals(
    centerX: Float, centerY: Float,
    rInner: Float, rOuter: Float,
    options: Int, gapDistance: Float
): List<Quadrilateral> {
    require(options >= 3) { "A minimum of 3 options is required." }

    val angleStep = (2 * Math.PI).toFloat() / options // 每个四边形的有效弧度

    val quadrilaterals = mutableListOf<Quadrilateral>()

    var startAngle = angleStep - (Math.PI / 2).toFloat() - if (options % 2 == 0) angleStep / 2 else angleStep * 0.75f
    var endAngle = startAngle + angleStep

    repeat(options) {
        // 当前四边形起始角度和结束角度
        val p1 = calculateNormalLinePoints(centerX, centerY, endAngle, rInner, gapDistance, false)

        val p2 = calculateNormalLinePoints(centerX, centerY, endAngle, rOuter, gapDistance, false)

        val p3 = calculateNormalLinePoints(centerX, centerY, startAngle, rOuter, gapDistance, true)

        val p4 = calculateNormalLinePoints(centerX, centerY, startAngle, rInner, gapDistance, true)

        // 顺时针排列顶点（p1 -> p2 -> p3 -> p4）
        quadrilaterals.add(Quadrilateral(p1, p2, p3, p4))
        startAngle += angleStep
        endAngle += angleStep

    }
    return quadrilaterals
}


fun calculatePointPosition(centerX: Float, centerY: Float, angle: Float, radius: Float): Vector2f {
    val x = centerX + radius * cos(angle) // 计算 x 坐标
    val y = centerY + radius * sin(angle) // 计算 y 坐标
    return Vector2f(x, y) // 返回计算出的点
}

fun calculateNormalLinePoints(
    centerX: Float, centerY: Float, // 圆心坐标
    angle: Float,                  // 线段与圆心的角度（弧度制）
    r: Float,                      // 距离圆心 R 的点
    length: Float,                  // 垂直线段的长度
    up: Boolean
): Vector2fc {
    // 计算距离圆心 R 的点（R 点）
    val rPointX = centerX + r * cos(angle)
    val rPointY = centerY + r * sin(angle)

    // 计算垂直线段的两个顶点
    val halfLength = length / 2

    // 垂直方向的单位向量 (-sin(θ), cos(θ))
    val perpX = -sin(angle)
    val perpY = cos(angle)

    // 顶点 1 和顶点 2
    return if (up) {
        Vector2f(
            rPointX + halfLength * perpX,
            rPointY + halfLength * perpY
        )
    } else {
        Vector2f(
            rPointX - halfLength * perpX,
            rPointY - halfLength * perpY
        )
    }
}