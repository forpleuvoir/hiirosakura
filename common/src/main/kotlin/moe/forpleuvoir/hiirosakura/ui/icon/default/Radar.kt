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
val Icons.Radar: ImageVector
    get() {
        if (radar != null) {
            return radar!!
        }
        radar =
            ImageVector.Builder(
                name = "radar",
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
                        moveTo(8.1f, 21.21f)
                        quadTo(6.28f, 20.43f, 4.93f, 19.08f)
                        quadTo(3.58f, 17.73f, 2.79f, 15.9f)
                        reflectiveQuadTo(2f, 12f)
                        quadTo(2f, 9.92f, 2.79f, 8.1f)
                        quadTo(3.58f, 6.27f, 4.93f, 4.93f)
                        quadTo(6.28f, 3.57f, 8.1f, 2.79f)
                        quadTo(9.93f, 2f, 12f, 2f)
                        reflectiveQuadToRelative(3.9f, 0.79f)
                        reflectiveQuadToRelative(3.17f, 2.14f)
                        quadToRelative(1.35f, 1.35f, 2.14f, 3.17f)
                        quadTo(22f, 9.92f, 22f, 12f)
                        reflectiveQuadToRelative(-0.79f, 3.9f)
                        reflectiveQuadToRelative(-2.14f, 3.17f)
                        quadToRelative(-1.35f, 1.35f, -3.17f, 2.14f)
                        reflectiveQuadTo(12f, 22f)
                        quadTo(9.93f, 22f, 8.1f, 21.21f)
                        close()
                        moveTo(12f, 20f)
                        quadToRelative(1.4f, 0f, 2.64f, -0.44f)
                        reflectiveQuadTo(16.9f, 18.33f)
                        lineTo(15.48f, 16.9f)
                        quadToRelative(-0.73f, 0.53f, -1.61f, 0.81f)
                        reflectiveQuadTo(12f, 18f)
                        quadTo(9.5f, 18f, 7.75f, 16.25f)
                        reflectiveQuadTo(6f, 12f)
                        reflectiveQuadTo(7.75f, 7.75f)
                        reflectiveQuadTo(12f, 6f)
                        reflectiveQuadToRelative(4.25f, 1.75f)
                        reflectiveQuadTo(18f, 12f)
                        quadToRelative(0f, 0.97f, -0.3f, 1.88f)
                        reflectiveQuadTo(16.88f, 15.5f)
                        lineToRelative(1.43f, 1.43f)
                        quadToRelative(0.8f, -1.03f, 1.25f, -2.28f)
                        reflectiveQuadTo(20f, 12f)
                        quadTo(20f, 8.65f, 17.68f, 6.32f)
                        reflectiveQuadTo(12f, 4f)
                        reflectiveQuadTo(6.33f, 6.32f)
                        reflectiveQuadTo(4f, 12f)
                        reflectiveQuadToRelative(2.33f, 5.68f)
                        reflectiveQuadTo(12f, 20f)
                        close()
                        moveToRelative(0f, -4f)
                        quadToRelative(0.55f, 0f, 1.06f, -0.14f)
                        quadToRelative(0.51f, -0.14f, 0.96f, -0.41f)
                        lineTo(12.5f, 13.93f)
                        quadToRelative(-0.13f, 0.05f, -0.25f, 0.06f)
                        reflectiveQuadTo(12f, 14f)
                        quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                        reflectiveQuadTo(10f, 12f)
                        reflectiveQuadToRelative(0.59f, -1.41f)
                        reflectiveQuadTo(12f, 10f)
                        reflectiveQuadToRelative(1.41f, 0.59f)
                        quadTo(14f, 11.18f, 14f, 12f)
                        quadToRelative(0f, 0.15f, -0.01f, 0.29f)
                        reflectiveQuadToRelative(-0.06f, 0.26f)
                        lineToRelative(1.5f, 1.5f)
                        quadTo(15.7f, 13.6f, 15.85f, 13.09f)
                        quadTo(16f, 12.58f, 16f, 12f)
                        quadTo(16f, 10.35f, 14.83f, 9.17f)
                        reflectiveQuadTo(12f, 8f)
                        reflectiveQuadTo(9.18f, 9.17f)
                        reflectiveQuadTo(8f, 12f)
                        reflectiveQuadToRelative(1.18f, 2.82f)
                        reflectiveQuadTo(12f, 16f)
                        close()
                    }
                }
                .build()
        return radar!!
    }

private var radar: ImageVector? = null