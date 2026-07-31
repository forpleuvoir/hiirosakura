package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.OptionalHolderSoundEventSelector
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.component.PiercingWeapon
import java.util.*

@Composable
fun PiercingWeaponComponentWrapper(
    key: Identifier,
    value: PiercingWeapon,
    onValueChange: (PiercingWeapon) -> Unit,
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
            PiercingWeaponEditorDialog(
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
fun PiercingWeaponEditorDialog(
    key: Identifier,
    value: PiercingWeapon,
    onValueChange: (PiercingWeapon) -> Unit,
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
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(key, suffix = "deals_knockback")
                    Switch(editing.dealsKnockback, { editing = editing.copy(dealsKnockback = it) })
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(key, suffix = "dismounts")
                    Switch(editing.dismounts, { editing = editing.copy(dismounts = it) })
                }
                OptionalHolderSoundEventSelector(
                    editing.sound,
                    { editing = editing.copy(sound = it) },
                    label = { Text(key, suffix = "sound") },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
                OptionalHolderSoundEventSelector(
                    editing.hitSound,
                    { editing = editing.copy(hitSound = it) },
                    label = { Text(key, suffix = "hit_sound") },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    )
}

fun PiercingWeapon.copy(
    dealsKnockback: Boolean = this.dealsKnockback,
    dismounts: Boolean = this.dismounts,
    sound: Optional<Holder<SoundEvent>> = this.sound,
    hitSound: Optional<Holder<SoundEvent>> = this.hitSound,
) = PiercingWeapon(
    dealsKnockback,
    dismounts,
    sound,
    hitSound
)