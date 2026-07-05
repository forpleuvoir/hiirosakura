package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.Equal: ImageVector
    get() {
        if (equal != null) {
            return equal!!
        }
        equal =
            ImageVector.Builder(
                name = "equal",
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
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(5.5f, 17f)
                        quadTo(4.88f, 17f, 4.44f, 16.56f)
                        reflectiveQuadTo(4f, 15.5f)
                        reflectiveQuadTo(4.44f, 14.44f)
                        reflectiveQuadTo(5.5f, 14f)
                        horizontalLineToRelative(13f)
                        quadToRelative(0.63f, 0f, 1.06f, 0.44f)
                        reflectiveQuadTo(20f, 15.5f)
                        reflectiveQuadToRelative(-0.44f, 1.06f)
                        reflectiveQuadTo(18.5f, 17f)
                        horizontalLineTo(5.5f)
                        close()
                        moveToRelative(0f, -7f)
                        quadTo(4.88f, 10f, 4.44f, 9.56f)
                        reflectiveQuadTo(4f, 8.5f)
                        reflectiveQuadTo(4.44f, 7.44f)
                        reflectiveQuadTo(5.5f, 7f)
                        horizontalLineToRelative(13f)
                        quadToRelative(0.63f, 0f, 1.06f, 0.44f)
                        reflectiveQuadTo(20f, 8.5f)
                        reflectiveQuadTo(19.56f, 9.56f)
                        reflectiveQuadTo(18.5f, 10f)
                        horizontalLineTo(5.5f)
                        close()
                    }
                }
                .build()
        return equal!!
    }

private var equal: ImageVector? = null