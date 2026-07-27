package moe.forpleuvoir.hiirosakura.ui.widget.matcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.icon.default.PlayArrow
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JexlSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JsonSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightDefaults
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ListConfigWrapperDefaults.MoveColumn
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.preset.toAnnotatedString
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.network.chat.Component

internal val LocalMatchEntryRowHeight = staticCompositionLocalOf {
    72.dp
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

@OptIn(ExperimentalFoundationApi::class)
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
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        animationSpec = tween(200),
        label = "MatchEntryRow"
    )
    Row(
        modifier = modifier
            .hoverable(interactionSource)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .onClick { onClick?.invoke() }
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

//region Adder
fun interface EntryEditor<T : MatchEntry<*>> {
    @Composable
    fun invoke(
        addAction: (T) -> Unit,
        onDismiss: () -> Unit
    )
}

data class AddMenuOption<T : MatchEntry<*>>(
    val title: Component,
    val defaultValue: () -> T,
    val editor: EntryEditor<T>
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : MatchEntry<*>> FloatingEntryAddButton(
    modifier: Modifier = Modifier,
    scrollState: LazyListState? = null,
    addMenuOptions: List<AddMenuOption<T>>,
    addAction: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var activeEditor: EntryEditor<T>? by remember { mutableStateOf(null) }

    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier
            .then(
                if (scrollState != null && !expanded)
                    Modifier.fabVisibilityAnimation(rememberFabVisibilityByScroll(scrollState))
                else Modifier
            ),
        button = {
            FloatingActionButton(
                modifier = Modifier.size(50.dp),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { expanded = !expanded },
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 45f else 0f,
                    animationSpec = tween(150)
                )
                Icon(
                    imageVector = Icons.Add,
                    contentDescription = "Add",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) {
        addMenuOptions.forEach { option ->
            FloatingActionButtonMenuItem(
                onClick = {
                    if (isQuickAction) {
                        addAction(option.defaultValue())
                    } else {
                        activeEditor = option.editor
                    }
                    expanded = false
                },
                text = { Text(option.title) },
                icon = {}
            )
        }
    }

    activeEditor?.invoke(
        addAction,
        onDismiss = { activeEditor = null }
    )
}
//endregion

@Composable
internal fun MatchEntryModeDisplayer(
    mode: MatchEntry.MatchMode,
    modifier: Modifier = Modifier.width(4.dp).padding(vertical = 4.dp).fillMaxHeight()
) {
    Spacer(
        modifier.background(
            color = if (mode.asBoolean) Colors.LIME_MINT_GREEN.toComposeColor
            else Colors.ORANGERED.toComposeColor,
            shape = MaterialTheme.shapes.small
        ).border(width = 0.5.dp, color = Color.White, MaterialTheme.shapes.small)
    )
}

@Composable
internal fun MatchEntryModeSelector(
    mode: MatchEntry.MatchMode,
    onModeChange: (MatchEntry.MatchMode) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        TipBox({ Text(MatchEntry.MatchMode.Include.translateComment) }) {
            SegmentedButton(
                selected = mode == MatchEntry.MatchMode.Include,
                onClick = { onModeChange(MatchEntry.MatchMode.Include) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(MatchEntry.MatchMode.Include.translateText)
            }
        }
        TipBox({ Text(MatchEntry.MatchMode.Exclude.translateComment) }) {
            SegmentedButton(
                selected = mode == MatchEntry.MatchMode.Exclude,
                onClick = { onModeChange(MatchEntry.MatchMode.Exclude) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(MatchEntry.MatchMode.Exclude.translateText)
            }
        }
    }
}

@Composable
internal fun CompositeMatcherModeSelector(
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
) {
    val items = CompositeMatcher.MatchMode.entries
    val textStyle = MaterialTheme.typography.labelLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val contentPadding = SegmentedButtonDefaults.ContentPadding

    val maxTextWidth = items.maxOf { item ->
        textMeasurer.measure(
            text = item.translateText.toAnnotatedString(),
            style = textStyle,
            maxLines = 1,
        ).size.width
    }

    val buttonWidth = with(density) {
        maxTextWidth.toDp()
    } + contentPadding.calculateStartPadding(layoutDirection) +
            contentPadding.calculateEndPadding(layoutDirection) +
            SegmentedButtonDefaults.IconSize +
            8.dp // 图标与文本之间的间距

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.width(IntrinsicSize.Max),
    ) {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                selected = mode == item,
                onClick = { onModeChange(item) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = items.size,
                ),
                modifier = Modifier
                    .width(buttonWidth)
                    .plainTooltip {
                        Text(item.translateComment)
                    },
                label = {
                    Text(item.translateText, style = textStyle, maxLines = 1)
                }
            )
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
    }, modifier = Modifier.plainTooltip {
        Text(tip)
    }) {
        Icon(Icons.PlayArrow, null)
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
    Box {
        val scrollState = rememberScrollState()
        OutlinedTextField(
            state = state,
            scrollState = scrollState,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            ),
            outputTransformation = transformation,
            modifier = Modifier.fillMaxSize()
                .codeEditorShortcuts(state)

        )

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState)
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