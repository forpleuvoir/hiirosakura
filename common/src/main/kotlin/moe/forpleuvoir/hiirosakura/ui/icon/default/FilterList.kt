package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
 val Icons.FilterList: ImageVector
    get() {
        if (filterList != null) {
            return filterList!!
        }
        filterList =
            ImageVector.Builder(
                name = "filter_list",
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
                        moveTo(11f, 18f)
                        quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
                        quadTo(10f, 17.43f, 10f, 17f)
                        reflectiveQuadToRelative(0.29f, -0.71f)
                        reflectiveQuadTo(11f, 16f)
                        horizontalLineToRelative(2f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(14f, 17f)
                        reflectiveQuadToRelative(-0.29f, 0.71f)
                        reflectiveQuadTo(13f, 18f)
                        horizontalLineTo(11f)
                        close()
                        moveTo(7f, 13f)
                        quadTo(6.58f, 13f, 6.29f, 12.71f)
                        quadTo(6f, 12.43f, 6f, 12f)
                        reflectiveQuadTo(6.29f, 11.29f)
                        reflectiveQuadTo(7f, 11f)
                        horizontalLineTo(17f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(18f, 12f)
                        reflectiveQuadToRelative(-0.29f, 0.71f)
                        reflectiveQuadTo(17f, 13f)
                        horizontalLineTo(7f)
                        close()
                        moveTo(4f, 8f)
                        quadTo(3.58f, 8f, 3.29f, 7.71f)
                        quadTo(3f, 7.43f, 3f, 7f)
                        reflectiveQuadTo(3.29f, 6.29f)
                        reflectiveQuadTo(4f, 6f)
                        horizontalLineTo(20f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(21f, 7f)
                        reflectiveQuadTo(20.71f, 7.71f)
                        reflectiveQuadTo(20f, 8f)
                        horizontalLineTo(4f)
                        close()
                    }
                }
                .build()
        return filterList!!
    }

private var filterList: ImageVector? = null