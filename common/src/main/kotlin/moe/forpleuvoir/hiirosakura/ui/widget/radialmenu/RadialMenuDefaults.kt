package moe.forpleuvoir.hiirosakura.ui.widget.radialmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object RadialMenuDefaults {

    val InnerRadius: Dp = 60.dp
    val OuterRadius: Dp = 120.dp
    val OptionRadius: Dp = 90.dp
    const val START_ANGLE_DEGREE: Float = -90f
    const val GAP: Float = 8f
    const val OPTIONS_PRE_PAGE: Int = 8
    val OptionContentSize: Dp = 32.dp
    val IndicatorHeight: Dp = 6.dp
    val IndicatorSpacing: Dp = 2.dp

    val IdleOuterColor: Color = Color(0x7F000000)
    val IdleInnerColor: Color = Color(0x33000000)
    val SelectedOuterColor: Color = Color(0xFFFF8899)
    val SelectedInnerColor: Color = Color(0x33FF8899)
    val IndicatorActiveColor: Color = Color.White
    val IndicatorInactiveColor: Color = Color.White.copy(alpha = 0.35f)
}

internal fun DrawScope.drawRadialGradientSector(
    quads: List<SectorQuad>,
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    innerColor: Color,
    outerColor: Color,
) {
    if (quads.isEmpty()) return
    val path = Path()
    quads.forEach { quad ->
        path.apply {
            moveTo(quad.p1.x, quad.p1.y)
            lineTo(quad.p2.x, quad.p2.y)
            lineTo(quad.p3.x, quad.p3.y)
            lineTo(quad.p4.x, quad.p4.y)
            close()
        }
    }
    val fraction = (innerRadius / outerRadius).coerceIn(0f, 0.99f)
    val brush = Brush.radialGradient(
        colorStops = arrayOf(
            0f to innerColor,
            fraction to innerColor,
            1f to outerColor,
        ),
        center = center,
        radius = outerRadius,
    )
    drawPath(path, brush)
}

@Composable
internal fun PageIndicator(
    pageCount: Int,
    currentPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    if (pageCount <= 1) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(RadialMenuDefaults.IndicatorSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .width(indicatorWidth(pageCount))
                    .height(RadialMenuDefaults.IndicatorHeight)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPageIndex)
                            RadialMenuDefaults.IndicatorActiveColor
                        else
                            RadialMenuDefaults.IndicatorInactiveColor
                    )
            )
        }
    }
}

private fun indicatorWidth(pageCount: Int): Dp {
    if (pageCount <= 1) return 0.dp
    val maxWidth = 48.dp
    val minWidth = 12.dp
    val w = maxWidth / pageCount.coerceAtLeast(1)
    return w.coerceAtMost(maxWidth).coerceAtLeast(minWidth)
}
