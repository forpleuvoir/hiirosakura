package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 测量一组标签（如 `key :`、`[n] :`）中最宽的宽度，
 * 用于让对象 / 数组条目中的 key 与 index 对齐。
 */
@Composable
internal fun rememberMaxLabelWidth(
    labels: List<String>,
    style: TextStyle,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val cacheKey = remember(labels) { labels.joinToString("\u0000") }
    return remember(cacheKey, style) {
        labels.maxOfOrNull { label ->
            val widthPx = textMeasurer.measure(AnnotatedString(label), style).size.width
            with(density) { widthPx.toDp() }
        } ?: 0.dp
    }
}
