package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.Link2: ImageVector
    get() {
        if (link2 != null) {
            return link2!!
        }
        link2 =
            ImageVector.Builder(
                name = "link_2",
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
                        moveTo(7.95f, 21f)
                        quadTo(5.9f, 21f, 4.45f, 19.55f)
                        reflectiveQuadTo(3f, 16.05f)
                        quadToRelative(0f, -1f, 0.38f, -1.9f)
                        reflectiveQuadToRelative(1.08f, -1.6f)
                        lineTo(7.08f, 9.92f)
                        quadToRelative(0.3f, -0.3f, 0.71f, -0.3f)
                        reflectiveQuadTo(8.5f, 9.92f)
                        quadToRelative(0.3f, 0.3f, 0.3f, 0.7f)
                        quadToRelative(0f, 0.4f, -0.3f, 0.7f)
                        lineTo(5.85f, 13.98f)
                        quadTo(5.43f, 14.4f, 5.21f, 14.94f)
                        reflectiveQuadTo(5f, 16.05f)
                        quadToRelative(0f, 1.22f, 0.86f, 2.09f)
                        reflectiveQuadTo(7.95f, 19f)
                        quadToRelative(0.58f, 0f, 1.13f, -0.21f)
                        reflectiveQuadToRelative(0.97f, -0.64f)
                        lineTo(12.68f, 15.5f)
                        quadToRelative(0.3f, -0.28f, 0.7f, -0.28f)
                        quadToRelative(0.4f, 0f, 0.7f, 0.3f)
                        reflectiveQuadToRelative(0.3f, 0.7f)
                        reflectiveQuadToRelative(-0.3f, 0.7f)
                        lineToRelative(-2.63f, 2.63f)
                        quadToRelative(-0.7f, 0.7f, -1.6f, 1.07f)
                        reflectiveQuadTo(7.95f, 21f)
                        close()
                        moveTo(9.2f, 14.8f)
                        quadTo(8.9f, 14.5f, 8.9f, 14.09f)
                        quadToRelative(0f, -0.41f, 0.3f, -0.71f)
                        lineTo(13.38f, 9.2f)
                        quadToRelative(0.3f, -0.3f, 0.71f, -0.3f)
                        reflectiveQuadTo(14.8f, 9.2f)
                        reflectiveQuadToRelative(0.3f, 0.71f)
                        reflectiveQuadToRelative(-0.3f, 0.71f)
                        lineTo(10.63f, 14.8f)
                        quadToRelative(-0.3f, 0.3f, -0.71f, 0.3f)
                        reflectiveQuadTo(9.2f, 14.8f)
                        close()
                        moveToRelative(6.3f, -0.72f)
                        quadToRelative(-0.3f, -0.3f, -0.3f, -0.7f)
                        reflectiveQuadToRelative(0.3f, -0.7f)
                        lineToRelative(2.65f, -2.63f)
                        quadTo(18.58f, 9.63f, 18.78f, 9.1f)
                        reflectiveQuadTo(18.98f, 8f)
                        quadToRelative(0f, -1.25f, -0.85f, -2.13f)
                        quadTo(17.28f, 5f, 16.03f, 5f)
                        quadTo(15.45f, 5f, 14.91f, 5.21f)
                        quadTo(14.38f, 5.43f, 13.95f, 5.85f)
                        lineTo(11.33f, 8.5f)
                        quadToRelative(-0.3f, 0.3f, -0.7f, 0.3f)
                        reflectiveQuadTo(9.93f, 8.5f)
                        reflectiveQuadTo(9.63f, 7.79f)
                        quadToRelative(0f, -0.41f, 0.3f, -0.71f)
                        lineTo(12.55f, 4.45f)
                        quadToRelative(0.7f, -0.7f, 1.6f, -1.07f)
                        reflectiveQuadTo(16.05f, 3f)
                        quadToRelative(2.05f, 0f, 3.49f, 1.45f)
                        reflectiveQuadToRelative(1.44f, 3.52f)
                        quadToRelative(0f, 0.98f, -0.36f, 1.88f)
                        reflectiveQuadToRelative(-1.06f, 1.6f)
                        lineToRelative(-2.63f, 2.63f)
                        quadToRelative(-0.3f, 0.3f, -0.71f, 0.3f)
                        reflectiveQuadTo(15.5f, 14.08f)
                        close()
                    }
                }
                .build()
        return link2!!
    }

private var link2: ImageVector? = null