package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.FormatExportButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportButton
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.*
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.PlainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fadeScaleTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.tooltip
import net.minecraft.world.item.ItemStack

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
    Text(value.mode.translateText, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
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
        0    -> Text(IGLang.Misc.hasNothing)
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
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositeMatcherModeSelector(
                value.mode,
                { onValueChange(value.copy(mode = it)) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ItemStackMatcher.handheldItemStack?.let { itemStack ->
                    TestButton(
                        HSLang.ItemStackMatcher.testButton.plainText,
                        HSLang.ItemStackMatcher.testSuccess.plainText,
                        HSLang.ItemStackMatcher.testFailed.plainText
                    ) {
                        value.match(itemStack)
                    }
                }
                FormatExportButton(IGLang.Misc.copySuccess(HSLang.ItemStackMatcher.title).plainText) {
                    MinecraftClipboard.setClipboardText(it.encode(ItemStackMatcher.serialization(value)))
                }
                FormatImportButton(HSLang.ItemStackMatcher.title.plainText, HSLang.Common.success.plainText) {
                    ItemStackMatcher.deserialization(it)
                        .getOrThrow()
                        .let { onValueChange(it) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()
            Column(Modifier.verticalScroll(scrollState).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                value.entries.forEachIndexed { index, entry ->
                    ItemStackMatchEntryRow(
                        modifier = Modifier.fillMaxWidth(),
                        entry = entry,
                        onChange = { newEntry ->
                            onValueChange(value.copy(entries = value.entries.map { if (it === entry) newEntry else it }))
                        },
                        onRemove = {
                            onValueChange(value.copy(entries = value.entries - entry))
                        }
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
            FloatingEntryAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                scrollState = scrollState,
                addMenuOptions = if (isNested) nestedAddMenuOptions else addMenuOptions
            ) { newEntry ->
                onValueChange(value.copy(entries = value.entries + newEntry))
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
    var editingMatcher by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.ItemStackMatcher.title) },
        content = {
            BasicItemStackMatcherEditor(
                editingMatcher,
                { editingMatcher = it },
                modifier = Modifier.size(LocalMatcherDialogContentSize.current)
            )
        },
        onConfirmRequest = {
            onValueChange(editingMatcher)
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
    modifier: Modifier = Modifier
) = MatchEntryRow(
    title = entry.translateText,
    entry = entry,
    entryCopyWithMode = { e, m -> e.copyWithMode(m) },
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
    AddMenuOption(ItemStackMatchEntry.Item.title, { ItemStackMatchEntry.Item.default }) { addAction, onDismiss ->
        ItemStackMatchEntryItemEditorDialog(onDismiss, ItemStackMatchEntry.Item.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Name.title, { ItemStackMatchEntry.Name.default }) { addAction, onDismiss ->
        ItemStackMatchEntryNameEditorDialog(onDismiss, ItemStackMatchEntry.Name.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Script.title, { ItemStackMatchEntry.Script.default }) { addAction, onDismiss ->
        ItemStackMatchEntryScriptEditorDialog(onDismiss, ItemStackMatchEntry.Script.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Count.title, { ItemStackMatchEntry.Count.default }) { addAction, onDismiss ->
        ItemStackMatchEntryCountEditorDialog(onDismiss, ItemStackMatchEntry.Count.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Rarity.title, { ItemStackMatchEntry.Rarity.default }) { addAction, onDismiss ->
        ItemStackMatchEntryRarityEditorDialog(onDismiss, ItemStackMatchEntry.Rarity.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Enchantment.title, { ItemStackMatchEntry.Enchantment.default }) { addAction, onDismiss ->
        ItemStackMatchEntryEnchantmentEditorDialog(onDismiss, ItemStackMatchEntry.Enchantment.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Tag.title, { ItemStackMatchEntry.Tag.default }) { addAction, onDismiss ->
        ItemStackMatchEntryTagEditorDialog(onDismiss, ItemStackMatchEntry.Tag.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.DataComponentType.title, { ItemStackMatchEntry.DataComponentType.default }) { addAction, onDismiss ->
        ItemStackMatchEntryDataComponentTypeEditorDialog(onDismiss, ItemStackMatchEntry.DataComponentType.default, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(ItemStackMatchEntry.Matcher.title, { ItemStackMatchEntry.Matcher.default }) { addAction, onDismiss ->
        ItemStackMatchEntryMatcherEditorDialog(onDismiss, ItemStackMatchEntry.Matcher.default, addAction)
    }
//endregion
