package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.SwingAnimation
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SwingAnimationComponentWrapper(
    key: Identifier,
    value: SwingAnimation,
    onValueChange: (SwingAnimation) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height)) {
        var showDialog by remember { mutableStateOf(false) }
        IconButton(onClick = {
            showDialog = true
        }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(Icons.Edit)
        }

        if (showDialog) {
            SwingAnimationEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

@Composable
fun SwingAnimationEditorDialog(
    key: Identifier,
    value: SwingAnimation,
    onValueChange: (SwingAnimation) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing)
            true
        },
        title = title,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EnumSelector(
                    editing.type,
                    { editing = SwingAnimation(it, editing.duration) },
                    label = { Text(key, suffix = "type", fallback = "Type") },
                    modifier = Modifier.width(300.dp)
                )
                                LabelBox(label = { Text(key, suffix = "duration", fallback = "Duration") }, modifier = Modifier.width(300.dp)) {
                    IntField(
                        editing.duration,
                        {

                        editing = SwingAnimation(editing.type, it)

                    },
                        valueRange = 1..Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    )
}