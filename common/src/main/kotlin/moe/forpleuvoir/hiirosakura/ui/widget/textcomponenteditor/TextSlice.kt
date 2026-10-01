package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.MutableText

@Immutable
data class TextSlice(
    val range: TextRange,
    val style: SliceStyle,
)

/**
 * 拼接切片文本。
 *
 * 切片由编辑后的效果更新、比预览晚一次重组，因此这里按 [text] 实际长度裁剪区间：
 * 越界的一端取到文本末尾，空区间不产生内容。
 */
fun List<TextSlice>.compose(text: String): MutableText {
    val result = Literal("")
    if (isEmpty() || text.isEmpty()) return result
    for (slice in this) {
        val start = slice.range.start.coerceIn(0, text.length)
        val end = slice.range.end.coerceIn(start, text.length)
        if (start == end) continue
        result.append(
            Literal(text.substring(start, end))
                .also { it.style = slice.style.toMcStyle() }
        )
    }
    return result
}
