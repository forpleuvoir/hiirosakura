package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import androidx.compose.foundation.layout.calculateStartPadding

/**
 * 带标题的输入区容器：标题在框体上方，框体由 sokitsu 的 [Surface] 提供。
 *
 * 像素风主题没有描边圆角与浮动标签（框线来自 `.aseprite` 九宫格），因此不保留 material3 时代的
 * `shape` / `colors` / `isError` / `interactionSource` 参数 —— 配色由主题槽位决定。
 *
 * @param label 标题；为 null 时只有框体
 * @param modifier 作用于整块（标题 + 框体）
 * @param contentPadding 框内边距
 * @param labelStartPadding 标题距逻辑起始边的偏移；null 时跟随 [contentPadding] 的起始边距
 * @param content 框内内容
 */
@Composable
fun LabelBox(
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding,
    labelStartPadding: Dp? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Box(
                modifier = Modifier.padding(
                    start = labelStartPadding
                        ?: contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                ),
            ) {
                label()
            }
            Spacer(Modifier.height(LabeledFieldDefaults.LabelSpacing))
        }
        Surface(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.CenterStart,
                propagateMinConstraints = true,
                content = content,
            )
        }
    }
}

/**
 * 带标题输入区的几何缺省值。
 *
 * 替代 material3 的 `OutlinedTextFieldDefaults`：像素风下只剩尺寸量，颜色与形状走主题。
 */
object LabeledFieldDefaults {

    /** 标题与框体之间的垂直间距。 */
    val LabelSpacing: Dp = 2.dp

    /** 框内默认内边距。 */
    val contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /** 按边指定的框内边距，缺省边沿用 8 / 12 dp。 */
    fun contentPadding(
        top: Dp = 8.dp,
        bottom: Dp = 8.dp,
        start: Dp = 12.dp,
        end: Dp = 12.dp,
    ): PaddingValues = PaddingValues(start = start, top = top, end = end, bottom = bottom)
}
