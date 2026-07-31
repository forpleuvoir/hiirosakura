package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.AttackRange


@Composable
fun AttackRangeComponentWrapper(
    key: Identifier,
    value: AttackRange,
    onValueChange: (AttackRange) -> Unit,
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
            AttackRangeEditorDialog(
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
fun AttackRangeEditorDialog(
    key: Identifier,
    value: AttackRange,
    onValueChange: (AttackRange) -> Unit,
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
                FloatField(
                    editing.minReach,
                    { editing = editing.copy(minReach = it) },
                    range = 0f..64.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "min_reach", fallback = "Min Reach") },
                    modifier = Modifier.width(300.dp)
                )
                FloatField(
                    editing.maxReach,
                    { editing = editing.copy(maxReach = it) },
                    range = 0f..64.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "max_reach", fallback = "Max Reach") },
                    modifier = Modifier.width(300.dp)
                )
                FloatField(
                    editing.minCreativeReach,
                    { editing = editing.copy(minCreativeReach = it) },
                    range = 0f..64.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "min_creative_reach", fallback = "Min Creative Reach") },
                    modifier = Modifier.width(300.dp)
                )
                FloatField(
                    editing.maxCreativeReach,
                    { editing = editing.copy(maxCreativeReach = it) },
                    range = 0f..64.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "max_creative_reach", fallback = "Max Creative Reach") },
                    modifier = Modifier.width(300.dp)
                )
                FloatField(
                    editing.hitboxMargin,
                    { editing = editing.copy(hitboxMargin = it) },
                    range = 0f..1.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "hitbox_margin", fallback = "Hitbox Margin") },
                    modifier = Modifier.width(300.dp)
                )
                FloatField(
                    editing.mobFactor,
                    { editing = editing.copy(mobFactor = it) },
                    range = 0f..2.0f,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = { Text(key, suffix = "mob_factor", fallback = "Mob Factor") },
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    )
}

fun AttackRange.copy(
    minReach: Float = this.minReach,
    maxReach: Float = this.maxReach,
    minCreativeReach: Float = this.minCreativeReach,
    maxCreativeReach: Float = this.maxCreativeReach,
    hitboxMargin: Float = this.hitboxMargin,
    mobFactor: Float = this.mobFactor,
) = AttackRange(
    minReach.coerceIn(0f..64.0f),
    maxReach.coerceIn(0f..64.0f),
    minCreativeReach.coerceIn(0f..64.0f),
    maxCreativeReach.coerceIn(0f..64.0f),
    hitboxMargin.coerceIn(0f..1.0f),
    mobFactor.coerceIn(0f..2.0f),
)