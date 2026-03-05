package moe.forpleuvoir.hiirosakura.gui.widget.radialmenu

import moe.forpleuvoir.hiirosakura.gui.extensions.Quadrilateral
import moe.forpleuvoir.hiirosakura.gui.extensions.QuadrilateralRenderState
import moe.forpleuvoir.ibukigourd.gui.base.render.IGGuiGraphics
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Matrix3x2f
import org.joml.Vector2f
import org.joml.Vector2fc
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun IGGuiGraphics.pushRadialSectorQuads(
    center: Vector2fc,
    sector: Sector,
    innerRadius: Float,
    outerRadius: Float,
    gap: Float,
    innerColor: ARGBColor,
    outerColor: ARGBColor,
    pose: Matrix3x2f = Matrix3x2f(pose()),
) {
    val (innerVertices, outerVertices) = if (gap > 0f) {
        getSectorVertices(innerRadius, outerRadius, sector, gap)

    } else {
        getSectorVertices(innerRadius, sector.start, sector.arch) to
                getSectorVertices(outerRadius, sector.start, sector.arch)
    }
    // 为每个四边形创建单独的QuadrilateralRenderState
    for (i in 0 until outerVertices.size - 1) {
        val outerPoint1 = Vector2f(outerVertices[i].x() + center.x(), outerVertices[i].y() + center.y())
        val outerPoint2 = Vector2f(outerVertices[i + 1].x() + center.x(), outerVertices[i + 1].y() + center.y())
        val innerPoint1 = Vector2f(innerVertices[i].x() + center.x(), innerVertices[i].y() + center.y())
        val innerPoint2 = Vector2f(innerVertices[i + 1].x() + center.x(), innerVertices[i + 1].y() + center.y())

        // 根据QuadrilateralRenderState的顶点顺序：p1(TOP LEFT), p2(BOTTOM LEFT), p3(BOTTOM RIGHT), p4(TOP RIGHT)
        // 对应颜色：col1(TOP LEFT), col2(BOTTOM LEFT), col3(BOTTOM RIGHT), col4(TOP RIGHT)
        this.getGuiRenderState().submitGuiElement(
            QuadrilateralRenderState(
                outerPoint1,  // TOP LEFT
                innerPoint1,  // BOTTOM LEFT
                innerPoint2,  // BOTTOM RIGHT
                outerPoint2,  // TOP RIGHT
                outerColor,   // TOP LEFT color
                innerColor,   // BOTTOM LEFT color
                innerColor,   // BOTTOM RIGHT color
                outerColor,   // TOP RIGHT color
                pose,
                RenderPipelines.GUI,
                peekScissorBox()?.asScreenRectangle
            )
        )
    }
}


fun IGGuiGraphics.pushRadialSectorQuads(
    iterable: Iterable<Quadrilateral>,
    innerColor: ARGBColor,
    outerColor: ARGBColor,
    pose: Matrix3x2f = Matrix3x2f(pose()),
) {
    iterable.forEach { quad ->
        this.getGuiRenderState().submitGuiElement(
            QuadrilateralRenderState(
                quad.p1,  // TOP LEFT
                quad.p2,  // BOTTOM LEFT
                quad.p3,  // BOTTOM RIGHT
                quad.p4,  // TOP RIGHT
                outerColor,   // TOP LEFT color
                innerColor,   // BOTTOM LEFT color
                innerColor,   // BOTTOM RIGHT color
                outerColor,   // TOP RIGHT color
                pose,
                RenderPipelines.GUI,
                peekScissorBox()?.asScreenRectangle
            )
        )
    }
}

// 获取组成一个扇区的四边形
fun getRadialSectorQuads(
    center: Vector2fc,
    sector: Sector,
    innerRadius: Float,
    outerRadius: Float,
    gap: Float
): List<Quadrilateral> {
    val (innerVertices, outerVertices) = if (gap > 0f) {
        getSectorVertices(innerRadius, outerRadius, sector, gap)

    } else {
        getSectorVertices(innerRadius, sector.start, sector.arch) to
                getSectorVertices(outerRadius, sector.start, sector.arch)
    }
    return buildList {
        // 为每个四边形创建单独的QuadrilateralRenderState
        for (i in 0 until outerVertices.lastIndex) {
            val outerPoint1 = Vector2f(outerVertices[i].x() + center.x(), outerVertices[i].y() + center.y())
            val outerPoint2 = Vector2f(outerVertices[i + 1].x() + center.x(), outerVertices[i + 1].y() + center.y())
            val innerPoint1 = Vector2f(innerVertices[i].x() + center.x(), innerVertices[i].y() + center.y())
            val innerPoint2 = Vector2f(innerVertices[i + 1].x() + center.x(), innerVertices[i + 1].y() + center.y())

            add(
                Quadrilateral(
                    outerPoint1,
                    innerPoint1,
                    innerPoint2,
                    outerPoint2,
                )
            )
        }
    }
}


data class ArcCacheKey(
    val radius: Float,
    val startAngle: Float,
    val arcAngle: Float
)

/**
 * 栈结构的缓存，有容量上限
 */
private class VertexCache(private val maxCapacity: Int) {
    private val cache = LinkedHashMap<ArcCacheKey, List<Vector2fc>>(maxCapacity, 0.75f, true)

    fun getOrPut(key: ArcCacheKey, valueProvider: () -> List<Vector2fc>): List<Vector2fc> {
        // 如果缓存已满，移除最久未使用的项
        if (cache.size >= maxCapacity && !cache.containsKey(key)) {
            val oldestKey = cache.keys.first()
            cache.remove(oldestKey)
        }
        return cache.getOrPut(key, valueProvider)
    }
}

private val VERTEX_CACHE = VertexCache(100)

private val Float.toDegrees get() = (this * 57.29577951308232).let { if (it < 0) it + 360 else it }
private val Float.toRadians get() = this * 0.017453292519943295

private fun getSectorVertices(innerRadius: Float, outerRadius: Float, sector: Sector, gap: Float): Pair<List<Vector2fc>, List<Vector2fc>> {
    val startRadians = sector.start.toRadians.toFloat()
    val endRadians = sector.end.toRadians.toFloat()
    //首先计算出四个关键点
    val startInnerPoint = calculateNormalLinePoints(0f, 0f, startRadians, innerRadius, gap, true)
    val startOuterPoint = calculateNormalLinePoints(0f, 0f, startRadians, outerRadius, gap, true)
    val endInnerPoint = calculateNormalLinePoints(0f, 0f, endRadians, innerRadius, gap, false)
    val endOuterPoint = calculateNormalLinePoints(0f, 0f, endRadians, outerRadius, gap, false)

    val innerStartAngle = atan2(startInnerPoint.y(), startInnerPoint.x()).toDegrees.toFloat()
    val innerEndAngle = atan2(endInnerPoint.y(), endInnerPoint.x()).toDegrees.toFloat()
    val outerStartAngle = atan2(startOuterPoint.y(), startOuterPoint.x()).toDegrees.toFloat()
    val outerEndAngle = atan2(endOuterPoint.y(), endOuterPoint.x()).toDegrees.toFloat()

    val innerVertices = getSectorVertices(innerRadius, innerStartAngle, Sector.calculateArchAngle(innerStartAngle, innerEndAngle))
    val outerVectors = getSectorVertices(outerRadius, outerStartAngle, Sector.calculateArchAngle(outerStartAngle, outerEndAngle))
    return innerVertices to outerVectors
}


private fun getSectorVertices(radius: Float, startAngle: Float, arcAngle: Float): List<Vector2fc> {
    val key = ArcCacheKey(radius, startAngle, arcAngle)
    return VERTEX_CACHE.getOrPut(key) {
        val segmentCount = if (abs(arcAngle) <= 90) 16 else 32
        buildList {
            val startRad = Math.toRadians(startAngle.toDouble()).toFloat()
            val sweepRad = Math.toRadians(arcAngle.toDouble()).toFloat()
            val stepRad = sweepRad / (segmentCount - 1)
            for (i in 0 until segmentCount) {
                val currentRad = startRad + i * stepRad
                add(Vector2f((radius * cos(currentRad.toDouble())).toFloat(), (radius * sin(currentRad.toDouble())).toFloat()))
            }
        }
    }
}