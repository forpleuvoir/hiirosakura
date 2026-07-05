package moe.forpleuvoir.hiirosakura.ui.widget.matcher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.icon.default.PlayArrow
import moe.forpleuvoir.hiirosakura.ui.util.rememberScrollFabProgress
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.ibukigourd.event.events.client.input.KeyboardEvent
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.Delete
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.nebula.event.invoke
import net.minecraft.network.chat.Component

internal val LocalMatchEntryRowHeight = staticCompositionLocalOf {
    72.dp
}

internal val LocalMatchEntryRowPadding = staticCompositionLocalOf {
    PaddingValues(20.dp, 12.dp, 12.dp, 12.dp)
}

internal val LocalMatcherDialogContentSize = staticCompositionLocalOf {
    DpSize(820.dp, 600.dp)
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
        label = "configRowBackground"
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
            Text(title)
            Spacer(Modifier.width(16.dp))
            MatchEntryModeSelector(entry.mode) { onChange(entryCopyWithMode(entry, it)) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f, fill = false).padding(start = 8.dp)) {
                content()
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Delete, "${IGLang.Misc.remove} ${title.plainText}")
            }
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
    val editor: EntryEditor<T>
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : MatchEntry<*>> FloatingEntryAddButton(
    modifier: Modifier = Modifier,
    scrollState: ScrollState? = null,
    addMenuOptions: List<AddMenuOption<T>>,
    addAction: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var activeEditor: EntryEditor<T>? by remember { mutableStateOf(null) }

    val progress = rememberScrollFabProgress(scrollState)

    var isAltPressed by remember { mutableStateOf(false) }
    val pressDisposable = KeyboardEvent.Pressed.register {
        if (it.keyCode == Keyboard.LEFT_ALT) isAltPressed = true
    }
    val releaseDisposable = KeyboardEvent.Released.register {
        if (it.keyCode == Keyboard.LEFT_ALT) isAltPressed = false
    }
    DisposableEffect(Unit) {
        onDispose {
            pressDisposable()
            releaseDisposable()
        }
    }
    val altMultiplier by animateFloatAsState(
        targetValue = if (isAltPressed) 0f else 1f,
        animationSpec = tween(200),
        label = "altMultiplier"
    )
    val displayProgress = progress * altMultiplier

    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier.graphicsLayer {
            if (scrollState != null && !expanded) {
                alpha = displayProgress
                translationY = (1f - displayProgress) * 60f
                scaleX = displayProgress
                scaleY = displayProgress
            }
        },
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { expanded = !expanded }
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
                    expanded = false
                    activeEditor = option.editor
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
    SingleChoiceSegmentedButtonRow {
        TipBox({ Text(CompositeMatcher.MatchMode.AllMatch.translateComment) }) {
            SegmentedButton(
                selected = mode == CompositeMatcher.MatchMode.AllMatch,
                onClick = { onModeChange(CompositeMatcher.MatchMode.AllMatch) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
            ) {
                Text(CompositeMatcher.MatchMode.AllMatch.translateText)
            }
        }
        TipBox({ Text(CompositeMatcher.MatchMode.AnyMatch.translateComment) }) {
            SegmentedButton(
                selected = mode == CompositeMatcher.MatchMode.AnyMatch,
                onClick = { onModeChange(CompositeMatcher.MatchMode.AnyMatch) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
            ) {
                Text(CompositeMatcher.MatchMode.AnyMatch.translateText)
            }
        }
        TipBox({ Text(CompositeMatcher.MatchMode.NoneMatch.translateComment) }) {
            SegmentedButton(
                selected = mode == CompositeMatcher.MatchMode.NoneMatch,
                onClick = { onModeChange(CompositeMatcher.MatchMode.NoneMatch) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
            ) {
                Text(CompositeMatcher.MatchMode.NoneMatch.translateText)
            }
        }
    }
}


//region ToolBar

@Composable
internal fun TestButton(
    successMsg: String,
    failedMsg: String,
    test: () -> Boolean
) {
    TipBox({
        Text(HSLang.Common.exportAsJson)
    }) {
        IconButton({
            if (test()) {
                showSuccessToast(successMsg)
            } else {
                showErrorToast(failedMsg)
            }
        }) {
            Icon(Icons.PlayArrow, null)
        }
    }
}

//endregion