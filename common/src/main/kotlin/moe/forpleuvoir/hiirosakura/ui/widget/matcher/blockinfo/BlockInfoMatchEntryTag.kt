package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.rememberTextFieldStateBinding
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.StringSelector

@Composable
fun BlockInfoMatchEntryTagInfo(entry: BlockInfoMatchEntry.Tag) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(BlockInfoMatchEntry.Tag.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryTagRow(
    entry: BlockInfoMatchEntry.Tag,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.Edit)
    }

    if (showEditor) {
        BlockInfoMatchEntryTagEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}


@Composable
internal fun BlockInfoMatchEntryTagEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Tag,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicBlockInfoMatchEntryTagEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.width(480.dp)
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicBlockInfoMatchEntryTagEditor(
    value: BlockInfoMatchEntry.Tag,
    modifier: Modifier = Modifier,
    onValueChange: (BlockInfoMatchEntry.Tag) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxWidth()) {
            val state = rememberTextFieldStateBinding(value.tag) { onValueChange(value.copy(tag = it)) }

            val tags = remember {
                mc.targetBlock?.state
                    ?.tags()
                    ?.toList()
                    ?.map { tag -> tag.location.toString() }
            }


            if (!tags.isNullOrEmpty()) {
                StringSelector(
                    HSLang.Common.getFromTargetBlock.plainText,
                    { selectedTag ->
                        if (state.text.toString() != selectedTag) {
                            state.edit { replace(0, length, selectedTag) }
                        }
                    },
                    items = tags,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
            LabelBox(label = { Text(value.translateText) }) {
                TextField(
                    state = state,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                )
            }
        }

    }
}
