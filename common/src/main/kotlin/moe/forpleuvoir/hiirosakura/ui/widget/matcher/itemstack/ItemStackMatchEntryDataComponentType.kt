package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.DataComponentTypeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

@Composable
fun ItemStackMatchEntryDataComponentTypeInfo(entry: ItemStackMatchEntry.DataComponentType) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(ItemStackMatchEntry.DataComponentType.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在 Row 中简单信息展示
 */
@Composable
internal fun ItemStackMatchEntryDataComponentTypeRow(
    entry: ItemStackMatchEntry.DataComponentType,
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
        ItemStackMatchEntryDataComponentTypeEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}

@Composable
internal fun ItemStackMatchEntryDataComponentTypeEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatchEntry.DataComponentType,
    onValueChange: (ItemStackMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicItemStackMatchEntryDataComponentTypeEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.width(680.dp)
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicItemStackMatchEntryDataComponentTypeEditor(
    value: ItemStackMatchEntry.DataComponentType,
    modifier: Modifier = Modifier,
    onValueChange: (ItemStackMatchEntry.DataComponentType) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        //从手中物品获取
        val width = remember { 440.dp }
        val components = remember(value) { ItemStackMatcher.handheldItemStack?.components?.keySet()?.toList() }
        if (!components.isNullOrEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(HSLang.Common.getFromHandItem)
                var selected by remember(value) { mutableStateOf(components.find { it == value } ?: components.first()) }
                DataComponentTypeSelector(
                    selected,
                    { onValueChange(value.copy(componentType = it)) },
                    items = components,
                    modifier = Modifier.width(width)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        //从注册表获取
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(HSLang.ItemStackMatcher.Entry.dataComponentType)
            DataComponentTypeSelector(
                value.componentType,
                { onValueChange(value.copy(componentType = it)) },
                modifier = Modifier.width(width),
                searchFilter = { str, type ->
                    str in type.keyOrUnknown.toString() || str in type.keyOrUnknown.asTranslateText().plainText
                }
            )
        }
    }
}
