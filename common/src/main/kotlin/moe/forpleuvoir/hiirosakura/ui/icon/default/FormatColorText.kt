package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.FormatColorText: ImageVector
    get() {
        if (formatColorText != null) {
            return formatColorText!!
        }
        formatColorText =
            ImageVector.Builder(
                name = "format_color_text",
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
                        moveTo(3f, 24f)
                        quadTo(2.58f, 24f, 2.29f, 23.71f)
                        quadTo(2f, 23.43f, 2f, 23f)
                        verticalLineTo(21f)
                        quadTo(2f, 20.58f, 2.29f, 20.29f)
                        reflectiveQuadTo(3f, 20f)
                        horizontalLineTo(21f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(22f, 21f)
                        verticalLineToRelative(2f)
                        quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                        reflectiveQuadTo(21f, 24f)
                        horizontalLineTo(3f)
                        close()
                        moveTo(7.13f, 17f)
                        quadTo(6.55f, 17f, 6.21f, 16.51f)
                        reflectiveQuadTo(6.08f, 15.48f)
                        lineTo(10.48f, 3.75f)
                        quadTo(10.6f, 3.4f, 10.9f, 3.2f)
                        reflectiveQuadTo(11.55f, 3f)
                        horizontalLineToRelative(0.9f)
                        quadToRelative(0.38f, 0f, 0.66f, 0.2f)
                        reflectiveQuadToRelative(0.41f, 0.55f)
                        lineTo(17.95f, 15.5f)
                        quadToRelative(0.2f, 0.55f, -0.14f, 1.02f)
                        quadTo(17.48f, 17f, 16.9f, 17f)
                        quadToRelative(-0.35f, 0f, -0.65f, -0.2f)
                        reflectiveQuadTo(15.83f, 16.25f)
                        lineTo(14.85f, 13.4f)
                        horizontalLineTo(9.2f)
                        lineTo(8.18f, 16.27f)
                        quadTo(8.05f, 16.63f, 7.76f, 16.81f)
                        reflectiveQuadTo(7.13f, 17f)
                        close()
                        moveTo(9.9f, 11.4f)
                        horizontalLineToRelative(4.2f)
                        lineTo(12.05f, 5.6f)
                        horizontalLineToRelative(-0.1f)
                        lineTo(9.9f, 11.4f)
                        close()
                    }
                }
                .build()
        return formatColorText!!
    }

private var formatColorText: ImageVector? = null


val Icons.FormatColorTextTop: ImageVector
    get() {
        if (formatColorTextTop != null) {
            return formatColorTextTop!!
        }

        formatColorTextTop = ImageVector.Builder(
            name = "format_color_text_top",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // 原来从这里开始的字母部分
                moveTo(7.13f, 17f)
                quadTo(6.55f, 17f, 6.21f, 16.51f)
                reflectiveQuadTo(6.08f, 15.48f)
                lineTo(10.48f, 3.75f)
                quadTo(10.6f, 3.4f, 10.9f, 3.2f)
                reflectiveQuadTo(11.55f, 3f)
                horizontalLineToRelative(0.9f)
                quadToRelative(0.38f, 0f, 0.66f, 0.2f)
                reflectiveQuadToRelative(0.41f, 0.55f)
                lineTo(17.95f, 15.5f)
                quadToRelative(0.2f, 0.55f, -0.14f, 1.02f)
                quadTo(17.48f, 17f, 16.9f, 17f)
                quadToRelative(-0.35f, 0f, -0.65f, -0.2f)
                reflectiveQuadTo(15.83f, 16.25f)
                lineTo(14.85f, 13.4f)
                horizontalLineTo(9.2f)
                lineTo(8.18f, 16.27f)
                quadTo(8.05f, 16.63f, 7.76f, 16.81f)
                reflectiveQuadTo(7.13f, 17f)
                close()

                moveTo(9.9f, 11.4f)
                horizontalLineToRelative(4.2f)
                lineTo(12.05f, 5.6f)
                horizontalLineToRelative(-0.1f)
                lineTo(9.9f, 11.4f)
                close()
            }
        }.build()

        return formatColorTextTop!!
    }

private var formatColorTextTop: ImageVector? = null


@Suppress("CheckReturnValue")
val Icons.FormatColorTextShadowTop: ImageVector
    get() {
        if (formatTextShadowTop != null) {
            return formatTextShadowTop!!
        }

        formatTextShadowTop = ImageVector.Builder(
            name = "format_color_text_shadow_top",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // 向右下偏移的文字阴影
            group(
                translationX = 2.8f,
                translationY = 1.5f,
            ) {
                addFormatTextPath(fillAlpha = 0.32f)
            }

            // 正常文字，覆盖在阴影上方
            addFormatTextPath(fillAlpha = 1f)
        }.build()

        return formatTextShadowTop!!
    }

private var formatTextShadowTop: ImageVector? = null

private fun ImageVector.Builder.addFormatTextPath(
    fillAlpha: Float,
) {
    path(
        fill = SolidColor(Color.Black),
        fillAlpha = fillAlpha,
        pathFillType = PathFillType.NonZero,
    ) {
        moveTo(7.13f, 17f)
        quadTo(6.55f, 17f, 6.21f, 16.51f)
        reflectiveQuadTo(6.08f, 15.48f)
        lineTo(10.48f, 3.75f)
        quadTo(10.6f, 3.4f, 10.9f, 3.2f)
        reflectiveQuadTo(11.55f, 3f)
        horizontalLineToRelative(0.9f)
        quadToRelative(0.38f, 0f, 0.66f, 0.2f)
        reflectiveQuadToRelative(0.41f, 0.55f)
        lineTo(17.95f, 15.5f)
        quadToRelative(0.2f, 0.55f, -0.14f, 1.02f)
        quadTo(17.48f, 17f, 16.9f, 17f)
        quadToRelative(-0.35f, 0f, -0.65f, -0.2f)
        reflectiveQuadTo(15.83f, 16.25f)
        lineTo(14.85f, 13.4f)
        horizontalLineTo(9.2f)
        lineTo(8.18f, 16.27f)
        quadTo(8.05f, 16.63f, 7.76f, 16.81f)
        reflectiveQuadTo(7.13f, 17f)
        close()

        moveTo(9.9f, 11.4f)
        horizontalLineToRelative(4.2f)
        lineTo(12.05f, 5.6f)
        horizontalLineToRelative(-0.1f)
        lineTo(9.9f, 11.4f)
        close()
    }
}

val Icons.FormatColorTextBottom: ImageVector
    get() {
        if (formatColorTextBottom != null) {
            return formatColorTextBottom!!
        }

        formatColorTextBottom = ImageVector.Builder(
            name = "format_color_text_bottom",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 24f)
                quadTo(2.58f, 24f, 2.29f, 23.71f)
                quadTo(2f, 23.43f, 2f, 23f)
                verticalLineTo(21f)
                quadTo(2f, 20.58f, 2.29f, 20.29f)
                reflectiveQuadTo(3f, 20f)
                horizontalLineTo(21f)
                quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                reflectiveQuadTo(22f, 21f)
                verticalLineToRelative(2f)
                quadToRelative(0f, 0.43f, -0.29f, 0.71f)
                reflectiveQuadTo(21f, 24f)
                horizontalLineTo(3f)
                close()
            }
        }.build()

        return formatColorTextBottom!!
    }

private var formatColorTextBottom: ImageVector? = null