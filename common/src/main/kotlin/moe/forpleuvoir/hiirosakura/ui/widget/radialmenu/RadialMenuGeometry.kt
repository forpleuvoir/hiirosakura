package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.ui.geometry.Offset
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

data class SectorQuad(
    val p1: Offset,
    val p2: Offset,
    val p3: Offset,
    val p4: Offset,
)

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

fun getSectorVertices(radius: Float, startAngle: Float, arcAngle: Float, segmentCount: Int): List<Offset> {
    return (0 until segmentCount).map { i ->
        val angle = startAngle + arcAngle * i / (segmentCount - 1).coerceAtLeast(1)
        val rad = Math.toRadians(angle.toDouble())
        Offset(radius * cos(rad).toFloat(), radius * sin(rad).toFloat())
    }
}

fun getSectorVerticesWithGap(
    innerRadius: Float,
    outerRadius: Float,
    sector: Sector,
    gap: Float,
    segmentCount: Int,
): Pair<List<Offset>, List<Offset>> {
    val innerGapAngle = Math.toDegrees((gap / innerRadius).toDouble()).toFloat()
    val outerGapAngle = Math.toDegrees((gap / outerRadius).toDouble()).toFloat()

    val innerStart = sector.start + innerGapAngle / 2f
    val innerArch = (sector.arch - innerGapAngle).coerceAtLeast(0f)
    val outerStart = sector.start + outerGapAngle / 2f
    val outerArch = (sector.arch - outerGapAngle).coerceAtLeast(0f)

    val innerVertices = getSectorVertices(innerRadius, innerStart, innerArch, segmentCount)
    val outerVertices = getSectorVertices(outerRadius, outerStart, outerArch, segmentCount)

    return innerVertices to outerVertices
}

fun getRadialSectorQuads(
    center: Offset,
    sector: Sector,
    innerRadius: Float,
    outerRadius: Float,
    gap: Float,
): List<SectorQuad> {
    val segmentCount = if (sector.arch <= 90f) 16 else 32

    val (innerVertices, outerVertices) = if (gap > 0f) {
        getSectorVerticesWithGap(innerRadius, outerRadius, sector, gap, segmentCount)
    } else {
        getSectorVertices(innerRadius, sector.start, sector.arch, segmentCount) to
                getSectorVertices(outerRadius, sector.start, sector.arch, segmentCount)
    }

    return buildList {
        for (i in 0 until outerVertices.lastIndex) {
            add(
                SectorQuad(
                    p1 = Offset(center.x + outerVertices[i].x, center.y + outerVertices[i].y),
                    p2 = Offset(center.x + innerVertices[i].x, center.y + innerVertices[i].y),
                    p3 = Offset(center.x + innerVertices[i + 1].x, center.y + innerVertices[i + 1].y),
                    p4 = Offset(center.x + outerVertices[i + 1].x, center.y + outerVertices[i + 1].y),
                )
            )
        }
    }
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
