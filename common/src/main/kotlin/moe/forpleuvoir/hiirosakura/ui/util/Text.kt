package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.text.plainText
import net.minecraft.network.chat.Component
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.hiirosakura.ui.widget.SegmentedButtonDefaults

@Composable
fun <T> rememberSegmentedButtonWidth(
    items: Iterable<T>,
    textStyle: TextStyle = SokitsuTheme.typography.button,
    contentPadding: PaddingValues = SegmentedButtonDefaults.ContentPadding,
    iconSize: Dp = SegmentedButtonDefaults.IconSize,
    iconSpacing: Dp = 8.dp,
    text: (T) -> Component,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // 保证语言或文本发生变化后能够重新计算
    val texts = items.map { text(it).plainText }

    // CMP 的文本测量只接受原版 Style；字号/字重由平台文本渲染决定，这里做宽度估算。
    val measureStyle = net.minecraft.network.chat.Style.EMPTY
    val maxTextWidth = remember(
        textMeasurer,
        texts,
    ) {
        val widths = texts.map { value ->
            textMeasurer.measure(
                text = AnnotatedString(value),
                style = measureStyle,
                maxLines = 1,
                softWrap = false,
            ).size.width
        }
        widths.maxOrNull() ?: 0
    }

    return with(density) {
        maxTextWidth.toDp()
    } +
            contentPadding.calculateStartPadding(layoutDirection) +
            contentPadding.calculateEndPadding(layoutDirection) +
            iconSize +
            iconSpacing
}