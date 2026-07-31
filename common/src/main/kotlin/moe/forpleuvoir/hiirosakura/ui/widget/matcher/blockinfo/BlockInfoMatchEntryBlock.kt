package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.BlockSelector
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

@Composable
fun BlockInfoMatchEntryBlockInfo(entry: BlockInfoMatchEntry.Block) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(BlockInfoMatchEntry.Block.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        ItemBrowserDefaults.ItemWrapper(entry.block, showTooltip = false, scaleOnHover = 1f, border = false, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryBlockRow(
    entry: BlockInfoMatchEntry.Block,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {

    BlockSelector(
        value = entry.block,
        onValueChange = { onChange(entry.copy(block = it)) },
    )

    Spacer(Modifier.width(12.dp))
    Text(entry.asText)
    Spacer(Modifier.width(6.dp))
}


//region Editor
@Composable
internal fun BlockInfoMatchEntryBlockEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Block,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicBlockInfoMatchEntryBlockEditor(
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
internal fun BasicBlockInfoMatchEntryBlockEditor(
    value: BlockInfoMatchEntry.Block = BlockInfoMatchEntry.Block.default,
    modifier: Modifier = Modifier,
    onValueChange: (BlockInfoMatchEntry.Block) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BlockSelector(
                value = value.block,
                onValueChange = { onValueChange(value.copy(block = it)) },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(value.block.name)
        }
    }
}
//endregion