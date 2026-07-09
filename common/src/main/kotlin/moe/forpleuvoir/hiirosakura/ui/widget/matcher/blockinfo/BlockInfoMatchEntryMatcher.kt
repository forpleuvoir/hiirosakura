package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

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
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatcherDialogContentSize
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values

@Composable
fun BlockInfoMatchEntryMatcherInfo(entry: BlockInfoMatchEntry.Matcher) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(BlockInfoMatchEntry.Matcher.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        BlockInfoMatcherSimpleInfo(entry.matcher)
    }
}

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryMatcherRow(
    entry: BlockInfoMatchEntry.Matcher,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    TipBox({
        Text(HSLang.BlockInfoMatcher.Entry.matcher)
    }) {
        Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    }
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditNote, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        BlockInfoMatchEntryMatcherEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}


@Composable
internal fun BlockInfoMatchEntryMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Matcher,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntryMode by remember(value) { mutableStateOf(value.mode) }
    var editingMode by remember(value) { mutableStateOf(value.matcher.mode) }
    val editingEntries = rememberKeyedList(value.matcher.entries)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(value.translateText) },
        content = {
            BasicBlockInfoMatchEntryMatcherEditor(
                entryMode = editingEntryMode,
                onEntryModeChange = { editingEntryMode = it },
                mode = editingMode,
                onModeChange = { editingMode = it },
                entries = editingEntries,
                modifier = Modifier.size(LocalMatcherDialogContentSize.current - DpSize(20.dp, 20.dp)),
            )
        },
        onConfirmRequest = {
            onValueChange(BlockInfoMatchEntry.Matcher(BlockInfoMatcher(editingMode, editingEntries.values()), editingEntryMode))
            true
        }
    )
}

@Composable
internal fun BasicBlockInfoMatchEntryMatcherEditor(
    entryMode: MatchEntry.MatchMode,
    onEntryModeChange: (MatchEntry.MatchMode) -> Unit,
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
    entries: SnapshotStateList<Keyed<BlockInfoMatchEntry>>,
    modifier: Modifier = Modifier
) {
    BasicMatchEntryEditor(
        mode = entryMode,
        onModeChange = onEntryModeChange,
        modifier = modifier,
    ) {
        BasicBlockInfoMatcherEditor(
            mode,
            onModeChange,
            entries,
            isNested = true
        )
    }
}