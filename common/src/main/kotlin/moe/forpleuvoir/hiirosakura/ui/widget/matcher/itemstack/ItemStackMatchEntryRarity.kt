package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.RaritySelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

@Composable
fun ItemStackMatchEntryRarityInfo(entry: ItemStackMatchEntry.Rarity) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.Rarity.title)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryRarityRow(
    entry: ItemStackMatchEntry.Rarity,
    onChange: (ItemStackMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    RaritySelector(
        entry.rarity,
        { onChange(entry.copy(rarity = it)) },
        modifier = Modifier.width(160.dp)
    )
}

@Composable
internal fun ItemStackMatchEntryRarityEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.Rarity,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryRarityEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryRarityEditor(
    value: ItemStackMatchEntry.Rarity,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.Rarity) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        RaritySelector(
            value.rarity,
            { onValueChange(value.copy(rarity = it)) },
            modifier = Modifier.width(240.dp)
        )
    }
}


