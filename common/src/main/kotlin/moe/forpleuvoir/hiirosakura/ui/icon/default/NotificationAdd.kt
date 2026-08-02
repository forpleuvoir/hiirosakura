package moe.forpleuvoir.hiirosakura.ui.icon.defaults

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.icon.Icons

@Suppress("CheckReturnValue")
val Icons.NotificationAdd: ImageVector
    get() {
        if (notificationAdd != null) {
            return notificationAdd!!
        }
        notificationAdd =
            ImageVector.Builder(
                name = "notification_add",
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
                        moveTo(12f, 22f)
                        quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
                        reflectiveQuadTo(10f, 20f)
                        horizontalLineToRelative(4f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(12f, 22f)
                        close()
                        moveTo(5f, 19f)
                        quadTo(4.58f, 19f, 4.29f, 18.71f)
                        quadTo(4f, 18.43f, 4f, 18f)
                        reflectiveQuadTo(4.29f, 17.29f)
                        reflectiveQuadTo(5f, 17f)
                        horizontalLineTo(6f)
                        verticalLineTo(10f)
                        quadTo(6f, 7.93f, 7.25f, 6.31f)
                        reflectiveQuadTo(10.5f, 4.2f)
                        verticalLineTo(3.5f)
                        quadToRelative(0f, -0.63f, 0.44f, -1.06f)
                        reflectiveQuadTo(12f, 2f)
                        reflectiveQuadToRelative(1.06f, 0.44f)
                        reflectiveQuadTo(13.5f, 3.5f)
                        verticalLineTo(4.2f)
                        quadToRelative(0.25f, 0.05f, 0.48f, 0.11f)
                        reflectiveQuadTo(14.4f, 4.5f)
                        quadToRelative(0.35f, 0.2f, 0.53f, 0.56f)
                        reflectiveQuadToRelative(0f, 0.74f)
                        reflectiveQuadTo(14.39f, 6.34f)
                        reflectiveQuadTo(13.65f, 6.35f)
                        quadTo(13.1f, 6.13f, 12.75f, 6.06f)
                        reflectiveQuadTo(12f, 6f)
                        quadTo(10.35f, 6f, 9.18f, 7.18f)
                        reflectiveQuadTo(8f, 10f)
                        verticalLineToRelative(7f)
                        horizontalLineToRelative(8f)
                        verticalLineTo(15f)
                        quadToRelative(0f, -0.5f, 0.31f, -0.75f)
                        reflectiveQuadTo(17f, 14f)
                        reflectiveQuadToRelative(0.69f, 0.25f)
                        reflectiveQuadTo(18f, 15f)
                        verticalLineToRelative(2f)
                        horizontalLineToRelative(1f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(20f, 18f)
                        reflectiveQuadToRelative(-0.29f, 0.71f)
                        reflectiveQuadTo(19f, 19f)
                        horizontalLineTo(5f)
                        close()
                        moveTo(18f, 10f)
                        horizontalLineTo(16f)
                        quadTo(15.58f, 10f, 15.29f, 9.71f)
                        reflectiveQuadTo(15f, 9f)
                        quadTo(15f, 8.57f, 15.29f, 8.29f)
                        reflectiveQuadTo(16f, 8f)
                        horizontalLineToRelative(2f)
                        verticalLineTo(6f)
                        quadTo(18f, 5.57f, 18.29f, 5.29f)
                        reflectiveQuadTo(19f, 5f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(20f, 6f)
                        verticalLineTo(8f)
                        horizontalLineToRelative(2f)
                        quadToRelative(0.43f, 0f, 0.71f, 0.29f)
                        reflectiveQuadTo(23f, 9f)
                        quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                        reflectiveQuadTo(22f, 10f)
                        horizontalLineTo(20f)
                        verticalLineToRelative(2f)
                        quadToRelative(0f, 0.42f, -0.29f, 0.71f)
                        reflectiveQuadTo(19f, 13f)
                        reflectiveQuadTo(18.29f, 12.71f)
                        quadTo(18f, 12.43f, 18f, 12f)
                        verticalLineTo(10f)
                        close()
                        moveToRelative(-6f, 1.5f)
                        close()
                    }
                }
                .build()
        return notificationAdd!!
    }

private var notificationAdd: ImageVector? = null