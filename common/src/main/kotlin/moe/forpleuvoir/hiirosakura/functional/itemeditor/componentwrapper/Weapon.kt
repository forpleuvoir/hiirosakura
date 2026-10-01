package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.Weapon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField

@Composable
fun WeaponComponentWrapper(
    key: Identifier,
    value: Weapon,
    onValueChange: (Weapon) -> Unit,
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
            "item_damage_per_attack ${value.itemDamagePerAttack} · disable_blocking_for_seconds ${value.disableBlockingForSeconds}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        WeaponEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DataComponentField(
                    key,
                    suffix = "item_damage_per_attack",
                    fallback = "Item Damage Per Attack",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IntField(
                        editingWeapon.itemDamagePerAttack,
                        { editingWeapon = Weapon(it, editingWeapon.disableBlockingForSeconds) },
                        valueRange = 0..Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DataComponentField(
                    key,
                    suffix = "disable_blocking_for_seconds",
                    fallback = "Disable Blocking For Seconds",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FloatField(
                        editingWeapon.disableBlockingForSeconds,
                        { editingWeapon = Weapon(editingWeapon.itemDamagePerAttack, it) },
                        valueRange = 0f..Float.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    )
}