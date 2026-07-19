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

    val InnerRadius: Dp = 150.dp
    val OuterRadius: Dp = 330.dp
    val OptionRadius: Dp = 240.dp
    const val START_ANGLE_DEGREE: Float = -90f
    const val GAP: Float = 12f
    val CornerRadius: Dp = 4.dp
    val BorderWidth: Dp = 2.dp
    const val OPTIONS_PRE_PAGE: Int = 8
    val OptionContentSize: Dp = 72.dp
    val IndicatorHeight: Dp = 6.dp
    val IndicatorSpacing: Dp = 2.dp

    val IdleOuterColor: Color = Color(0x7F000000)
    val IdleInnerColor: Color = Color(0x33000000)
    val IdleBorderColor: Color = Color(0x33FFFFFF)
    val SelectedOuterColor: Color = Color(0xFFFF8899)
    val SelectedInnerColor: Color = Color(0x33FF8899)
    val SelectedBorderColor: Color = Color(0xFFFF8899)
    val IndicatorActiveColor: Color = Color.White
    val IndicatorInactiveColor: Color = Color.White.copy(alpha = 0.35f)

    /** 默认的扇区动画参数（共享实例，保证默认参数相等性） */
    val Animation: RadialMenuAnimation = RadialMenuAnimation()
}

internal fun DrawScope.drawRadialGradientSector(
    path: Path,
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    innerColor: Color,
    outerColor: Color,
) {
    if (path.isEmpty) return
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
