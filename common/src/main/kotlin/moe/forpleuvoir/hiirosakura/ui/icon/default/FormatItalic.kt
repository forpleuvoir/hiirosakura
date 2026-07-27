package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.FormatItalic: ImageVector
    get() {
        if (formatItalic != null) {
            return formatItalic!!
        }
        formatItalic =
            ImageVector.Builder(
                name = "format_italic",
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
                        moveTo(6.25f, 19f)
                        quadTo(5.73f, 19f, 5.36f, 18.64f)
                        reflectiveQuadTo(5f, 17.75f)
                        reflectiveQuadTo(5.36f, 16.86f)
                        reflectiveQuadTo(6.25f, 16.5f)
                        horizontalLineTo(9f)
                        lineToRelative(3f, -9f)
                        horizontalLineTo(9.25f)
                        quadTo(8.73f, 7.5f, 8.36f, 7.14f)
                        reflectiveQuadTo(8f, 6.25f)
                        quadTo(8f, 5.72f, 8.36f, 5.36f)
                        reflectiveQuadTo(9.25f, 5f)
                        horizontalLineToRelative(7.5f)
                        quadToRelative(0.53f, 0f, 0.89f, 0.36f)
                        quadTo(18f, 5.72f, 18f, 6.25f)
                        quadToRelative(0f, 0.52f, -0.36f, 0.89f)
                        quadTo(17.28f, 7.5f, 16.75f, 7.5f)
                        horizontalLineTo(14.5f)
                        lineToRelative(-3f, 9f)
                        horizontalLineToRelative(2.25f)
                        quadToRelative(0.53f, 0f, 0.89f, 0.36f)
                        reflectiveQuadTo(15f, 17.75f)
                        reflectiveQuadToRelative(-0.36f, 0.89f)
                        reflectiveQuadTo(13.75f, 19f)
                        horizontalLineTo(6.25f)
                        close()
                    }
                }
                .build()
        return formatItalic!!
    }

private var formatItalic: ImageVector? = null