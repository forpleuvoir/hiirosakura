package moe.forpleuvoir.hiirosakura.ui.widget.matcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JexlSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightDefaults
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateText

import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.network.chat.Component
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.hiirosakura.ui.icon.HSIcons
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.PlayArrow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.input.TextFieldLineLimits
import moe.forpleuvoir.compose_minecraft.platform.ui.text.MinecraftFonts
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import net.minecraft.network.chat.Style

internal val LocalMatchEntryRowHeight = staticCompositionLocalOf {
    80.dp
}

internal val LocalMatchEntryRowPadding = staticCompositionLocalOf {
    PaddingValues(12.dp)
}

internal val LocalMatcherDialogContentSize = staticCompositionLocalOf {
    DpSize(900.dp, 620.dp)
}

internal val LocalMatchEntryInfoHeight = staticCompositionLocalOf {
    32.dp
}

@Composable
internal fun <T : MatchEntry<*>> MatchEntryRow(
    title: Component,
    entry: T,
    entryCopyWithMode: (T, MatchEntry.MatchMode) -> T,
    onChange: (T) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    moveHandler: @Composable (() -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .hoverable(interactionSource)
            .hoverHighlight(interactionSource)
            .clickable { onClick?.invoke() }
            .fillMaxWidth()
            .height(LocalMatchEntryRowHeight.current)
            .padding(LocalMatchEntryRowPadding.current),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            moveHandler?.let {
                it()
                Spacer(Modifier.width(8.dp))
            }

            Text(title)
            Spacer(Modifier.width(16.dp))
            MatchEntryModeSelector(entry.mode) { onChange(entryCopyWithMode(entry, it)) }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f, fill = false).padding(start = 8.dp)) {
                content()
            }
            RemoveConfirmButton(title.plainText, onRemove)
        }
    }

}

@Composable
internal fun BasicMatchEntryEditor(
    mode: MatchEntry.MatchMode,
    onModeChange: (MatchEntry.MatchMode) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        MatchEntryModeSelector(mode, onModeChange)
        Spacer(modifier = Modifier.height(16.dp))

        content()
    }
}

@Composable
internal fun MatchEntryModeDisplayer(
    mode: MatchEntry.MatchMode,
    modifier: Modifier = Modifier.width(4.dp).padding(vertical = 4.dp).fillMaxHeight()
) {
    Spacer(
        modifier.background(
            color = if (mode.asBoolean) Colors.LIME_MINT_GREEN.toComposeColor()
            else Colors.ORANGERED.toComposeColor()
        ).border(width = 0.5.dp, color = Color.White, RectangleShape)
    )
}

@Composable
internal fun MatchEntryModeSelector(
    mode: MatchEntry.MatchMode,
    onModeChange: (MatchEntry.MatchMode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(RadioButtonDefaults.spacing)) {
        RadioButton(
            selected = mode == MatchEntry.MatchMode.Include,
            onSelect = { onModeChange(MatchEntry.MatchMode.Include) },
            index = 0,
            count = 2,
            modifier = Modifier.tooltip {
                Text(MatchEntry.MatchMode.Include.translateComment)
            }
        ) {
            Text(MatchEntry.MatchMode.Include.translateText)
        }
        RadioButton(
            selected = mode == MatchEntry.MatchMode.Exclude,
            onSelect = { onModeChange(MatchEntry.MatchMode.Exclude) },
            index = 1,
            count = 2,
            modifier = Modifier.tooltip {
                Text(MatchEntry.MatchMode.Exclude.translateComment)
            }
        ) {
            Text(MatchEntry.MatchMode.Exclude.translateText)
        }
    }
}

@Composable
internal fun CompositeMatcherModeSelector(
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
) {
    val items = CompositeMatcher.MatchMode.entries
    Row(
        horizontalArrangement = Arrangement.spacedBy(RadioButtonDefaults.spacing),
    ) {
        items.forEachIndexed { index, item ->
            RadioButton(
                selected = mode == item,
                index = index,
                count = items.size,
                onSelect = { onModeChange(item) },
                modifier = Modifier
                    .tooltip {
                        Text(item.translateComment)
                    },
                colors = ButtonDefaults.colors(),
            ) {
                Text(item.translateText, maxLines = 1)
            }
        }
    }
}


//region ToolBar

@Composable
internal fun TestButton(
    tip: String,
    successMsg: String,
    failedMsg: String,
    test: () -> Boolean
) {
    IconButton({
        if (test()) {
            showSuccessToast(successMsg)
        } else {
            showErrorToast(failedMsg)
        }
    }, modifier = Modifier.tooltip {
        Text(tip)
    }) {
        Icon(Icons.ArrowRight)
    }
}

//endregion

@Composable
internal fun BasicScriptEditor(
    script: String,
    onValueChange: (String) -> Unit
) {
    val state = rememberTextFieldStateBinding(script, onValueChange)
    val transformation = rememberSyntaxHighlightTransformation(
        language = JexlSyntaxLanguage,
        theme = SyntaxHighlightDefaults.theme(),
        text = state.text.toString(),
    )
    Row {
        val scrollState = rememberScrollState()
        TextField(
            state = state,
            lineLimits = TextFieldLineLimits.Default,
            textStyle = Style.EMPTY.withFont(MinecraftFonts.FusionPixelMono),
//            outputTransformation = transformation,
            modifier = Modifier.fillMaxSize().codeEditorShortcuts(state),
        )

        VerticalFlatScroller(
            adapter = rememberScrollerAdapter(scrollState)
        )
    }
}

@Composable
fun rememberTextFieldStateBinding(
    value: String,
    onValueChange: (String) -> Unit
): TextFieldState {
    val state = rememberTextFieldState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.edit {
                replace(0, length, value)
            }
        }
    }
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .collect { text ->
                if (text != value) {
                    onValueChange(text)
                }
            }
    }
    return state
}