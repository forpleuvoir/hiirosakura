package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicScriptEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox

@Composable
fun BlockInfoMatchEntryScriptInfo(entry: BlockInfoMatchEntry.Script) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(BlockInfoMatchEntry.Script.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryScriptRow(
    entry: BlockInfoMatchEntry.Script,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {

    val lines = entry.script.lineSequence().toList()
    val displayText = if (lines.size <= 5) {
        entry.script
    } else {
        lines.take(5).joinToString("\n") + "..."
    }

    TipBox({
        Text(displayText)
    }) {
        Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    }
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditNote, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        BlockInfoMatchEntryScriptEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}


@Composable
internal fun BlockInfoMatchEntryScriptEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Script,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        modifier = Modifier.padding(24.dp).heightIn(480.dp).fillMaxHeight(0.85f).widthIn(720.dp).fillMaxWidth(0.65f),
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicBlockInfoMatchEntryScriptEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier.fillMaxSize()
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicBlockInfoMatchEntryScriptEditor(
    value: BlockInfoMatchEntry.Script,
    modifier: Modifier = Modifier,
    onValueChange: (BlockInfoMatchEntry.Script) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        BasicScriptEditor(value.script, { onValueChange(value.copy(script = it)) })
    }
}