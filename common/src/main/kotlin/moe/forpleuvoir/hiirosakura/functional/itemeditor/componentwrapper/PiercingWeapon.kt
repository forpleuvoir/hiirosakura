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
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.OptionalHolderSoundEventSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.component.PiercingWeapon
import java.util.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

@Composable
fun PiercingWeaponComponentWrapper(
    key: Identifier,
    value: PiercingWeapon,
    onValueChange: (PiercingWeapon) -> Unit,
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
            "deals_knockback ${value.dealsKnockback} · dismounts ${value.dismounts}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        PiercingWeaponEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
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
                    modifier = Modifier.fillMaxWidth()
                )
                OptionalHolderSoundEventSelector(
                    editing.hitSound,
                    { editing = editing.copy(hitSound = it) },
                    modifier = Modifier.fillMaxWidth()
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