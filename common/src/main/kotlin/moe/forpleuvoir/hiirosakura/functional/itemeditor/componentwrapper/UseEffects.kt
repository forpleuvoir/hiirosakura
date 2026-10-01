package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.UseEffects
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun UseEffectsComponentWrapper(
    key: Identifier,
    value: UseEffects,
    onValueChange: (UseEffects) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        Text(
            "can_sprint ${value.canSprint} · interact_vibrations ${value.interactVibrations} · speed_multiplier ${value.speedMultiplier}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        UseEffectsEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "can_sprint")
                    Switch(editing.canSprint, { editing = editing.copy(canSprint = it) })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "interact_vibrations")
                    Switch(editing.interactVibrations, { editing = editing.copy(interactVibrations = it) })
                }
                DataComponentField(key, suffix = "speed_multiplier", modifier = Modifier.fillMaxWidth()) {
                    FloatField(
                        editing.speedMultiplier,
                        { editing = editing.copy(speedMultiplier = it) },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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