package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.ui.icon.default.EditLocationAlt
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatchEntryInfoHeight
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryModeDisplayer
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.preset.*
import org.joml.Vector3i
import org.joml.Vector3ic

@Composable
fun BlockInfoMatchEntryPosInfo(entry: BlockInfoMatchEntry.Pos) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(LocalMatchEntryInfoHeight.current)) {
        MatchEntryModeDisplayer(entry.mode)
        Spacer(Modifier.width(8.dp))
        Text(BlockInfoMatchEntry.Pos.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(8.dp))
        Text(entry.asText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryPosRow(
    entry: BlockInfoMatchEntry.Pos,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditLocationAlt, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        BlockInfoMatchEntryPosEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}


@Composable
internal fun BlockInfoMatchEntryPosEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Pos,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicBlockInfoMatchEntryPosEditor(
                value = editingEntry,
                onValueChange = { editingEntry = it },
                modifier = Modifier
            )
        },
        onConfirmRequest = {
            onValueChange(editingEntry)
            true
        }
    )
}

@Composable
internal fun BasicBlockInfoMatchEntryPosEditor(
    value: BlockInfoMatchEntry.Pos,
    modifier: Modifier = Modifier,
    onValueChange: (BlockInfoMatchEntry.Pos) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("min")
                Spacer(Modifier.width(12.dp))
                Vector3iEditor(value.min, onValueChange = { onValueChange(value.copy(min = it)) }, modifier = Modifier.height(64.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("max")
                Spacer(Modifier.width(12.dp))
                Vector3iEditor(value.max, onValueChange = { onValueChange(value.copy(max = it)) }, modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun Vector3iEditor(
    value: Vector3ic,
    onValueChange: (Vector3ic) -> Unit,
    modifier: Modifier = Modifier,
    fieldWidth: Dp = 160.dp,
    spacing: Dp = 12.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        IntField(
            value = value.x(),
            onValueChange = { x ->
                val newValue = Vector3i(x, value.y(), value.z())
                onValueChange(newValue)
            },
            modifier = Modifier.width(fieldWidth),
            label = { Text("X", color = Color.Red) },
        )
        IntField(
            value = value.y(),
            onValueChange = { y ->
                val newValue = Vector3i(value.x(), y, value.z())
                onValueChange(newValue)
            },
            modifier = Modifier.width(fieldWidth),
            label = { Text("Y", color = Color.Green) },
        )
        IntField(
            value = value.z(),
            onValueChange = { z ->
                val newValue = Vector3i(value.x(), value.y(), z)
                onValueChange(newValue)
            },
            modifier = Modifier.width(fieldWidth),
            label = { Text("Z", color = Color.Blue) },
        )
    }
}