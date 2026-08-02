package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.Task: ImageVector
    get() {
        if (task != null) {
            return task!!
        }
        task =
            ImageVector.Builder(
                name = "task",
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
                        moveTo(10.93f, 15.13f)
                        lineToRelative(-1.4f, -1.4f)
                        quadTo(9.38f, 13.58f, 9.2f, 13.5f)
                        reflectiveQuadTo(8.84f, 13.43f)
                        reflectiveQuadTo(8.46f, 13.5f)
                        reflectiveQuadTo(8.13f, 13.73f)
                        quadToRelative(-0.3f, 0.3f, -0.3f, 0.71f)
                        reflectiveQuadToRelative(0.3f, 0.71f)
                        lineToRelative(2.13f, 2.15f)
                        quadToRelative(0.15f, 0.15f, 0.33f, 0.21f)
                        reflectiveQuadToRelative(0.38f, 0.06f)
                        reflectiveQuadToRelative(0.38f, -0.06f)
                        reflectiveQuadTo(11.65f, 17.3f)
                        lineToRelative(4.22f, -4.23f)
                        quadToRelative(0.3f, -0.3f, 0.3f, -0.72f)
                        reflectiveQuadToRelative(-0.3f, -0.72f)
                        reflectiveQuadToRelative(-0.72f, -0.3f)
                        reflectiveQuadToRelative(-0.72f, 0.3f)
                        lineToRelative(-3.5f, 3.5f)
                        close()
                        moveTo(6f, 22f)
                        quadTo(5.18f, 22f, 4.59f, 21.41f)
                        reflectiveQuadTo(4f, 20f)
                        verticalLineTo(4f)
                        quadTo(4f, 3.17f, 4.59f, 2.59f)
                        reflectiveQuadTo(6f, 2f)
                        horizontalLineToRelative(7.18f)
                        quadToRelative(0.4f, 0f, 0.76f, 0.15f)
                        reflectiveQuadToRelative(0.64f, 0.43f)
                        lineToRelative(4.85f, 4.85f)
                        quadTo(19.7f, 7.7f, 19.85f, 8.06f)
                        quadTo(20f, 8.42f, 20f, 8.82f)
                        verticalLineTo(20f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(18f, 22f)
                        horizontalLineTo(6f)
                        close()
                        moveTo(13f, 8f)
                        verticalLineTo(4f)
                        horizontalLineTo(6f)
                        verticalLineTo(20f)
                        horizontalLineTo(18f)
                        verticalLineTo(9f)
                        horizontalLineTo(14f)
                        quadTo(13.58f, 9f, 13.29f, 8.71f)
                        reflectiveQuadTo(13f, 8f)
                        close()
                        moveTo(6f, 4f)
                        verticalLineTo(8f)
                        quadTo(6f, 8.42f, 6f, 8.71f)
                        reflectiveQuadTo(6f, 9f)
                        verticalLineTo(4f)
                        verticalLineTo(8f)
                        quadTo(6f, 8.42f, 6f, 8.71f)
                        reflectiveQuadTo(6f, 9f)
                        verticalLineTo(20f)
                        verticalLineTo(4f)
                        close()
                    }
                }
                .build()
        return task!!
    }

private var task: ImageVector? = null