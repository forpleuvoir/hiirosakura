package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.nebula.common.color.Color

sealed interface PropertyPatch<out T> {
    data object Keep : PropertyPatch<Nothing>
    data class Replace<out T>(val value: StyleProperty<T>) : PropertyPatch<T>
}

fun <T> StyleProperty<T>.apply(patch: PropertyPatch<T>): StyleProperty<T> = when (patch) {
    PropertyPatch.Keep       -> this
    is PropertyPatch.Replace -> patch.value
}

data class SliceStylePatch(
    val color: PropertyPatch<Color> = PropertyPatch.Keep,
    val shadowColor: PropertyPatch<Color> = PropertyPatch.Keep,
    val bold: PropertyPatch<Boolean> = PropertyPatch.Keep,
    val italic: PropertyPatch<Boolean> = PropertyPatch.Keep,
    val underlined: PropertyPatch<Boolean> = PropertyPatch.Keep,
    val strikethrough: PropertyPatch<Boolean> = PropertyPatch.Keep,
    val obfuscated: PropertyPatch<Boolean> = PropertyPatch.Keep,
) {
    companion object {
        val Clear = SliceStylePatch(
            PropertyPatch.Replace<Color>(StyleProperty.None),
            PropertyPatch.Replace<Color>(StyleProperty.None),
            PropertyPatch.Replace<Boolean>(StyleProperty.None),
            PropertyPatch.Replace<Boolean>(StyleProperty.None),
            PropertyPatch.Replace<Boolean>(StyleProperty.None),
            PropertyPatch.Replace<Boolean>(StyleProperty.None),
            PropertyPatch.Replace<Boolean>(StyleProperty.None)
        )
        val Unset = SliceStylePatch(
            PropertyPatch.Replace<Color>(StyleProperty.Unset),
            PropertyPatch.Replace<Color>(StyleProperty.Unset),
            PropertyPatch.Replace<Boolean>(StyleProperty.Unset),
            PropertyPatch.Replace<Boolean>(StyleProperty.Unset),
            PropertyPatch.Replace<Boolean>(StyleProperty.Unset),
            PropertyPatch.Replace<Boolean>(StyleProperty.Unset),
            PropertyPatch.Replace<Boolean>(StyleProperty.Unset)
        )
    }
}

fun SliceStylePatch.normalized(): SliceStylePatch = SliceStylePatch(
    color = color,
    shadowColor = shadowColor,
    bold = bold.normalized(),
    italic = italic.normalized(),
    underlined = underlined.normalized(),
    strikethrough = strikethrough.normalized(),
    obfuscated = obfuscated.normalized(),
)

private fun PropertyPatch<Boolean>.normalized(): PropertyPatch<Boolean> = when (this) {
    is PropertyPatch.Replace -> {
        val normalized = value.normalized()
        if (normalized === value) this else PropertyPatch.Replace(normalized)
    }

    PropertyPatch.Keep       -> this
}

data class StyleOverlay(
    val range: TextRange,
    val patch: SliceStylePatch,
)

fun StyleProperty<Boolean>.isTrue(): Boolean = this is StyleProperty.Set && value
