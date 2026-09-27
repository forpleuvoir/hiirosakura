package moe.forpleuvoir.hiirosakura.ui.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

/**
 * 渲染 [ImageVector] 的图标组件。
 *
 * IbukiGourd 的 `Icon` 只接受 sokitsu 精灵，本项目的自有图标是矢量构建的，因此自行渲染：
 * compose-minecraft 已移植 `androidx.compose.ui.graphics.vector`，直接用 [rememberVectorPainter]。
 * 固有尺寸取自图标自身；[modifier] 上显式给出的尺寸在链外层、优先于固有尺寸。
 *
 * @param image 矢量图标
 * @param contentDescription 无障碍描述，为 null 时不参与语义
 * @param modifier 作用于整个图标
 * @param tint 着色，[Color.Unspecified] 表示保持图标自身颜色
 */
@Composable
fun VectorIcon(
    image: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    val colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    Box(modifier.size(image.defaultWidth, image.defaultHeight)) {
        Image(
            painter = rememberVectorPainter(image),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            colorFilter = colorFilter,
        )
    }
}
