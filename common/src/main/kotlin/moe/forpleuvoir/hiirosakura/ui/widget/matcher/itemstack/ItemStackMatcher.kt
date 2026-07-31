package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.AddMenuOption
import moe.forpleuvoir.hiirosakura.ui.widget.FloatingAddButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatExportButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportButton
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.CompositeMatcherModeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatcherDialogContentSize
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryRow
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.TestButton
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.PlainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fadeScaleTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.tooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

//region Displayer

@Composable
fun ItemStackMatcherDisplayerEditor(
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    displayModifier: RowScope.() -> Modifier = { Modifier },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = Row(
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    modifier = modifier
) {
    ItemStackMatcherDisplayer(
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
        ItemStackMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun ItemStackMatcherDisplayerInnerEditor(
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    ItemStackMatcherDisplayer(
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
        ItemStackMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun ItemStackMatcherDisplayer(
    value: ItemStackMatcher,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    AssistChip(
        {},
        modifier = modifier.tooltip {
            fadeScaleTooltip {
                PlainTooltip {
                    ItemStackMatcherInfo(value)
                }
            }
        },
        label = {
            ItemStackMatcherSimpleInfo(value, Modifier.padding(vertical = 8.dp))
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    )
}

@Composable
fun ItemStackMatcherInfo(
    value: ItemStackMatcher,
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
        ItemStackMatchEntryInfo(entry)
    }
}

@Composable
fun ItemStackMatcherSimpleInfo(
    value: ItemStackMatcher,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    val entries = value.entries
    when (entries.size) {
        0    -> Text(IGLang.Misc.hasNothing, maxLines = 1, overflow = TextOverflow.Ellipsis)
        1    -> ItemStackMatchEntryInfo(entries.first())
        else -> {
            if (ItemStackMatcher.isAnyMatcher(value)) {
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
fun ItemStackMatchEntryInfo(entry: MatchEntry<ItemStack>) {
    when (entry) {
        is ItemStackMatchEntry.Matcher           -> ItemStackMatchEntryMatcherInfo(entry)
        is ItemStackMatchEntry.Item              -> ItemStackMatchEntryItemInfo(entry)
        is ItemStackMatchEntry.Name              -> ItemStackMatchEntryNameInfo(entry)
        is ItemStackMatchEntry.Script            -> ItemStackMatchEntryScriptInfo(entry)
        is ItemStackMatchEntry.Count             -> ItemStackMatchEntryCountInfo(entry)
        is ItemStackMatchEntry.Enchantment       -> ItemStackMatchEntryEnchantmentInfo(entry)
        is ItemStackMatchEntry.Tag               -> ItemStackMatchEntryTagInfo(entry)
        is ItemStackMatchEntry.Rarity            -> ItemStackMatchEntryRarityInfo(entry)
        is ItemStackMatchEntry.DataComponentType -> ItemStackMatchEntryDataComponentTypeInfo(entry)
    }
}
//endregion

@Composable
fun BasicItemStackMatcherEditor(
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
    entries: SnapshotStateList<Keyed<ItemStackMatchEntry>>,
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
                ItemStackMatcher.handheldItemStack?.let { itemStack ->
                    TestButton(
                        HSLang.ItemStackMatcher.testButton.plainText,
                        HSLang.ItemStackMatcher.testSuccess.plainText,
                        HSLang.ItemStackMatcher.testFailed.plainText
                    ) {
                        ItemStackMatcher(mode, entries.values()).match(itemStack)
                    }
                }
                FormatExportButton(IGLang.Misc.copySuccess(HSLang.ItemStackMatcher.title).plainText) {
                    MinecraftClipboard.setClipboardText(it.encode(ItemStackMatcher.serialization(ItemStackMatcher(mode, entries.values()))))
                }
                FormatImportButton(HSLang.ItemStackMatcher.title.plainText, HSLang.Common.success.plainText) {
                    ItemStackMatcher.deserialization(it)
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
                        ItemStackMatchEntryRow(
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

            FloatingAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                fabVisibilityState = rememberFabVisibilityByScroll(lazyListState),
                addMenuOptions = if (isNested) nestedAddMenuOptions else addMenuOptions
            ) { newEntry ->
                entries.add(Keyed(entries.size.toLong(), newEntry))
            }
        }
    }
}

@Composable
fun ItemStackMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
) {
    var editingMode by remember(value) { mutableStateOf(value.mode) }
    val editingEntries = rememberKeyedList(value.entries)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.ItemStackMatcher.title) },
        content = {
            BasicItemStackMatcherEditor(
                editingMode,
                { editingMode = it },
                editingEntries,
                modifier = Modifier.size(LocalMatcherDialogContentSize.current)
            )
        },
        onConfirmRequest = {
            onValueChange(ItemStackMatcher(editingMode, editingEntries.values()))
            true
        }
    )
}

//region EntryRow

@Composable
private fun ItemStackMatchEntryRow(
    entry: ItemStackMatchEntry,
    onChange: (ItemStackMatchEntry) -> Unit,
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
        is ItemStackMatchEntry.Matcher           -> ItemStackMatchEntryMatcherRow(entry, onChange)
        is ItemStackMatchEntry.Item              -> ItemStackMatchEntryItemRow(entry, onChange)
        is ItemStackMatchEntry.Name              -> ItemStackMatchEntryNameRow(entry, onChange)
        is ItemStackMatchEntry.Script            -> ItemStackMatchEntryScriptRow(entry, onChange)
        is ItemStackMatchEntry.Count             -> ItemStackMatchEntryCountRow(entry, onChange)
        is ItemStackMatchEntry.Rarity            -> ItemStackMatchEntryRarityRow(entry, onChange)
        is ItemStackMatchEntry.Enchantment       -> ItemStackMatchEntryEnchantmentRow(entry, onChange)
        is ItemStackMatchEntry.Tag               -> ItemStackMatchEntryTagRow(entry, onChange)
        is ItemStackMatchEntry.DataComponentType -> ItemStackMatchEntryDataComponentTypeRow(entry, onChange)
    }
}

//endregion

//region Adder

private val nestedAddMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> = listOf(
    AddMenuOption({ Text(ItemStackMatchEntry.Item.title) }, { ItemStackMatchEntry.Item.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryItemEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Item, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Name.title) }, { ItemStackMatchEntry.Name.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryNameEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Name, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Script.title) }, { ItemStackMatchEntry.Script.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryScriptEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Script, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Count.title) }, { ItemStackMatchEntry.Count.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryCountEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Count, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Rarity.title) }, { ItemStackMatchEntry.Rarity.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryRarityEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Rarity, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Enchantment.title) }, { ItemStackMatchEntry.Enchantment.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryEnchantmentEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Enchantment, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Tag.title) }, { ItemStackMatchEntry.Tag.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryTagEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Tag, addAction)
    },
    AddMenuOption(
        { Text(ItemStackMatchEntry.DataComponentType.title) },
        { ItemStackMatchEntry.DataComponentType.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryDataComponentTypeEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.DataComponentType, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(
        { Text(ItemStackMatchEntry.Matcher.title) },
        { ItemStackMatchEntry.Matcher.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryMatcherEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Matcher, addAction)
    }
//endregion
