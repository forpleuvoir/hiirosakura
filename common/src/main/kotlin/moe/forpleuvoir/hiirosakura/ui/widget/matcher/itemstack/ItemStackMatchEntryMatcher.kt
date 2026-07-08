package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatcherDialogContentSize
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox


@Composable
fun ItemStackMatchEntryMatcherInfo(entry: ItemStackMatchEntry.Matcher) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Matcher.title)
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
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryMatcherEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.size(LocalMatcherDialogContentSize.current - DpSize(20.dp, 20.dp)),
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryMatcherEditor(
    value: ItemStackMatchEntry.Matcher,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.Matcher) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        BasicItemStackMatcherEditor(value.matcher, { onValueChange(value.copy(matcher = it)) }, isNested = true)
    }
}
