package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.ui.widget.OptionalHolderSoundEventSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.component.KineticWeapon
import java.util.*
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun KineticWeaponComponentWrapper(
    key: Identifier,
    value: KineticWeapon,
    onValueChange: (KineticWeapon) -> Unit,
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
            "contact_cooldown_ticks ${value.contactCooldownTicks} · delay_ticks ${value.delayTicks} · forward_movement ${value.forwardMovement} · damage_multiplier ${value.damageMultiplier}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        KineticWeaponEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
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
                    DataComponentSection(key, suffix = "contact_cooldown_ticks", modifier = Modifier.weight(1f)) {
                        IntField(
                            editing.contactCooldownTicks,
                            { editing = editing.copy(contactCooldownTicks = it) },
                            valueRange = 0..Int.MAX_VALUE,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(key, suffix = "delay_ticks", modifier = Modifier.weight(1f)) {
                        IntField(
                            editing.delayTicks,
                            { editing = editing.copy(delayTicks = it) },
                            valueRange = 0..Int.MAX_VALUE,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(key, suffix = "forward_movement", modifier = Modifier.weight(1f)) {
                        FloatField(
                            editing.forwardMovement,
                            { editing = editing.copy(forwardMovement = it) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(key, suffix = "damage_multiplier", modifier = Modifier.weight(1f)) {
                        FloatField(
                            editing.damageMultiplier,
                            { editing = editing.copy(damageMultiplier = it) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DataComponentSection(key, suffix = "sound", modifier = Modifier.weight(1f)) {
                        OptionalHolderSoundEventSelector(
                            editing.sound,
                            { editing = editing.copy(sound = it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DataComponentSection(key, suffix = "hit_sound", modifier = Modifier.weight(1f)) {
                        OptionalHolderSoundEventSelector(
                            editing.hitSound,
                            { editing = editing.copy(hitSound = it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
    Surface(
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
                    Icon(icon)
                }
            }
            value.getOrNull()?.let { value ->
                DataComponentSection(key, suffix = "max_duration_ticks", modifier = Modifier.fillMaxWidth()) {
                    IntField(
                        value.maxDurationTicks,
                        { onValueChange(Optional.of(value.copy(maxDurationTicks = it))) },
                        valueRange = 0..Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DataComponentSection(key, suffix = "min_speed", modifier = Modifier.fillMaxWidth()) {
                    FloatField(
                        value.minSpeed,
                        { onValueChange(Optional.of(value.copy(minSpeed = it))) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DataComponentSection(key, suffix = "min_relative_speed", modifier = Modifier.fillMaxWidth()) {
                    FloatField(
                        value.minRelativeSpeed,
                        { onValueChange(Optional.of(value.copy(minRelativeSpeed = it))) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
