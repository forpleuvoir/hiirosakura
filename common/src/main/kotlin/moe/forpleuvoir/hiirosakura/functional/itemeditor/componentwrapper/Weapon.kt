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
import moe.forpleuvoir.ibukigourd.ui.preset.*
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.Weapon

@Composable
fun WeaponComponentWrapper(
    key: Identifier,
    value: Weapon,
    onValueChange: (Weapon) -> Unit,
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
            WeaponEditorDialog(
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
fun WeaponEditorDialog(
    key: Identifier,
    value: Weapon,
    onValueChange: (Weapon) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editingWeapon by remember { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editingWeapon)
            true
        },
        title = title,
        content = {
            CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    IntField(
                        editingWeapon.itemDamagePerAttack,
                        {
                            editingWeapon = Weapon(it, editingWeapon.disableBlockingForSeconds)
                        },
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        label = { Text(key, suffix = "item_damage_per_attack", fallback = "Item Damage Per Attack") },
                        range = 0..Int.MAX_VALUE,
                        modifier = Modifier.width(300.dp)
                    )
                    FloatField(
                        editingWeapon.disableBlockingForSeconds,
                        {
                            editingWeapon = Weapon(editingWeapon.itemDamagePerAttack, it)
                        },
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        range = 0f..Float.MAX_VALUE,
                        label = { Text(key, suffix = "disable_blocking_for_seconds", fallback = "Disable Blocking For Seconds") },
                        modifier = Modifier.width(300.dp)
                    )
                }
            }
        }
    )
}