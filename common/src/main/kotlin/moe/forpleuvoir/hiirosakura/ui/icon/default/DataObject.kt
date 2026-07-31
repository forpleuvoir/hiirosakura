package moe.forpleuvoir.hiirosakura.ui.icon.default

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.DataObject: ImageVector
    get() {
        if (dataObject != null) {
            return dataObject!!
        }
        dataObject =
            ImageVector.Builder(
                name = "data_object",
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
                        moveTo(15f, 20f)
                        quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                        quadTo(14f, 19.43f, 14f, 19f)
                        reflectiveQuadToRelative(0.29f, -0.71f)
                        reflectiveQuadTo(15f, 18f)
                        horizontalLineToRelative(2f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        quadTo(18f, 17.43f, 18f, 17f)
                        verticalLineTo(15f)
                        quadToRelative(0f, -0.95f, 0.55f, -1.73f)
                        reflectiveQuadTo(20f, 12.18f)
                        verticalLineTo(11.83f)
                        quadTo(19.1f, 11.5f, 18.55f, 10.73f)
                        reflectiveQuadTo(18f, 9f)
                        verticalLineTo(7f)
                        quadTo(18f, 6.57f, 17.71f, 6.29f)
                        reflectiveQuadTo(17f, 6f)
                        horizontalLineTo(15f)
                        quadTo(14.58f, 6f, 14.29f, 5.71f)
                        quadTo(14f, 5.43f, 14f, 5f)
                        reflectiveQuadTo(14.29f, 4.29f)
                        reflectiveQuadTo(15f, 4f)
                        horizontalLineToRelative(2f)
                        quadToRelative(1.25f, 0f, 2.13f, 0.88f)
                        reflectiveQuadTo(20f, 7f)
                        verticalLineTo(9f)
                        quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                        reflectiveQuadTo(21f, 10f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(22f, 11f)
                        verticalLineToRelative(2f)
                        quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                        reflectiveQuadTo(21f, 14f)
                        reflectiveQuadToRelative(-0.71f, 0.29f)
                        reflectiveQuadTo(20f, 15f)
                        verticalLineToRelative(2f)
                        quadToRelative(0f, 1.25f, -0.88f, 2.13f)
                        reflectiveQuadTo(17f, 20f)
                        horizontalLineTo(15f)
                        close()
                        moveTo(7f, 20f)
                        quadTo(5.75f, 20f, 4.88f, 19.13f)
                        reflectiveQuadTo(4f, 17f)
                        verticalLineTo(15f)
                        quadTo(4f, 14.58f, 3.71f, 14.29f)
                        reflectiveQuadTo(3f, 14f)
                        reflectiveQuadTo(2.29f, 13.71f)
                        quadTo(2f, 13.43f, 2f, 13f)
                        verticalLineTo(11f)
                        quadTo(2f, 10.58f, 2.29f, 10.29f)
                        reflectiveQuadTo(3f, 10f)
                        reflectiveQuadTo(3.71f, 9.71f)
                        reflectiveQuadTo(4f, 9f)
                        verticalLineTo(7f)
                        quadTo(4f, 5.75f, 4.88f, 4.88f)
                        reflectiveQuadTo(7f, 4f)
                        horizontalLineTo(9f)
                        quadTo(9.43f, 4f, 9.71f, 4.29f)
                        reflectiveQuadTo(10f, 5f)
                        reflectiveQuadTo(9.71f, 5.71f)
                        reflectiveQuadTo(9f, 6f)
                        horizontalLineTo(7f)
                        quadTo(6.58f, 6f, 6.29f, 6.29f)
                        reflectiveQuadTo(6f, 7f)
                        verticalLineTo(9f)
                        quadTo(6f, 9.95f, 5.45f, 10.73f)
                        reflectiveQuadTo(4f, 11.83f)
                        verticalLineToRelative(0.35f)
                        quadToRelative(0.9f, 0.33f, 1.45f, 1.1f)
                        reflectiveQuadTo(6f, 15f)
                        verticalLineToRelative(2f)
                        quadToRelative(0f, 0.43f, 0.29f, 0.71f)
                        reflectiveQuadTo(7f, 18f)
                        horizontalLineTo(9f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(10f, 19f)
                        reflectiveQuadTo(9.71f, 19.71f)
                        reflectiveQuadTo(9f, 20f)
                        horizontalLineTo(7f)
                        close()
                    }
                }
                .build()
        return dataObject!!
    }

private var dataObject: ImageVector? = null