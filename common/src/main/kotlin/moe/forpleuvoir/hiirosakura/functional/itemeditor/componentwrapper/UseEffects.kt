package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FloatField
import moe.forpleuvoir.ibukigourd.ui.preset.LocalNumberFieldStyle
import moe.forpleuvoir.ibukigourd.ui.preset.NumberFieldStyle
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.UseEffects

@Composable
fun UseEffectsComponentWrapper(
    key: Identifier,
    value: UseEffects,
    onValueChange: (UseEffects) -> Unit,
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
            Icon(Icons.EditNote, null)
        }
        if (showDialog) {
            UseEffectsEditorDialog(
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
fun UseEffectsEditorDialog(
    key: Identifier,
    value: UseEffects,
    onValueChange: (UseEffects) -> Unit,
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
                Row(
                    modifier = Modifier.width(300.dp).height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "can_sprint")
                    Switch(editing.canSprint, { editing = editing.copy(canSprint = it) })
                }
                Row(
                    modifier = Modifier.width(300.dp).height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "interact_vibrations")
                    Switch(editing.interactVibrations, { editing = editing.copy(interactVibrations = it) })
                }
                FloatField(
                    editing.speedMultiplier,
                    { editing = editing.copy(speedMultiplier = it) },
                    range = 0f..1f,
                    label = { Text(key, suffix = "speed_multiplier") },
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    )
}

fun UseEffects.copy(
    canSprint: Boolean = this.canSprint,
    interactVibrations: Boolean = this.interactVibrations,
    speedMultiplier: Float = this.speedMultiplier,
) = UseEffects(
    canSprint,
    interactVibrations,
    speedMultiplier.coerceIn(0f, 1f),
)