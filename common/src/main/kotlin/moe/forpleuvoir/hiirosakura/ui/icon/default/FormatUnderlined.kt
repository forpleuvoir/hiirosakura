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
val Icons.FormatUnderlined: ImageVector
    get() {
        if (formatUnderlined != null) {
            return formatUnderlined!!
        }
        formatUnderlined =
            ImageVector.Builder(
                name = "format_underlined",
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
                        moveTo(6f, 21f)
                        quadTo(5.58f, 21f, 5.29f, 20.71f)
                        quadTo(5f, 20.43f, 5f, 20f)
                        reflectiveQuadTo(5.29f, 19.29f)
                        reflectiveQuadTo(6f, 19f)
                        horizontalLineTo(18f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(19f, 20f)
                        reflectiveQuadToRelative(-0.29f, 0.71f)
                        reflectiveQuadTo(18f, 21f)
                        horizontalLineTo(6f)
                        close()
                        moveTo(8.08f, 15.43f)
                        quadTo(6.68f, 13.85f, 6.68f, 11.25f)
                        verticalLineTo(4.27f)
                        quadToRelative(0f, -0.52f, 0.39f, -0.9f)
                        reflectiveQuadTo(7.98f, 3f)
                        reflectiveQuadToRelative(0.9f, 0.38f)
                        reflectiveQuadToRelative(0.38f, 0.9f)
                        verticalLineTo(11.4f)
                        quadToRelative(0f, 1.4f, 0.7f, 2.28f)
                        reflectiveQuadTo(12f, 14.55f)
                        reflectiveQuadToRelative(2.05f, -0.88f)
                        reflectiveQuadToRelative(0.7f, -2.28f)
                        verticalLineTo(4.27f)
                        quadToRelative(0f, -0.52f, 0.39f, -0.9f)
                        reflectiveQuadTo(16.05f, 3f)
                        reflectiveQuadToRelative(0.9f, 0.38f)
                        reflectiveQuadToRelative(0.38f, 0.9f)
                        verticalLineToRelative(6.98f)
                        quadToRelative(0f, 2.6f, -1.4f, 4.17f)
                        reflectiveQuadTo(12f, 17f)
                        reflectiveQuadTo(8.08f, 15.43f)
                        close()
                    }
                }
                .build()
        return formatUnderlined!!
    }

private var formatUnderlined: ImageVector? = null