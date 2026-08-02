package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.Assignment: ImageVector
    get() {
        if (assignment != null) {
            return assignment!!
        }
        assignment =
            ImageVector.Builder(
                name = "assignment",
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
                        moveTo(5f, 21f)
                        quadTo(4.18f, 21f, 3.59f, 20.41f)
                        reflectiveQuadTo(3f, 19f)
                        verticalLineTo(5f)
                        quadTo(3f, 4.17f, 3.59f, 3.59f)
                        reflectiveQuadTo(5f, 3f)
                        horizontalLineTo(9.2f)
                        quadTo(9.53f, 2.1f, 10.29f, 1.55f)
                        reflectiveQuadTo(12f, 1f)
                        reflectiveQuadToRelative(1.71f, 0.55f)
                        reflectiveQuadTo(14.8f, 3f)
                        horizontalLineTo(19f)
                        quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                        reflectiveQuadTo(21f, 5f)
                        verticalLineTo(19f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(19f, 21f)
                        horizontalLineTo(5f)
                        close()
                        moveTo(5f, 19f)
                        horizontalLineTo(19f)
                        verticalLineTo(5f)
                        horizontalLineTo(5f)
                        verticalLineTo(19f)
                        close()
                        moveTo(8f, 17f)
                        horizontalLineToRelative(5f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        quadTo(14f, 16.43f, 14f, 16f)
                        reflectiveQuadTo(13.71f, 15.29f)
                        reflectiveQuadTo(13f, 15f)
                        horizontalLineTo(8f)
                        quadTo(7.58f, 15f, 7.29f, 15.29f)
                        reflectiveQuadTo(7f, 16f)
                        reflectiveQuadToRelative(0.29f, 0.71f)
                        reflectiveQuadTo(8f, 17f)
                        close()
                        moveTo(8f, 13f)
                        horizontalLineToRelative(8f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        quadTo(17f, 12.43f, 17f, 12f)
                        reflectiveQuadTo(16.71f, 11.29f)
                        reflectiveQuadTo(16f, 11f)
                        horizontalLineTo(8f)
                        quadTo(7.58f, 11f, 7.29f, 11.29f)
                        reflectiveQuadTo(7f, 12f)
                        reflectiveQuadToRelative(0.29f, 0.71f)
                        reflectiveQuadTo(8f, 13f)
                        close()
                        moveTo(8f, 9f)
                        horizontalLineToRelative(8f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        reflectiveQuadTo(17f, 8f)
                        quadTo(17f, 7.57f, 16.71f, 7.29f)
                        reflectiveQuadTo(16f, 7f)
                        horizontalLineTo(8f)
                        quadTo(7.58f, 7f, 7.29f, 7.29f)
                        reflectiveQuadTo(7f, 8f)
                        quadTo(7f, 8.42f, 7.29f, 8.71f)
                        reflectiveQuadTo(8f, 9f)
                        close()
                        moveTo(12.54f, 4.04f)
                        quadTo(12.75f, 3.82f, 12.75f, 3.5f)
                        quadToRelative(0f, -0.33f, -0.21f, -0.54f)
                        reflectiveQuadTo(12f, 2.75f)
                        reflectiveQuadTo(11.46f, 2.96f)
                        reflectiveQuadTo(11.25f, 3.5f)
                        quadToRelative(0f, 0.32f, 0.21f, 0.54f)
                        reflectiveQuadTo(12f, 4.25f)
                        reflectiveQuadTo(12.54f, 4.04f)
                        close()
                        moveTo(5f, 19f)
                        verticalLineTo(5f)
                        verticalLineTo(19f)
                        close()
                    }
                }
                .build()
        return assignment!!
    }

private var assignment: ImageVector? = null