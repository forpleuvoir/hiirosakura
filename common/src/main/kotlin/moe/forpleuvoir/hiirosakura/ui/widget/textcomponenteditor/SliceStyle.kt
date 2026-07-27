package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.runtime.Immutable
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

@Immutable
data class SliceStyle(
    val color: StyleProperty<Color> = StyleProperty.Unset,
    val shadowColor: StyleProperty<Color> = StyleProperty.Unset,
    val bold: StyleProperty<Boolean> = StyleProperty.Unset,
    val italic: StyleProperty<Boolean> = StyleProperty.Unset,
    val underlined: StyleProperty<Boolean> = StyleProperty.Unset,
    val strikethrough: StyleProperty<Boolean> = StyleProperty.Unset,
    val obfuscated: StyleProperty<Boolean> = StyleProperty.Unset,
) {

    companion object {
        val EMPTY = SliceStyle()
    }

    fun merge(other: SliceStyle): SliceStyle = SliceStyle(
        color = color.merge(other.color),
        shadowColor = shadowColor.merge(other.shadowColor),
        bold = bold.merge(other.bold),
        italic = italic.merge(other.italic),
        underlined = underlined.merge(other.underlined),
        strikethrough = strikethrough.merge(other.strikethrough),
        obfuscated = obfuscated.merge(other.obfuscated),
    )

    fun apply(patch: SliceStylePatch): SliceStyle = SliceStyle(
        color = color.apply(patch.color),
        shadowColor = shadowColor.apply(patch.shadowColor),
        bold = bold.apply(patch.bold),
        italic = italic.apply(patch.italic),
        underlined = underlined.apply(patch.underlined),
        strikethrough = strikethrough.apply(patch.strikethrough),
        obfuscated = obfuscated.apply(patch.obfuscated),
    )

    fun normalized(): SliceStyle = copy(
        bold = bold.normalized(),
        italic = italic.normalized(),
        underlined = underlined.normalized(),
        strikethrough = strikethrough.normalized(),
        obfuscated = obfuscated.normalized(),
    )
}

fun SliceStyle.toMcStyle(): Style = Style(
    when (color) {
        is StyleProperty.Set -> TextColor.fromRgb(color.value.rgb)
        else -> null
    },
    when (shadowColor) {
        is StyleProperty.Set -> shadowColor.value.argb
        else -> null
    },
    when (bold) {
        is StyleProperty.Set -> true
        StyleProperty.None -> false
        StyleProperty.Unset -> null
    },
    when (italic) {
        is StyleProperty.Set -> true
        StyleProperty.None -> false
        StyleProperty.Unset -> null
    },
    when (underlined) {
        is StyleProperty.Set -> true
        StyleProperty.None -> false
        StyleProperty.Unset -> null
    },
    when (strikethrough) {
        is StyleProperty.Set -> true
        StyleProperty.None -> false
        StyleProperty.Unset -> null
    },
    when (obfuscated) {
        is StyleProperty.Set -> true
        StyleProperty.None -> false
        StyleProperty.Unset -> null
    },
    null,
    null,
    null,
    null,
)
