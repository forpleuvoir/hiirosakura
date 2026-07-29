package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import kotlin.math.*

data class Sector(
    val start: Float,
    val end: Float,
) {
    companion object {
        fun calculateArchAngle(start: Float, end: Float): Float {
            return (end - start).let { if (it < 0) it + 360f else it }
        }
    }

    val centerAngle: Float
        get() = normalizeDegree(start + arch * 0.5f)

    val arch: Float
        get() = calculateArchAngle(start, end)

    operator fun contains(angle: Float): Boolean {
        val a = normalizeDegree(angle)
        if (start <= end) return a in start..end
        return a >= start || a <= end
    }
}

fun normalizeDegree(angle: Float): Float {
    val a = angle % 360f
    return if (a < 0f) a + 360f else a
}

fun calculateSectors(startAngleDegree: Float, count: Int): List<Sector> {
    require(count > 0) { "count must be > 0" }
    val step = 360f / count
    val firstStart = startAngleDegree - step / 2f
    return List(count) { i ->
        Sector(
            start = normalizeDegree(firstStart + i * step),
            end = normalizeDegree(firstStart + (i + 1) * step),
        )
    }
}

fun pointOnCircle(center: Offset, radius: Float, angleDegree: Float): Offset {
    val rad = Math.toRadians(angleDegree.toDouble())
    return Offset(
        center.x + radius * cos(rad).toFloat(),
        center.y + radius * sin(rad).toFloat()
    )
}

// ---------------- 扇区轮廓路径（平行间隙 + 圆角） ----------------

/** 边曲线的采样段数（边是"等弧长偏移"曲线，用折线近似） */
private const val EDGE_SEGMENTS = 8

/** 向量按角度约定旋转 +90°（y 向下时即视觉上顺时针） */
private fun perp90(v: Offset): Offset = Offset(-v.y, v.x)

private fun Offset.length(): Float = sqrt(x * x + y * y)

private fun Offset.normalized(): Offset = length().let { if (it > 0f) this / it else this }

private fun Offset.dot(other: Offset): Float = x * other.x + y * other.y

/** 向量的方向角（度），与 [pointOnCircle] 的角度约定一致 */
private fun angleOf(v: Offset): Float = Math.toDegrees(atan2(v.y.toDouble(), v.x.toDouble())).toFloat()

/**
 * 扇区一个角的圆角信息。
 * 圆角同时与圆环内/外圈（圆心为菜单中心）和扇区边曲线相切（边曲线以切点处的切线近似）。
 */
private class CornerFillet(
    /** 圆角圆心（相对菜单中心） */
    val center: Offset,
    /** 圆角与圆环圈的切点方向角（度，相对菜单中心），与圆角圆心的方向角一致 */
    val circleTangentAngle: Float,
    /** 圆角圆心指向边切点的方向角（度） */
    val footAngle: Float,
    /** 边切点到菜单中心的距离 */
    val footRadius: Float,
)

/**
 * 计算扇区圆角。
 *
 * @param circleRadius 圆环圈半径（内圈或外圈）
 * @param outerCircle true 与圆内切（外圈角），false 与圆外切（内圈角）
 * @param edgePoint 边曲线在该半径处的点（相对菜单中心）
 * @param edgeTangent 边曲线在该点的单位切向（指向半径增大方向）
 * @param interiorNormal 边的单位法向，指向扇区内部
 */
private fun computeCornerFillet(
    cornerRadius: Float,
    circleRadius: Float,
    outerCircle: Boolean,
    edgePoint: Offset,
    edgeTangent: Offset,
    interiorNormal: Offset,
): CornerFillet {
    val sigma = if (outerCircle) -1f else 1f
    val pt = edgePoint.dot(edgeTangent)
    val pn = edgePoint.dot(interiorNormal)
    val discriminant = (pt * pt - 2f * cornerRadius * (pn - sigma * circleRadius)).coerceAtLeast(0f)
    val a = -pt + sqrt(discriminant)
    val c = edgePoint + edgeTangent * a + interiorNormal * cornerRadius
    return CornerFillet(
        center = c,
        circleTangentAngle = angleOf(c),
        footAngle = angleOf(-interiorNormal),
        footRadius = (edgePoint + edgeTangent * a).length(),
    )
}

/**
 * 构建扇区（圆环段）的轮廓路径。
 *
 * 间隙（[gap]）在任意半径处的线性宽度恒定（间隙两侧保持平行），
 * 因此扇区的两条边是"等弧长偏移"曲线（角度随半径变化）而非径向直线。
 * 四个角以 [cornerRadius] 圆角化，半径会被钳制：不超过环厚的一半，
 * 且保证扣除圆角后内外圆弧角宽非负；为 0 时退化为直角。
 */
fun getSectorPath(
    center: Offset,
    sector: Sector,
    innerRadius: Float,
    outerRadius: Float,
    gap: Float,
    cornerRadius: Float,
): Path {
    val path = Path()
    val start = sector.start
    val end = start + sector.arch

    // 半径 r 处的单侧间隙角（度），使间隙线性宽度恒为 gap
    fun gapHalfAngle(radius: Float): Float =
        if (gap > 0f && radius > 0f) Math.toDegrees((gap / (2.0 * radius))).toFloat() else 0f

    // 边曲线上的点（相对菜单中心）：startEdge 为起始边，否则为结束边
    fun edgePoint(startEdge: Boolean, radius: Float): Offset {
        val angle = if (startEdge) start + gapHalfAngle(radius) else end - gapHalfAngle(radius)
        return pointOnCircle(Offset.Zero, radius, angle)
    }

    // 边曲线的单位切向（指向半径增大方向）
    fun edgeTangent(startEdge: Boolean, radius: Float): Offset {
        val angle = if (startEdge) start + gapHalfAngle(radius) else end - gapHalfAngle(radius)
        val k = if (radius > 0f) gap / (2f * radius) else 0f
        val radial = pointOnCircle(Offset.Zero, 1f, angle)
        val v = if (startEdge) radial - perp90(radial) * k else radial + perp90(radial) * k
        return v.normalized()
    }

    // 采样边曲线（从 fromRadius 到 toRadius），调用时当前点应已在边的起点处
    fun Path.addEdge(startEdge: Boolean, fromRadius: Float, toRadius: Float) {
        for (i in 1..EDGE_SEGMENTS) {
            val r = fromRadius + (toRadius - fromRadius) * i / EDGE_SEGMENTS
            val p = edgePoint(startEdge, r) + center
            lineTo(p.x, p.y)
        }
    }

    val outerHalf = gapHalfAngle(outerRadius)
    val innerHalf = gapHalfAngle(innerRadius)
    val outerArch = (sector.arch - 2f * outerHalf).coerceAtLeast(0f)
    val innerArch = (sector.arch - 2f * innerHalf).coerceAtLeast(0f)
    if (outerArch <= 0f || innerArch <= 0f) return path

    val outerRect = Rect(center, outerRadius)
    val innerRect = Rect(center, innerRadius)

    // 圆角半径钳制：不超过环厚的一半，且保证扣除圆角后内外圆弧角宽非负
    fun maxCornerForSpan(radius: Float, span: Float, outer: Boolean): Float {
        if (span >= 180f) return Float.MAX_VALUE
        val s = sin(Math.toRadians((span / 2f).toDouble())).toFloat()
        return if (outer) radius * s / (1f + s) else radius * s / (1f - s)
    }

    val radius = cornerRadius
        .coerceIn(0f, (outerRadius - innerRadius) / 2f * 0.999f)
        .coerceAtMost(maxCornerForSpan(outerRadius, outerArch, outer = true) * 0.999f)
        .coerceAtMost(maxCornerForSpan(innerRadius, innerArch, outer = false) * 0.999f)

    if (radius <= 0f) {
        path.arcTo(outerRect, start + outerHalf, outerArch, forceMoveTo = true)
        path.addEdge(startEdge = false, fromRadius = outerRadius, toRadius = innerRadius)
        path.arcTo(innerRect, start + innerHalf + innerArch, -innerArch, forceMoveTo = false)
        path.addEdge(startEdge = true, fromRadius = innerRadius, toRadius = outerRadius)
        path.close()
        return path
    }

    val startTangentOuter = edgeTangent(true, outerRadius)
    val endTangentOuter = edgeTangent(false, outerRadius)
    val endTangentInner = edgeTangent(false, innerRadius)
    val startTangentInner = edgeTangent(true, innerRadius)

    // 四个角的圆角
    val outerStart = computeCornerFillet(radius, outerRadius, true, edgePoint(true, outerRadius), startTangentOuter, perp90(startTangentOuter))
    val outerEnd = computeCornerFillet(radius, outerRadius, true, edgePoint(false, outerRadius), endTangentOuter, -perp90(endTangentOuter))
    val innerEnd = computeCornerFillet(radius, innerRadius, false, edgePoint(false, innerRadius), endTangentInner, -perp90(endTangentInner))
    val innerStart = computeCornerFillet(radius, innerRadius, false, edgePoint(true, innerRadius), startTangentInner, perp90(startTangentInner))

    // 外弧：起始外角切点 -> 结束外角切点
    path.arcTo(
        outerRect,
        outerStart.circleTangentAngle,
        normalizeDegree(outerEnd.circleTangentAngle - outerStart.circleTangentAngle),
        forceMoveTo = true,
    )
    // 结束外圆角：圆切点 -> 边切点
    path.arcTo(
        Rect(outerEnd.center + center, radius),
        outerEnd.circleTangentAngle,
        normalizeDegree(outerEnd.footAngle - outerEnd.circleTangentAngle),
        forceMoveTo = false,
    )
    // 结束边（外 -> 内）
    path.addEdge(startEdge = false, fromRadius = outerEnd.footRadius, toRadius = innerEnd.footRadius)
    // 结束内圆角：边切点 -> 圆切点
    path.arcTo(
        Rect(innerEnd.center + center, radius),
        innerEnd.footAngle,
        normalizeDegree(innerEnd.circleTangentAngle + 180f - innerEnd.footAngle),
        forceMoveTo = false,
    )
    // 内弧（反向）
    path.arcTo(
        innerRect,
        innerEnd.circleTangentAngle,
        -normalizeDegree(innerEnd.circleTangentAngle - innerStart.circleTangentAngle),
        forceMoveTo = false,
    )
    // 起始内圆角：圆切点 -> 边切点
    path.arcTo(
        Rect(innerStart.center + center, radius),
        innerStart.circleTangentAngle + 180f,
        normalizeDegree(innerStart.footAngle - (innerStart.circleTangentAngle + 180f)),
        forceMoveTo = false,
    )
    // 起始边（内 -> 外）
    path.addEdge(startEdge = true, fromRadius = innerStart.footRadius, toRadius = outerStart.footRadius)
    // 起始外圆角：边切点 -> 圆切点
    path.arcTo(
        Rect(outerStart.center + center, radius),
        outerStart.footAngle,
        normalizeDegree(outerStart.circleTangentAngle - outerStart.footAngle),
        forceMoveTo = false,
    )
    path.close()
    return path
}

fun findSectorIndex(
    sectors: List<Sector>,
    center: Offset,
    pointer: Offset,
    innerRadiusPx: Float,
    outerRadiusPx: Float,
): Int {
    val dx = pointer.x - center.x
    val dy = pointer.y - center.y
    val dist = sqrt(dx * dx + dy * dy)
    if (dist !in innerRadiusPx..outerRadiusPx) return -1
    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    return sectors.indexOfFirst { it.contains(angle) }
}
