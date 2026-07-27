package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.nebula.common.color.Color

@Immutable
data class SliceStyleState(
    val color: StyleProperty<Color>?,
    val shadowColor: StyleProperty<Color>?,
    val bold: StyleProperty<Boolean>?,
    val italic: StyleProperty<Boolean>?,
    val underlined: StyleProperty<Boolean>?,
    val strikethrough: StyleProperty<Boolean>?,
    val obfuscated: StyleProperty<Boolean>?,
)

fun List<TextSlice>.computeSelectionStyleState(range: TextRange): SliceStyleState {
    val styles = filter { slice ->
        slice.range.start < range.end && slice.range.end > range.start
    }.map { it.style }

    return SliceStyleState(
        color = styles.map { it.color }.reduceOrMixed(),
        shadowColor = styles.map { it.shadowColor }.reduceOrMixed(),
        bold = styles.map { it.bold }.reduceOrMixed(),
        italic = styles.map { it.italic }.reduceOrMixed(),
        underlined = styles.map { it.underlined }.reduceOrMixed(),
        strikethrough = styles.map { it.strikethrough }.reduceOrMixed(),
        obfuscated = styles.map { it.obfuscated }.reduceOrMixed(),
    )
}

private fun <T> List<StyleProperty<T>>.reduceOrMixed(): StyleProperty<T>? {
    if (isEmpty()) return StyleProperty.Unset
    val first = this[0]
    for (i in 1 until size) {
        if (this[i] != first) return null
    }
    return first
}
