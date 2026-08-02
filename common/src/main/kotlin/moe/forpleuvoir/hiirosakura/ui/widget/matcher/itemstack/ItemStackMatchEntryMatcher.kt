package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatcherDialogContentSize
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values


@Composable
fun ItemStackMatchEntryMatcherInfo(entry: ItemStackMatchEntry.Matcher) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Matcher.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        ItemStackMatcherSimpleInfo(entry.matcher)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryMatcherRow(
    entry: ItemStackMatchEntry.Matcher,
    onChange: (ItemStackMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    TipBox({
        Text(HSLang.ItemStackMatcher.Entry.matcher)
    }) {
        Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    }
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditNote, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        ItemStackMatchEntryMatcherEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}

@Composable
internal fun ItemStackMatchEntryMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.Matcher,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntryMode by remember(value) { mutableStateOf(value.mode) }
    var editingMode by remember(value) { mutableStateOf(value.matcher.mode) }
    val editingEntries = rememberKeyedList(value.matcher.entries)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(value.translateText) },
        content = {
            BasicItemStackMatchEntryMatcherEditor(
                entryMode = editingEntryMode,
                onEntryModeChange = { editingEntryMode = it },
                mode = editingMode,
                onModeChange = { editingMode = it },
                entries = editingEntries,
                modifier = Modifier.size(LocalMatcherDialogContentSize.current - DpSize(20.dp, 20.dp)),
            )
        },
        onConfirmRequest = {
            onValueChange(ItemStackMatchEntry.Matcher(ItemStackMatcher(editingMode, editingEntries.values()), editingEntryMode))
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryMatcherEditor(
    entryMode: MatchEntry.MatchMode,
    onEntryModeChange: (MatchEntry.MatchMode) -> Unit,
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
    entries: SnapshotStateList<Keyed<ItemStackMatchEntry>>,
    modifier: Modifier = Modifier
) {
    BasicMatchEntryEditor(
        mode = entryMode,
        onModeChange = onEntryModeChange,
        modifier = modifier,
    ) {
        BasicItemStackMatcherEditor(
            mode,
            onModeChange,
            entries,
            isNested = true
        )
    }
}
