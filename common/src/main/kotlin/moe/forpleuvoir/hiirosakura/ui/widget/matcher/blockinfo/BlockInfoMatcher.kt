package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.hiirosakura.ui.widget.FormatExportButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportButton
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.*
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.DragHandle
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.PlainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fadeScaleTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.moveElement
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

//region Displayer

@Composable
fun BlockInfoMatcherDisplayerEditor(
    value: BlockInfoMatcher,
    onValueChange: (BlockInfoMatcher) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    displayModifier: RowScope.() -> Modifier = { Modifier },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = Row(
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    modifier = modifier
) {
    BlockInfoMatcherDisplayer(
        value = value,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = Modifier.padding(vertical = 8.dp).then(displayModifier()),
    )
    Spacer(Modifier.width(ConfigRowWrapper.spacing))
    var showDialog by remember { mutableStateOf(false) }
    IconButton(onClick = {
        showDialog = true
    }) {
        Icon(Icons.EditNote, null)
    }
    if (showDialog) {
        BlockInfoMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun BlockInfoMatcherDisplayerInnerEditor(
    value: BlockInfoMatcher,
    onValueChange: (BlockInfoMatcher) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    BlockInfoMatcherDisplayer(
        value = value,
        modifier = Modifier.padding(vertical = 8.dp).then(modifier),
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.EditNote, null)
            }
        }
    )
    if (showDialog) {
        BlockInfoMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun BlockInfoMatcherDisplayer(
    value: BlockInfoMatcher,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    AssistChip(
        {},
        modifier = modifier.tooltip {
            fadeScaleTooltip {
                PlainTooltip {
                    BlockInfoMatcherInfo(value)
                }
            }
        },
        label = {
            BlockInfoMatcherSimpleInfo(value, Modifier.padding(vertical = 8.dp))
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}

@Composable
fun BlockInfoMatcherInfo(
    value: BlockInfoMatcher,
    modifier: Modifier = Modifier.width(IntrinsicSize.Min),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) = Column(modifier, verticalArrangement, horizontalAlignment) {
    Text(
        value.mode.translateText,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    HorizontalDivider()
    value.entries.take(10).forEach { entry ->
        BlockInfoMatchEntryInfo(entry)
    }
}

@Composable
fun BlockInfoMatcherSimpleInfo(
    value: BlockInfoMatcher,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    val entries = value.entries
    when (entries.size) {
        0    -> Text(IGLang.Misc.hasNothing, maxLines = 1, overflow = TextOverflow.Ellipsis)
        1    -> BlockInfoMatchEntryInfo(entries.first())
        else -> {
            if (BlockInfoMatcher.isAnyMatcher(value)) {
                Text(CompositeMatcher.MatchMode.AnyMatch.translateText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else Text(
                value.mode.translateText.appendLiteral(":").append(HSLang.Matcher.entries(entries.size)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BlockInfoMatchEntryInfo(entry: MatchEntry<BlockInfo>) {
    when (entry) {
        is BlockInfoMatchEntry.Matcher  -> BlockInfoMatchEntryMatcherInfo(entry)
        is BlockInfoMatchEntry.Block    -> BlockInfoMatchEntryBlockInfo(entry)
        is BlockInfoMatchEntry.Script   -> BlockInfoMatchEntryScriptInfo(entry)
        is BlockInfoMatchEntry.Pos      -> BlockInfoMatchEntryPosInfo(entry)
        is BlockInfoMatchEntry.Tag      -> BlockInfoMatchEntryTagInfo(entry)
        is BlockInfoMatchEntry.Property -> BlockInfoMatchEntryPropertyInfo(entry)
    }
}
//endregion

@Composable
fun BasicBlockInfoMatcherEditor(
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
    entries: SnapshotStateList<Keyed<BlockInfoMatchEntry>>,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositeMatcherModeSelector(mode, onModeChange)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                mc.targetBlock?.let { targetBlock ->
                    TestButton(
                        HSLang.BlockInfoMatcher.testButton.plainText,
                        HSLang.BlockInfoMatcher.testSuccess.plainText,
                        HSLang.BlockInfoMatcher.testFailed.plainText
                    ) {
                        BlockInfoMatcher(mode, entries.values()).match(targetBlock)
                    }
                }
                FormatExportButton(IGLang.Misc.copySuccess(HSLang.BlockInfoMatcher.title).plainText) {
                    MinecraftClipboard.setClipboardText(it.encode(BlockInfoMatcher.serialization(BlockInfoMatcher(mode, entries.values()))))
                }
                FormatImportButton(HSLang.BlockInfoMatcher.title.plainText, HSLang.Common.success.plainText) {
                    BlockInfoMatcher.deserialization(it)
                        .getOrThrow()
                        .let { result ->
                            onModeChange(result.mode)
                            entries.clear()
                            entries.addAll(result.entries.mapIndexed { index, entry -> Keyed(index.toLong(), entry) })
                        }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxSize()) {

            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                entries.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {
                itemsIndexed(entries, key = { _, keyed -> keyed.key }) { index, (key, entry) ->
                    ReorderableItem(reorderableLazyListState, key = key) { isDragging ->
                        val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                        val handleInteraction = remember { MutableInteractionSource() }
                        val handleHovered by handleInteraction.collectIsHoveredAsState()
                        BlockInfoMatchEntryRow(
                            modifier = Modifier.fillMaxWidth().scale(scale),
                            entry = entry,
                            onChange = { newEntry ->
                                entries[index] = entries[index].copyValue(newEntry)
                            },
                            onRemove = {
                                entries.removeAt(index)
                            },
                            moveHandler = {
                                DragHandle(
                                    hapticFeedback,
                                    handleInteraction,
                                    handleHovered,
                                    isDragging
                                )
                            }
                        )
                    }

                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            FloatingEntryAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                scrollState = lazyListState,
                addMenuOptions = if (isNested) nestedAddMenuOptions else addMenuOptions
            ) { newEntry ->
                entries.add(Keyed(entries.size.toLong(), newEntry))
            }
        }
    }
}

@Composable
fun BlockInfoMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatcher,
    onValueChange: (BlockInfoMatcher) -> Unit,
) {
    var editingMode by remember(value) { mutableStateOf(value.mode) }
    val editingEntries = rememberKeyedList(value.entries)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.BlockInfoMatcher.title) },
        content = {
            BasicBlockInfoMatcherEditor(
                editingMode,
                { editingMode = it },
                editingEntries,
                modifier = Modifier.size(LocalMatcherDialogContentSize.current)
            )
        },
        onConfirmRequest = {
            onValueChange(BlockInfoMatcher(editingMode, editingEntries.values()))
            true
        }
    )
}

//region EntryRow

@Composable
private fun BlockInfoMatchEntryRow(
    entry: BlockInfoMatchEntry,
    onChange: (BlockInfoMatchEntry) -> Unit,
    onRemove: () -> Unit,
    moveHandler: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) = MatchEntryRow(
    title = entry.translateText,
    entry = entry,
    entryCopyWithMode = { e, m -> e.copyWithMode(m) },
    moveHandler = moveHandler,
    onChange = onChange,
    onRemove = onRemove,
    modifier = modifier
) {
    when (entry) {
        is BlockInfoMatchEntry.Matcher  -> BlockInfoMatchEntryMatcherRow(entry, onChange)
        is BlockInfoMatchEntry.Block    -> BlockInfoMatchEntryBlockRow(entry, onChange)
        is BlockInfoMatchEntry.Script   -> BlockInfoMatchEntryScriptRow(entry, onChange)
        is BlockInfoMatchEntry.Pos      -> BlockInfoMatchEntryPosRow(entry, onChange)
        is BlockInfoMatchEntry.Tag      -> BlockInfoMatchEntryTagRow(entry, onChange)
        is BlockInfoMatchEntry.Property -> BlockInfoMatchEntryPropertyRow(entry, onChange)
    }
}

//endregion


//region Adder


private val nestedAddMenuOptions: List<AddMenuOption<BlockInfoMatchEntry>> = listOf(
    AddMenuOption(BlockInfoMatchEntry.Block.title, { BlockInfoMatchEntry.Block.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryBlockEditorDialog(onDismiss, BlockInfoMatchEntry.Block.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Script.title, { BlockInfoMatchEntry.Script.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryScriptEditorDialog(onDismiss, BlockInfoMatchEntry.Script.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Pos.title, { BlockInfoMatchEntry.Pos.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryPosEditorDialog(onDismiss, BlockInfoMatchEntry.Pos.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Tag.title, { BlockInfoMatchEntry.Tag.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryTagEditorDialog(onDismiss, BlockInfoMatchEntry.Tag.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Property.title, { BlockInfoMatchEntry.Property.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryPropertyEditorDialog(onDismiss, BlockInfoMatchEntry.Property.default, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<BlockInfoMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(BlockInfoMatchEntry.Matcher.title, { BlockInfoMatchEntry.Matcher.default }) { addAction, onDismiss ->
        BlockInfoMatchEntryMatcherEditorDialog(onDismiss, BlockInfoMatchEntry.Matcher.default, addAction)
    }
//endregion