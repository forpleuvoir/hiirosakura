package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

class RichTextOutputTransformation(
    private val state: RichTextEditorState,
) : OutputTransformation {

    override fun TextFieldBuffer.transformOutput() {
        for (slice in state.slices) {
            if (slice.range.start >= length || slice.range.end > length) continue
            if (slice.range.length <= 0) continue
            addStyle(
                spanStyle = slice.style.toSpanStyle(),
                start = slice.range.start.coerceIn(0, length),
                end = slice.range.end.coerceIn(0, length),
            )
        }
    }
}

fun SliceStyle.toSpanStyle(): SpanStyle = SpanStyle(
    color = when (val c = color) {
        is StyleProperty.Set -> Color(c.value.argb)
        else -> Color.Unspecified
    },
    fontWeight = when (val b = bold) {
        is StyleProperty.Set -> if (b.value) FontWeight.Bold else null
        else -> null
    },
    fontStyle = when (val i = italic) {
        is StyleProperty.Set -> if (i.value) FontStyle.Italic else null
        else -> null
    },
    textDecoration = buildTextDecoration(this),
    shadow = when (val s = shadowColor) {
        is StyleProperty.Set -> androidx.compose.ui.graphics.Shadow(
            color = Color(s.value.argb),
            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
            blurRadius = 1f,
        )
        else -> null
    },
)

private fun buildTextDecoration(style: SliceStyle): TextDecoration? {
    val hasUnderline = isStyleSet(style.underlined)
    val hasStrikethrough = isStyleSet(style.strikethrough)
    if (hasUnderline && hasStrikethrough) return TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
    if (hasUnderline) return TextDecoration.Underline
    if (hasStrikethrough) return TextDecoration.LineThrough
    if (style.underlined is StyleProperty.None || style.strikethrough is StyleProperty.None) return TextDecoration.None
    return null
}

private fun isStyleSet(property: StyleProperty<Boolean>): Boolean =
    property is StyleProperty.Set && property.value

fun computeTextDiff(oldText: String, newText: String): Pair<TextRange, String> {
    var prefixLen = 0
    while (prefixLen < oldText.length && prefixLen < newText.length &&
        oldText[prefixLen] == newText[prefixLen]
    ) {
        prefixLen++
    }
    var suffixLen = 0
    while (suffixLen < oldText.length - prefixLen && suffixLen < newText.length - prefixLen &&
        oldText[oldText.length - 1 - suffixLen] == newText[newText.length - 1 - suffixLen]
    ) {
        suffixLen++
    }
    val replacedStart = prefixLen
    val replacedEnd = oldText.length - suffixLen
    val replacement = newText.substring(prefixLen, newText.length - suffixLen)
    return TextRange(replacedStart, replacedEnd.coerceAtLeast(replacedStart)) to replacement
}

@Composable
fun Modifier.richTextEditor(state: RichTextEditorState): Modifier {
    var previousText by remember { mutableStateOf(state.textState.text.toString()) }

    LaunchedEffect(state.textState.text) {
        val newText = state.textState.text.toString()
        if (newText != previousText) {
            val (range, replacement) = computeTextDiff(previousText, newText)
            state.replace(previousText, range, replacement)
            previousText = newText
        }
    }

    LaunchedEffect(state.textState.selection) {
        state.syncDefaultStyleFromCursor(state.textState.selection.end)
    }

    return this.onPreviewKeyEvent { event ->
        event.isCtrlPressed && (event.key == Key.Z || event.key == Key.Y)
    }
}
