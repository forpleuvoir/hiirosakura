package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.EditSquare: ImageVector
    get() {
        if (editSquare != null) {
            return editSquare!!
        }
        editSquare =
            ImageVector.Builder(
                name = "edit_square",
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
                        horizontalLineToRelative(6.53f)
                        quadToRelative(0.5f, 0f, 0.75f, 0.31f)
                        reflectiveQuadTo(12.53f, 4f)
                        reflectiveQuadTo(12.26f, 4.69f)
                        reflectiveQuadTo(11.5f, 5f)
                        horizontalLineTo(5f)
                        verticalLineTo(19f)
                        horizontalLineTo(19f)
                        verticalLineTo(12.48f)
                        quadToRelative(0f, -0.5f, 0.31f, -0.75f)
                        reflectiveQuadTo(20f, 11.48f)
                        reflectiveQuadToRelative(0.69f, 0.25f)
                        reflectiveQuadTo(21f, 12.48f)
                        verticalLineTo(19f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(19f, 21f)
                        horizontalLineTo(5f)
                        close()
                        moveToRelative(7f, -9f)
                        close()
                        moveTo(9f, 14f)
                        verticalLineTo(11.58f)
                        quadToRelative(0f, -0.4f, 0.15f, -0.76f)
                        reflectiveQuadTo(9.58f, 10.17f)
                        lineToRelative(8.6f, -8.6f)
                        quadToRelative(0.3f, -0.3f, 0.68f, -0.45f)
                        quadTo(19.23f, 0.97f, 19.6f, 0.97f)
                        quadToRelative(0.4f, 0f, 0.76f, 0.15f)
                        quadToRelative(0.36f, 0.15f, 0.66f, 0.45f)
                        lineTo(22.43f, 3f)
                        quadToRelative(0.28f, 0.3f, 0.43f, 0.66f)
                        reflectiveQuadTo(23f, 4.4f)
                        reflectiveQuadTo(22.86f, 5.14f)
                        reflectiveQuadTo(22.43f, 5.8f)
                        lineToRelative(-8.6f, 8.6f)
                        quadToRelative(-0.28f, 0.28f, -0.64f, 0.44f)
                        reflectiveQuadTo(12.43f, 15f)
                        horizontalLineTo(10f)
                        quadTo(9.58f, 15f, 9.29f, 14.71f)
                        reflectiveQuadTo(9f, 14f)
                        close()
                        moveTo(21.03f, 4.4f)
                        lineTo(19.63f, 3f)
                        lineToRelative(1.4f, 1.4f)
                        close()
                        moveTo(11f, 13f)
                        horizontalLineToRelative(1.4f)
                        lineTo(18.2f, 7.2f)
                        lineTo(17.5f, 6.5f)
                        lineTo(16.78f, 5.8f)
                        lineTo(11f, 11.58f)
                        verticalLineTo(13f)
                        close()
                        moveTo(17.5f, 6.5f)
                        lineTo(16.78f, 5.8f)
                        lineTo(17.5f, 6.5f)
                        lineToRelative(0.7f, 0.7f)
                        lineTo(17.5f, 6.5f)
                        close()
                    }
                }
                .build()
        return editSquare!!
    }

private var editSquare: ImageVector? = null