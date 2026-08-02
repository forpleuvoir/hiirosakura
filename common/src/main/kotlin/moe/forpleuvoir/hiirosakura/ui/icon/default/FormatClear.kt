package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.FormatClear: ImageVector
    get() {
        if (formatClear != null) {
            return formatClear!!
        }
        formatClear =
            ImageVector.Builder(
                name = "format_clear",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(13.2f, 10.35f)
                        lineTo(10.88f, 8.02f)
                        quadTo(10.55f, 7.7f, 10.21f, 7.36f)
                        quadTo(9.88f, 7.02f, 9.55f, 6.7f)
                        quadTo(9.08f, 6.22f, 9.34f, 5.61f)
                        reflectiveQuadTo(10.28f, 5f)
                        horizontalLineTo(18.5f)
                        quadToRelative(0.63f, 0f, 1.06f, 0.44f)
                        reflectiveQuadTo(20f, 6.5f)
                        reflectiveQuadTo(19.56f, 7.56f)
                        reflectiveQuadTo(18.5f, 8f)
                        horizontalLineTo(14.2f)
                        lineToRelative(-1f, 2.35f)
                        close()
                        moveTo(19.1f, 21.9f)
                        lineTo(11.5f, 14.3f)
                        lineTo(9.9f, 18.08f)
                        quadTo(9.73f, 18.5f, 9.34f, 18.75f)
                        reflectiveQuadTo(8.5f, 19f)
                        quadTo(7.7f, 19f, 7.25f, 18.33f)
                        reflectiveQuadTo(7.13f, 16.9f)
                        lineTo(9.2f, 12f)
                        lineTo(2.1f, 4.9f)
                        quadTo(1.83f, 4.63f, 1.83f, 4.2f)
                        reflectiveQuadTo(2.1f, 3.5f)
                        quadTo(2.38f, 3.22f, 2.8f, 3.22f)
                        reflectiveQuadTo(3.5f, 3.5f)
                        lineToRelative(17f, 17f)
                        quadToRelative(0.28f, 0.27f, 0.28f, 0.7f)
                        reflectiveQuadTo(20.5f, 21.9f)
                        quadToRelative(-0.27f, 0.28f, -0.7f, 0.28f)
                        reflectiveQuadTo(19.1f, 21.9f)
                        close()
                    }
                }
                .build()
        return formatClear!!
    }

private var formatClear: ImageVector? = null