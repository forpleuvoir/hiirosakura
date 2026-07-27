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

fun List<TextSlice>.compose(text: String): MutableText {
    val result = Literal("")
    if (isEmpty()) return result
    for (slice in this) {
        result.append(
            Literal(text.substring(slice.range.start, slice.range.end))
                .also { it.style = slice.style.toMcStyle() }
        )
    }
    return result
}
