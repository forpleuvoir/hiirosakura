package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> rememberSegmentedButtonWidth(
    items: Iterable<T>,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    contentPadding: PaddingValues = SegmentedButtonDefaults.ContentPadding,
    iconSize: Dp = SegmentedButtonDefaults.IconSize,
    iconSpacing: Dp = 8.dp,
    text: (T) -> AnnotatedString,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // 保证语言或文本发生变化后能够重新计算
    val texts = items.map(text)

    val maxTextWidth = remember(
        textMeasurer,
        texts,
        textStyle,
    ) {
        texts.maxOfOrNull { value ->
            textMeasurer.measure(
                text = value,
                style = textStyle,
                maxLines = 1,
                softWrap = false,
            ).size.width
        } ?: 0
    }

    return with(density) {
        maxTextWidth.toDp()
    } +
            contentPadding.calculateStartPadding(layoutDirection) +
            contentPadding.calculateEndPadding(layoutDirection) +
            iconSize +
            iconSpacing
}