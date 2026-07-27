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
val Icons.FormatBold: ImageVector
    get() {
        if (formatBold != null) {
            return formatBold!!
        }
        formatBold =
            ImageVector.Builder(
                name = "format_bold",
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
                        moveTo(8.8f, 19f)
                        quadTo(7.98f, 19f, 7.39f, 18.41f)
                        reflectiveQuadTo(6.8f, 17f)
                        verticalLineTo(7f)
                        quadTo(6.8f, 6.18f, 7.39f, 5.59f)
                        reflectiveQuadTo(8.8f, 5f)
                        horizontalLineToRelative(3.53f)
                        quadToRelative(1.63f, 0f, 3f, 1f)
                        reflectiveQuadTo(16.7f, 8.77f)
                        quadToRelative(0f, 1.28f, -0.57f, 1.96f)
                        reflectiveQuadToRelative(-1.07f, 0.99f)
                        quadToRelative(0.63f, 0.28f, 1.39f, 1.03f)
                        reflectiveQuadTo(17.2f, 15f)
                        quadToRelative(0f, 2.23f, -1.62f, 3.11f)
                        reflectiveQuadTo(12.53f, 19f)
                        horizontalLineTo(8.8f)
                        close()
                        moveTo(9.83f, 16.2f)
                        horizontalLineToRelative(2.6f)
                        quadToRelative(1.2f, 0f, 1.46f, -0.61f)
                        reflectiveQuadTo(14.15f, 14.7f)
                        reflectiveQuadTo(13.89f, 13.81f)
                        reflectiveQuadTo(12.35f, 13.2f)
                        horizontalLineTo(9.83f)
                        verticalLineToRelative(3f)
                        close()
                        moveToRelative(0f, -5.7f)
                        horizontalLineToRelative(2.32f)
                        quadToRelative(0.83f, 0f, 1.2f, -0.43f)
                        reflectiveQuadTo(13.73f, 9.13f)
                        quadToRelative(0f, -0.6f, -0.43f, -0.98f)
                        reflectiveQuadTo(12.2f, 7.77f)
                        horizontalLineTo(9.83f)
                        verticalLineTo(10.5f)
                        close()
                    }
                }
                .build()
        return formatBold!!
    }

private var formatBold: ImageVector? = null