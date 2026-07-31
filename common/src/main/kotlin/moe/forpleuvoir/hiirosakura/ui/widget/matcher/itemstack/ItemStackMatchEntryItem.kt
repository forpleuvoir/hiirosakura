package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

@Composable
fun ItemStackMatchEntryItemInfo(entry: ItemStackMatchEntry.Item) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        ItemBrowserDefaults.ItemWrapper(entry.item, showTooltip = false, scaleOnHover = 1f, border = false, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryItemRow(
    entry: ItemStackMatchEntry.Item,
    onChange: (ItemStackMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    ItemSelector(
        value = entry.item,
        onValueChange = { onChange(ItemStackMatchEntry.Item(it, entry.mode)) },
    )
    Spacer(Modifier.width(12.dp))
    Text(entry.asText)
    Spacer(Modifier.width(6.dp))
}

//region Editor
@Composable
internal fun ItemStackMatchEntryItemEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.Item,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryItemEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it }
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryItemEditor(
    value: ItemStackMatchEntry.Item = ItemStackMatchEntry.Item.default,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.Item) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ItemSelector(
                value = value.item,
                onValueChange = { onValueChange(ItemStackMatchEntry.Item(it, value.mode)) },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(value.item.name)
        }
    }
}
//endregion
