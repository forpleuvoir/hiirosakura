package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.FormatExportButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportButton
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.*
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

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
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
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
    AddMenuOption(ItemStackMatchEntry.Item.title) { addAction, onDismiss ->
        ItemStackMatchEntryItemEditorDialog(onDismiss, ItemStackMatchEntry.Item.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Name.title) { addAction, onDismiss ->
        ItemStackMatchEntryNameEditorDialog(onDismiss, ItemStackMatchEntry.Name.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Script.title) { addAction, onDismiss ->
        ItemStackMatchEntryScriptEditorDialog(onDismiss, ItemStackMatchEntry.Script.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Count.title) { addAction, onDismiss ->
        ItemStackMatchEntryCountEditorDialog(onDismiss, ItemStackMatchEntry.Count.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Rarity.title) { addAction, onDismiss ->
        ItemStackMatchEntryRarityEditorDialog(onDismiss, ItemStackMatchEntry.Rarity.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Enchantment.title) { addAction, onDismiss ->
        ItemStackMatchEntryEnchantmentEditorDialog(onDismiss, ItemStackMatchEntry.Enchantment.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.Tag.title) { addAction, onDismiss ->
        ItemStackMatchEntryTagEditorDialog(onDismiss, ItemStackMatchEntry.Tag.default, addAction)
    },
    AddMenuOption(ItemStackMatchEntry.DataComponentType.title) { addAction, onDismiss ->
        ItemStackMatchEntryDataComponentTypeEditorDialog(onDismiss, ItemStackMatchEntry.DataComponentType.default, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(ItemStackMatchEntry.Matcher.title) { addAction, onDismiss ->
        ItemStackMatchEntryMatcherEditorDialog(onDismiss, ItemStackMatchEntry.Matcher.default, addAction)
    }
//endregion
