package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.ui.icon.default.EditSquare
import moe.forpleuvoir.hiirosakura.ui.icon.default.Equal
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.BasicMatchEntryEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.rememberTextFieldStateBinding
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.util.Util

/**
 * Entry 在Row中简单信息展示
 */
@Composable
internal fun BlockInfoMatchEntryPropertyRow(
    entry: BlockInfoMatchEntry.Property,
    onChange: (BlockInfoMatchEntry) -> Unit,
    modifier: Modifier = Modifier
) = Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(entry.asText, Modifier.weight(1f, false), overflow = TextOverflow.Ellipsis)
    Spacer(Modifier.width(6.dp))
    var showEditor by remember { mutableStateOf(false) }
    IconButton({ showEditor = true }) {
        Icon(Icons.EditSquare, "${IGLang.Misc.edit} ${entry.translateText.plainText}")
    }
    if (showEditor) {
        BlockInfoMatchEntryPropertyEditorDialog(
            { showEditor = false },
            entry,
            onChange,
        )
    }
}


@Composable
internal fun BlockInfoMatchEntryPropertyEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatchEntry.Property,
    onValueChange: (BlockInfoMatchEntry) -> Unit,
) {
    var editingEntry by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(editingEntry.translateText) },
        content = {
            BasicBlockInfoMatchEntryPropertyEditor(
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
internal fun BasicBlockInfoMatchEntryPropertyEditor(
    value: BlockInfoMatchEntry.Property,
    modifier: Modifier = Modifier,
    onValueChange: (BlockInfoMatchEntry.Property) -> Unit,
) {
    BasicMatchEntryEditor(
        mode = value.mode,
        onModeChange = { onValueChange(value.copy(mode = it)) },
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(value.translateText)

            val keyState = rememberTextFieldStateBinding(value.property.first) { onValueChange(value.copy(property = it to value.property.second)) }
            val valueState = rememberTextFieldStateBinding(value.property.second) { onValueChange(value.copy(property = value.property.first to it)) }

            Spacer(Modifier.height(16.dp))
            val properties = remember {
                mc.targetBlock?.state?.values?.map {
                    it.property.name to Util.getPropertyName(it.property, it.value)
                }?.toList()
            }
            if (!properties.isNullOrEmpty()) {
                Selector(
                    "" to "",
                    { selectedTag ->
                        if (keyState.text.toString() != selectedTag.first) {
                            keyState.edit { replace(0, length, selectedTag.first) }
                        }
                        if (valueState.text.toString() != selectedTag.second) {
                            valueState.edit { replace(0, length, selectedTag.second) }
                        }
                    },
                    items = properties,
                    content = { Text(HSLang.Common.getFromTargetBlock.plainText) },
                    itemContent = { property, _ ->
                        Text("${property.first} = ${property.second}")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    state = keyState,
                    label = { Text(IGLang.ConfigWrapper.mapKey) },
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Equal, null, Modifier.padding(horizontal = 16.dp))
                OutlinedTextField(
                    state = valueState,
                    label = { Text(IGLang.ConfigWrapper.mapValue) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

    }
}
