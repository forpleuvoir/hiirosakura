package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.compose_minecraft.platform.render.text.FontRegistry
import moe.forpleuvoir.compose_minecraft.platform.render.text.FontResolver
import moe.forpleuvoir.compose_minecraft.platform.ui.text.LocalDefaultFontSize
import moe.forpleuvoir.compose_minecraft.platform.ui.text.resolveDefaultFont
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ProvideTextStyle

/**
 * 带标题的内容容器：标题在内容上方，本组件自身**不画容器**（无描边、无底色、无内容色）。
 *
 * 框体统一由内容自己承担 —— sokitsu 的 `TextField` / `NumberField` / `Button` 等已经用
 * 九宫格精灵画了自己的背景与描边，本组件再套一层 sokitsu `Surface` 就会叠出双框；
 * 因此这里只剩「标题 + 内容」两段纵向排布，等价于一个纯布局的 [Column]。
 *
 * 标题字号默认取[像素字体原生网格换算出的最小锐利字号][LabeledFieldDefaults.labelFontSize]
 * （12px 网格 → 1sp == 1px @density 1），而不是主题 body 的 24sp。标题里未显式指定
 * `fontSize` 的文本，两条渲染路径都会吃到这个默认值：
 * - `Text(String / AnnotatedString)` 走 [moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle]（[ProvideTextStyle]）；
 * - `Text(Component)` 走平台默认字号（[LocalDefaultFontSize]）。
 *
 * @param label 标题；为 null 时只有内容
 * @param modifier 作用于整块（标题 + 内容）
 * @param contentPadding 内容内边距
 * @param labelStartPadding 标题距逻辑起始边的偏移；null 时跟随 [contentPadding] 的起始边距
 * @param labelIndent 标题内容相对标题起始边的额外缩进
 * @param content 内容
 */
@Composable
fun LabelBox(
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding,
    labelStartPadding: Dp? = null,
    labelIndent: Dp = LabeledFieldDefaults.LabelIndent,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (label != null) {
            val labelFontSize = LabeledFieldDefaults.labelFontSize
            CompositionLocalProvider(LocalDefaultFontSize provides labelFontSize) {
                ProvideTextStyle(TextStyle(fontSize = labelFontSize)) {
                    Box(
                        modifier = Modifier.padding(
                            start = labelStartPadding
                                ?: contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                        ),
                    ) {
                        Row {
                            Spacer(Modifier.width(labelIndent))
                            label()
                        }
                    }
                }
            }
            Spacer(Modifier.height(LabeledFieldDefaults.LabelSpacing))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.CenterStart,
            content = content,
        )
    }
}

/**
 * 带标题容器的缺省值。
 *
 * 替代 material3 的 `OutlinedTextFieldDefaults`：像素风下只剩尺寸量，颜色与形状走主题。
 */
object LabeledFieldDefaults {

    /** 标题与内容之间的垂直间距。 */
    val LabelSpacing: Dp = 2.dp

    /** 标题内容相对标题起始边的额外缩进。 */
    val LabelIndent: Dp = 12.dp

    /** 内容默认内边距。 */
    val contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /**
     * 标题默认字号：**生效字体的原生网格**折成的 sp。
     *
     * 网格取 [moe.forpleuvoir.compose_minecraft.platform.render.text.PlatformFont.providerEmPx]
     * （Fusion Pixel = `TextRenderConfig.pixelFontEmSp` = 12px，即字体的设计尺寸），再按平台
     * 「字号 → 像素 em」的唯一换算入口反向折算：
     * `emPx = sp × density × fontScale` ⇒ `sp = emPx / (density × fontScale)`。
     *
     * 这正是像素字体与屏幕像素 1:1 的**最小锐利字号**（density 1 时 12sp → 12px 网格）：
     * 比它更小的整数倍不存在，非整数倍会让字形落到亚像素上发糊；主题 body 的 24sp 是它的 2×。
     * 字号跟随生效字体解析，局部 `LocalDefaultFont` 换字体时标题尺寸同步变化。
     */
    val labelFontSize: TextUnit
        @Composable
        get() {
            val density = LocalDensity.current
            val font = FontRegistry[resolveDefaultFont()] ?: FontResolver.defaultFont()
            return (font.providerEmPx / (density.density * density.fontScale)).sp
        }

    /** 按边指定的内容内边距，缺省边沿用 8 / 12 dp。 */
    fun contentPadding(
        top: Dp = 8.dp,
        bottom: Dp = 8.dp,
        start: Dp = 12.dp,
        end: Dp = 12.dp,
    ): PaddingValues = PaddingValues(start = start, top = top, end = end, bottom = bottom)
}
