package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.OptionalHolderSoundEventSelector
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Delete
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.FloatField
import moe.forpleuvoir.ibukigourd.ui.preset.IntField
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.component.KineticWeapon
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Composable
fun KineticWeaponComponentWrapper(
    key: Identifier,
    value: KineticWeapon,
    onValueChange: (KineticWeapon) -> Unit,
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
            KineticWeaponEditorDialog(
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
fun KineticWeaponEditorDialog(
    key: Identifier,
    value: KineticWeapon,
    onValueChange: (KineticWeapon) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var editing by remember { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(editing)
            true
        },
        modifier = Modifier.padding(24.dp),
        title = title,
        content = {
            Column(
                modifier = Modifier.width(780.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IntField(
                        editing.contactCooldownTicks,
                        { editing = editing.copy(contactCooldownTicks = it) },
                        range = 0..Int.MAX_VALUE,
                        label = { Text(key, suffix = "contact_cooldown_ticks") },
                        modifier = Modifier.weight(1f)
                    )
                    IntField(
                        editing.delayTicks,
                        { editing = editing.copy(delayTicks = it) },
                        range = 0..Int.MAX_VALUE,
                        label = { Text(key, suffix = "delay_ticks") },
                        modifier = Modifier.weight(1f)
                    )
                    FloatField(
                        editing.forwardMovement,
                        { editing = editing.copy(forwardMovement = it) },
                        label = { Text(key, suffix = "forward_movement") },
                        modifier = Modifier.weight(1f)
                    )
                    FloatField(
                        editing.damageMultiplier,
                        { editing = editing.copy(damageMultiplier = it) },
                        label = { Text(key, suffix = "damage_multiplier") },
                        modifier = Modifier.weight(1f)
                    )

                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OptionalHolderSoundEventSelector(
                        editing.sound,
                        { editing = editing.copy(sound = it) },
                        label = { Text(key, suffix = "sound") },
                        modifier = Modifier.weight(1f).height(64.dp)
                    )
                    OptionalHolderSoundEventSelector(
                        editing.hitSound,
                        { editing = editing.copy(hitSound = it) },
                        label = { Text(key, suffix = "hit_sound") },
                        modifier = Modifier.weight(1f).height(64.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OptionalKineticWeaponConditionCard(
                        editing.dismountConditions,
                        { editing = editing.copy(dismountConditions = it) },
                        key = key,
                        label = {
                            Text(key, suffix = "dismount_conditions")
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OptionalKineticWeaponConditionCard(
                        editing.knockbackConditions,
                        { editing = editing.copy(knockbackConditions = it) },
                        key = key,
                        label = {
                            Text(key, suffix = "knockback_conditions")
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OptionalKineticWeaponConditionCard(
                        editing.damageConditions,
                        { editing = editing.copy(damageConditions = it) },
                        key = key,
                        label = {
                            Text(key, suffix = "damage_conditions")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
fun OptionalKineticWeaponConditionCard(
    value: Optional<KineticWeapon.Condition>,
    onValueChange: (Optional<KineticWeapon.Condition>) -> Unit,
    key: Identifier,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                label?.invoke()

                IconButton(onClick = {
                    if (value.isEmpty) {
                        onValueChange(Optional.of(KineticWeapon.Condition(0, 7f, 0f)))
                    } else {
                        onValueChange(Optional.empty())
                    }
                }) {
                    val icon = if (value.isEmpty) {
                        Icons.Add
                    } else {
                        Icons.Delete
                    }
                    Icon(icon, null)
                }
            }
            value.getOrNull()?.let { value ->
                IntField(
                    value.maxDurationTicks,
                    { onValueChange(Optional.of(value.copy(maxDurationTicks = it))) },
                    range = 0..Int.MAX_VALUE,
                    label = { Text(key, suffix = "max_duration_ticks") },
                    modifier = Modifier
                )
                FloatField(
                    value.minSpeed,
                    { onValueChange(Optional.of(value.copy(minSpeed = it))) },
                    label = { Text(key, suffix = "min_speed") },
                    modifier = Modifier
                )
                FloatField(
                    value.minRelativeSpeed,
                    { onValueChange(Optional.of(value.copy(minRelativeSpeed = it))) },
                    label = { Text(key, suffix = "min_relative_speed") },
                    modifier = Modifier
                )
            }
        }
    }
}

fun KineticWeapon.copy(
    contactCooldownTicks: Int = this.contactCooldownTicks,
    delayTicks: Int = this.delayTicks,
    dismountConditions: Optional<KineticWeapon.Condition> = this.dismountConditions,
    knockbackConditions: Optional<KineticWeapon.Condition> = this.knockbackConditions,
    damageConditions: Optional<KineticWeapon.Condition> = this.damageConditions,
    forwardMovement: Float = this.forwardMovement,
    damageMultiplier: Float = this.damageMultiplier,
    sound: Optional<Holder<SoundEvent>> = this.sound,
    hitSound: Optional<Holder<SoundEvent>> = this.hitSound,
) = KineticWeapon(
    contactCooldownTicks.coerceIn(0..Int.MAX_VALUE),
    delayTicks.coerceIn(0..Int.MAX_VALUE),
    dismountConditions,
    knockbackConditions,
    damageConditions,
    forwardMovement,
    damageMultiplier,
    sound,
    hitSound
)

fun KineticWeapon.Condition.copy(
    maxDurationTicks: Int = this.maxDurationTicks,
    minSpeed: Float = this.minSpeed,
    minRelativeSpeed: Float = this.minRelativeSpeed,
) = KineticWeapon.Condition(
    maxDurationTicks.coerceIn(0..Int.MAX_VALUE),
    minSpeed,
    minRelativeSpeed
)