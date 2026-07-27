package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.contents.PlainTextContents

@Stable
class RichTextEditorState(
    initialText: String = "",
    initialDefaultStyle: SliceStyle = SliceStyle.EMPTY,
) {

    val textState: TextFieldState = TextFieldState().also { state ->
        if (initialText.isNotEmpty()) {
            state.edit {
                replace(0, 0, initialText)
            }
        }
    }

    var defaultStyle: SliceStyle by mutableStateOf(initialDefaultStyle)
        private set

    @Suppress("PropertyName")
    internal var _slices: List<TextSlice> by mutableStateOf(
        if (initialText.isNotEmpty())
            listOf(TextSlice(TextRange(0, initialText.length), SliceStyle.EMPTY))
        else
            emptyList(),
    )
    val slices: List<TextSlice> get() = _slices

    private var defaultStyleFromUser: Boolean = false

    @Suppress("PropertyName")
    internal var _editJustHappened: Boolean by mutableStateOf(false)

    val outputTransformation: RichTextOutputTransformation by lazy {
        RichTextOutputTransformation(this)
    }

    val mcText: MutableText by derivedStateOf {
        val text = textState.text.toString()
        if (text.isEmpty()) Literal("")
        else _slices.compose(text)
    }

    fun replace(oldText: String, range: TextRange, replacement: String) {
        val oldLength = oldText.length
        val clampedRange = normalizeRange(range, oldLength)
        if (clampedRange.length == 0 && replacement.isEmpty()) return

        val insertStyle = resolveInsertStyle(clampedRange.start, oldLength)
        val newLength = oldLength - clampedRange.length + replacement.length

        val adjustedSlices = applyTextReplace(_slices, clampedRange, replacement, insertStyle)
        _slices = flatten(newLength, adjustedSlices, emptyList())

        _editJustHappened = true

        validateSlices(newLength, _slices)
    }

    fun applyStyle(range: TextRange, patch: SliceStylePatch) {
        val length = currentTextLength()
        val clampedRange = normalizeRange(range, length)
        if (clampedRange.length == 0) return

        val overlay = StyleOverlay(clampedRange, patch.normalized())
        _slices = flatten(length, _slices, listOf(overlay))

        validateSlices(length, _slices)
    }

    fun clearFormatting(range: TextRange) {
        applyStyle(range, ALL_UNSET_PATCH)
    }

    fun updateDefaultStyle(patch: SliceStylePatch) {
        defaultStyle = defaultStyle.apply(patch.normalized())
        defaultStyleFromUser = true
    }

    fun syncDefaultStyleFromCursor(cursor: Int) {
        if (_editJustHappened) {
            _editJustHappened = false
            return
        }
        val length = currentTextLength()
        if (length == 0) return
        defaultStyleFromUser = false
        defaultStyle = styleAt(cursor.coerceIn(0, length))
    }

    private fun currentTextLength(): Int =
        if (_slices.isEmpty()) 0 else _slices.last().range.end

    private fun resolveInsertStyle(position: Int, textLength: Int): SliceStyle {
        if (textLength == 0) return defaultStyle
        if (defaultStyleFromUser) return defaultStyle
        val effective = if (position <= 0) 0 else position - 1
        return styleAt(effective)
    }

    private fun styleAt(position: Int): SliceStyle {
        for (slice in _slices) {
            if (position >= slice.range.start && position < slice.range.end) {
                return slice.style
            }
        }
        return SliceStyle.EMPTY
    }

    companion object {

        fun fromMcText(component: Component): RichTextEditorState {
            val segments = collectMcSegments(component)
            val fullText = segments.joinToString("") { it.first }

            val state = RichTextEditorState()

            val initialSlices = mutableListOf<TextSlice>()
            var offset = 0
            for ((text, style) in segments) {
                val len = text.length
                if (len > 0) {
                    initialSlices.add(TextSlice(TextRange(offset, offset + len), style.toSliceStyle()))
                    offset += len
                }
            }

            if (fullText.isNotEmpty()) {
                state.textState.edit {
                    replace(0, 0, fullText)
                }
                state._slices = flatten(fullText.length, initialSlices, emptyList())
            }

            return state
        }

        private fun collectMcSegments(component: Component): List<Pair<String, Style>> {
            val result = mutableListOf<Pair<String, Style>>()
            collectMcSegmentsRecursive(component, result)
            return result
        }

        private fun collectMcSegmentsRecursive(
            component: Component,
            result: MutableList<Pair<String, Style>>,
        ) {
            val text = when (val c = component.contents) {
                is PlainTextContents -> c.text()
                else -> component.string.take(32767)
            }
            if (text.isNotEmpty()) {
                result.add(text to component.style)
            }
            for (sibling in component.siblings) {
                collectMcSegmentsRecursive(sibling, result)
            }
        }

        private fun Style.toSliceStyle(): SliceStyle = SliceStyle(
            color = color?.let { StyleProperty.Set(NebulaColor.fromRGB(it.value)) }
                ?: StyleProperty.Unset,
            shadowColor = shadowColor?.let { StyleProperty.Set(NebulaColor.fromARGB(it)) }
                ?: StyleProperty.Unset,
            bold = bold.toStyleProperty(),
            italic = italic.toStyleProperty(),
            underlined = underlined.toStyleProperty(),
            strikethrough = strikethrough.toStyleProperty(),
            obfuscated = obfuscated.toStyleProperty(),
        )

        private fun Boolean?.toStyleProperty(): StyleProperty<Boolean> = when (this) {
            true -> StyleProperty.Set(true)
            false -> StyleProperty.None
            null -> StyleProperty.Unset
        }

        private val ALL_UNSET_PATCH = SliceStylePatch(
            color = PropertyPatch.Replace(StyleProperty.Unset),
            shadowColor = PropertyPatch.Replace(StyleProperty.Unset),
            bold = PropertyPatch.Replace(StyleProperty.Unset),
            italic = PropertyPatch.Replace(StyleProperty.Unset),
            underlined = PropertyPatch.Replace(StyleProperty.Unset),
            strikethrough = PropertyPatch.Replace(StyleProperty.Unset),
            obfuscated = PropertyPatch.Replace(StyleProperty.Unset),
        )

        private fun normalizeRange(range: TextRange, textLength: Int): TextRange {
            val a = range.start.coerceIn(0, textLength)
            val b = range.end.coerceIn(0, textLength)
            return if (a <= b) TextRange(a, b) else TextRange(b, a)
        }

        internal fun applyTextReplace(
            slices: List<TextSlice>,
            replaceRange: TextRange,
            replacement: String,
            insertStyle: SliceStyle,
        ): List<TextSlice> {
            val shift = replacement.length - replaceRange.length
            val adjusted = mutableListOf<TextSlice>()

            for (slice in slices) {
                val s = slice.range.start
                val e = slice.range.end

                when {
                    e <= replaceRange.start -> adjusted.add(slice)
                    s >= replaceRange.end -> adjusted.add(
                        TextSlice(TextRange(s + shift, e + shift), slice.style)
                    )
                    s >= replaceRange.start && e <= replaceRange.end -> { /* removed */ }
                    s < replaceRange.start && e > replaceRange.end -> {
                        adjusted.add(TextSlice(TextRange(s, replaceRange.start), slice.style))
                        adjusted.add(TextSlice(TextRange(replaceRange.end + shift, e + shift), slice.style))
                    }
                    s < replaceRange.start -> {
                        adjusted.add(TextSlice(TextRange(s, replaceRange.start), slice.style))
                    }
                    else -> {
                        adjusted.add(TextSlice(TextRange(replaceRange.end + shift, e + shift), slice.style))
                    }
                }
            }

            if (replacement.isNotEmpty()) {
                adjusted.add(
                    TextSlice(
                        TextRange(replaceRange.start, replaceRange.start + replacement.length),
                        insertStyle,
                    ),
                )
            }

            return adjusted
        }
    }
}
