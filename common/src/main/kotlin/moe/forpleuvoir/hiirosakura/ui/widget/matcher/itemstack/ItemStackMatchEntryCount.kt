package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*

@Composable
fun ItemStackMatchEntryCountInfo(entry: ItemStackMatchEntry.Count) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Count.title)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryCountRow(
    entry: ItemStackMatchEntry.Count,
    onChange: (ItemStackMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditNote, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        ItemStackMatchEntryCountEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}

@Composable
internal fun ItemStackMatchEntryCountEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.Count,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryCountEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.width(360.dp)
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryCountEditor(
    value: ItemStackMatchEntry.Count,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.Count) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                IntField(
                    value.count.first,
                    { onValueChange(value.copy(count = it..value.count.last)) },
                    range = 1..Int.MAX_VALUE,
                    modifier = Modifier.weight(1f)
                )
                Text("≤..≤", modifier = Modifier.padding(horizontal = 12.dp))
                IntField(
                    value.count.last,
                    { onValueChange(value.copy(count = value.count.first..it)) },
                    range = value.count.first..Int.MAX_VALUE,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
