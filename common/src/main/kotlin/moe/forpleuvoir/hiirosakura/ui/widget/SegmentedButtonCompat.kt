package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalColorScheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * 旧版 material3 `SegmentedButton` / `SegmentedButtonDefaults` 在本项目的兼容实现。
 *
 * 像素风主题没有圆角「分段」概念（组件边框来自 `.aseprite` 九宫格），因此：
 * - [SegmentedButtonDefaults.itemShape] 只保留签名，返回直角形状（不参与绘制）；
 * - 选中态用 sokitsu 的容器色板表达 —— 选中取 `primaryContainer` / `onPrimaryContainer`，
 *   未选中走 [ButtonDefaults.colors] 的缺省槽位。
 *
 * 调用点（16 处，分布在 8 个文件）沿用旧签名，无需改动。
 */

/** 兼容 material3 的分段按钮几何缺省值。 */
object SegmentedButtonDefaults {

    /** 分段块内部默认内边距。 */
    val ContentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp)

    /** 前导图标默认尺寸。 */
    val IconSize: Dp = 16.dp

    /** 分段中第 [index] 块（共 [count] 块）的形状。像素风下无圆角槽位，返回直角。 */
    fun itemShape(index: Int, count: Int): Shape = RectangleShape
}

/**
 * 分段按钮中的一块。
 *
 * @param selected 是否选中
 * @param onClick 点击回调
 * @param shape 形状，兼容旧签名保留但不参与绘制
 * @param label 文案槽位
 * @param icon 图标槽位
 */
@Composable
fun SegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    val scheme = LocalColorScheme.current
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = if (selected) {
            ButtonDefaults.colors(
                color = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer,
            )
        } else {
            ButtonDefaults.colors()
        },
    ) {
        icon?.invoke()
        label?.invoke()
    }
}
