package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.EditLocationAlt: ImageVector
    get() {
        if (editLocationAlt != null) {
            return editLocationAlt!!
        }
        editLocationAlt =
            ImageVector.Builder(
                name = "edit_location_alt",
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
                        moveTo(4f, 10.2f)
                        quadTo(4f, 6.45f, 6.41f, 4.22f)
                        reflectiveQuadTo(12f, 2f)
                        quadToRelative(0.05f, 0f, 0.13f, 0f)
                        reflectiveQuadToRelative(0.13f, 0f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(13.25f, 3f)
                        quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                        reflectiveQuadTo(12.25f, 4f)
                        quadTo(12.2f, 4f, 12.13f, 4f)
                        reflectiveQuadTo(12f, 4f)
                        quadTo(9.48f, 4f, 7.74f, 5.74f)
                        quadTo(6f, 7.47f, 6f, 10.2f)
                        quadToRelative(0f, 1.78f, 1.48f, 4.06f)
                        reflectiveQuadTo(12f, 19.35f)
                        quadToRelative(3.05f, -2.8f, 4.53f, -5.09f)
                        quadTo(18f, 11.98f, 18f, 10.2f)
                        quadTo(18f, 9.77f, 18.29f, 9.49f)
                        reflectiveQuadTo(19f, 9.2f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(20f, 10.2f)
                        quadToRelative(0f, 2.35f, -1.7f, 5.04f)
                        quadToRelative(-1.7f, 2.69f, -4.97f, 5.59f)
                        quadTo(13.05f, 21.08f, 12.7f, 21.2f)
                        reflectiveQuadTo(12f, 21.33f)
                        reflectiveQuadTo(11.3f, 21.2f)
                        reflectiveQuadTo(10.68f, 20.83f)
                        quadTo(9.05f, 19.33f, 7.8f, 17.9f)
                        quadTo(6.55f, 16.48f, 5.71f, 15.14f)
                        reflectiveQuadTo(4.44f, 12.56f)
                        reflectiveQuadTo(4f, 10.2f)
                        close()
                        moveToRelative(8f, 0f)
                        close()
                        moveToRelative(1.4f, 0.5f)
                        lineTo(18.95f, 5.15f)
                        quadTo(19.1f, 5f, 19.1f, 4.8f)
                        reflectiveQuadTo(18.95f, 4.45f)
                        lineToRelative(-1.4f, -1.4f)
                        quadTo(17.4f, 2.9f, 17.2f, 2.9f)
                        reflectiveQuadTo(16.85f, 3.05f)
                        lineTo(11.3f, 8.6f)
                        quadTo(11.15f, 8.75f, 11.08f, 8.94f)
                        reflectiveQuadTo(11f, 9.32f)
                        verticalLineToRelative(0.93f)
                        quadToRelative(0f, 0.32f, 0.21f, 0.54f)
                        reflectiveQuadTo(11.75f, 11f)
                        horizontalLineToRelative(0.93f)
                        quadToRelative(0.2f, 0f, 0.39f, -0.08f)
                        reflectiveQuadTo(13.4f, 10.7f)
                        close()
                        moveTo(19.65f, 3.75f)
                        quadTo(19.8f, 3.9f, 20f, 3.9f)
                        reflectiveQuadTo(20.35f, 3.75f)
                        lineTo(20.7f, 3.4f)
                        quadTo(20.98f, 3.13f, 20.98f, 2.7f)
                        reflectiveQuadTo(20.7f, 2f)
                        lineTo(20f, 1.3f)
                        quadTo(19.73f, 1.02f, 19.3f, 1.02f)
                        reflectiveQuadTo(18.6f, 1.3f)
                        lineTo(18.25f, 1.65f)
                        quadTo(18.1f, 1.8f, 18.1f, 2f)
                        reflectiveQuadToRelative(0.15f, 0.35f)
                        lineToRelative(1.4f, 1.4f)
                        close()
                    }
                }
                .build()
        return editLocationAlt!!
    }

private var editLocationAlt: ImageVector? = null