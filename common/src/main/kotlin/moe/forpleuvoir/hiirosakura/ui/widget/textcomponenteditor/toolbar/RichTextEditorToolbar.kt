package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.*
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.hiirosakura.ui.icon.HSIcons
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatBold
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatClear
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatItalic
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatStrikethrough
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatUnderlined
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Help
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.QuestionMark
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatColorTextTop
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatColorTextShadowTop
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FormatColorTextBottom

@Composable
fun RichTextEditorToolbar(
    state: RichTextEditorState,
    modifier: Modifier = Modifier,
) {
    val selection = state.textState.selection
    val hasSelection = selection.length > 0 && selection.start < selection.end

    val selectionStyleState: SliceStyleState? = remember(selection, state.slices) {
        if (hasSelection) state.slices.computeSelectionStyleState(selection) else null
    }


    val effectiveBold = selectionStyleState?.bold ?: state.defaultStyle.bold
    val effectiveItalic = selectionStyleState?.italic ?: state.defaultStyle.italic
    val effectiveUnderlined = selectionStyleState?.underlined ?: state.defaultStyle.underlined
    val effectiveStrikethrough = selectionStyleState?.strikethrough ?: state.defaultStyle.strikethrough
    val effectiveObfuscated = selectionStyleState?.obfuscated ?: state.defaultStyle.obfuscated
    val effectiveColor = selectionStyleState?.color ?: state.defaultStyle.color
    val effectiveShadowColor = selectionStyleState?.shadowColor ?: state.defaultStyle.shadowColor

    var pendingTextColor by remember { mutableStateOf(Colors.WHITE) }

    var pendingShadowColor by remember { mutableStateOf(NebulaColor.fromRGB(0x3F003F)) }


    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FormatButton(
                state = if (selectionStyleState == null || selectionStyleState.bold != null) effectiveBold else null,
                onLeftClick = { applyToggle(state, PropertyToggle.Bold) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(bold = PropertyPatch.Replace(StyleProperty.Unset))) },
                tip = { Text(component = HSLang.TextEditor.bold) },
                label = { VectorIcon(HSIcons.FormatBold) },
            )

            FormatButton(
                state = if (selectionStyleState == null || selectionStyleState.italic != null) effectiveItalic else null,
                onLeftClick = { applyToggle(state, PropertyToggle.Italic) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(italic = PropertyPatch.Replace(StyleProperty.Unset))) },
                tip = { Text(component = HSLang.TextEditor.italic) },
                label = { VectorIcon(HSIcons.FormatItalic) },
            )

            FormatButton(
                state = if (selectionStyleState == null || selectionStyleState.underlined != null) effectiveUnderlined else null,
                onLeftClick = { applyToggle(state, PropertyToggle.Underlined) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(underlined = PropertyPatch.Replace(StyleProperty.Unset))) },
                tip = { Text(component = HSLang.TextEditor.underlined) },
                label = { VectorIcon(HSIcons.FormatUnderlined) },
            )

            FormatButton(
                state = if (selectionStyleState == null || selectionStyleState.strikethrough != null) effectiveStrikethrough else null,
                onLeftClick = { applyToggle(state, PropertyToggle.Strikethrough) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(strikethrough = PropertyPatch.Replace(StyleProperty.Unset))) },
                tip = { Text(component = HSLang.TextEditor.strikethrough) },
                label = { VectorIcon(HSIcons.FormatStrikethrough) },
            )

            FormatButton(
                state = if (selectionStyleState == null || selectionStyleState.obfuscated != null) effectiveObfuscated else null,
                onLeftClick = { applyToggle(state, PropertyToggle.Obfuscated) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(obfuscated = PropertyPatch.Replace(StyleProperty.Unset))) },
                tip = { Text(component = HSLang.TextEditor.obfuscated) },
                label = { VectorIcon(HSIcons.QuestionMark) },
            )

            ColorStyleControl(
                currentState = if (selectionStyleState == null || selectionStyleState.color != null) effectiveColor else null,
                pendingColor = pendingTextColor,
                onPendingColorChange = { pendingTextColor = it },
                onLeftClick = { applyStyleAction(state, SliceStylePatch(color = PropertyPatch.Replace(StyleProperty.Set(pendingTextColor)))) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(color = PropertyPatch.Replace(StyleProperty.Unset))) },
                enabledAlpha = false,
                tip = { Text(component = HSLang.TextEditor.textColor) },
                label = { current, pending ->
                    Box(modifier = Modifier.size(24.dp)) {
                        VectorIcon(HSIcons.FormatColorTextTop, tint = current)
                        VectorIcon(HSIcons.FormatColorTextBottom, tint = pending, modifier = Modifier.matchParentSize())
                    }
                },
            )

            ColorStyleControl(
                currentState = if (selectionStyleState == null || selectionStyleState.shadowColor != null) effectiveShadowColor else null,
                pendingColor = pendingShadowColor,
                onPendingColorChange = { pendingShadowColor = it },
                onLeftClick = { applyStyleAction(state, SliceStylePatch(shadowColor = PropertyPatch.Replace(StyleProperty.Set(pendingShadowColor)))) },
                onRightClick = { applyStyleAction(state, SliceStylePatch(shadowColor = PropertyPatch.Replace(StyleProperty.Unset))) },
                enabledAlpha = true,
                tip = { Text(component = HSLang.TextEditor.shadowColor) },
                label = { current, pending ->
                    Box(modifier = Modifier.size(24.dp)) {
                        VectorIcon(HSIcons.FormatColorTextShadowTop, tint = current, modifier = Modifier.matchParentSize())
                        VectorIcon(HSIcons.FormatColorTextBottom, tint = pending, modifier = Modifier.matchParentSize())
                    }
                },
            )

            FormatButton(
                state = StyleProperty.Unset,
                onLeftClick = { applyStyleAction(state, SliceStylePatch.Clear) },
                onRightClick = { applyStyleAction(state, SliceStylePatch.Unset) },
                tip = { Text(component = HSLang.TextEditor.clearStyle) },
                label = { VectorIcon(HSIcons.FormatClear) },
            )
        }
IconButton({}, modifier = Modifier.tooltip {
            Text(component = HSLang.TextEditor.helpTip)
        }) {
            VectorIcon(HSIcons.Help)
        }
    }
}

private enum class PropertyToggle { Bold, Italic, Underlined, Strikethrough, Obfuscated }

private fun applyToggle(state: RichTextEditorState, toggle: PropertyToggle) {
    val sel = state.textState.selection
    val hasSel = sel.length > 0 && sel.start < sel.end

    val current: StyleProperty<Boolean>? = if (hasSel) {
        val s = state.slices.computeSelectionStyleState(sel)
        when (toggle) {
            PropertyToggle.Bold          -> s.bold
            PropertyToggle.Italic        -> s.italic
            PropertyToggle.Underlined    -> s.underlined
            PropertyToggle.Strikethrough -> s.strikethrough
            PropertyToggle.Obfuscated    -> s.obfuscated
        }
    } else {
        when (toggle) {
            PropertyToggle.Bold          -> state.defaultStyle.bold
            PropertyToggle.Italic        -> state.defaultStyle.italic
            PropertyToggle.Underlined    -> state.defaultStyle.underlined
            PropertyToggle.Strikethrough -> state.defaultStyle.strikethrough
            PropertyToggle.Obfuscated    -> state.defaultStyle.obfuscated
        }
    }


    val target = when {
        current == null                               -> StyleProperty.Set(true)
        current is StyleProperty.Set && current.value -> StyleProperty.None
        else                                          -> StyleProperty.Set(true)
    }


    val patch = when (toggle) {
        PropertyToggle.Bold          -> SliceStylePatch(bold = PropertyPatch.Replace(target))
        PropertyToggle.Italic        -> SliceStylePatch(italic = PropertyPatch.Replace(target))
        PropertyToggle.Underlined    -> SliceStylePatch(underlined = PropertyPatch.Replace(target))
        PropertyToggle.Strikethrough -> SliceStylePatch(strikethrough = PropertyPatch.Replace(target))
        PropertyToggle.Obfuscated    -> SliceStylePatch(obfuscated = PropertyPatch.Replace(target))
    }
    applyStyleAction(state, patch)
}

private fun applyStyleAction(
    state: RichTextEditorState,
    patch: SliceStylePatch,
) {
    val sel = state.textState.selection
    if (sel.length > 0 && sel.start < sel.end) {
        state.applyStyle(sel, patch)
    } else {
        state.updateDefaultStyle(patch)
    }
}
