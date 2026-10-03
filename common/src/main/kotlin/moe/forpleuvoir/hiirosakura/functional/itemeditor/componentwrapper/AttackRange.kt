package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.AttackRange


@Composable
fun AttackRangeComponentWrapper(
    key: Identifier,
    value: AttackRange,
    onValueChange: (AttackRange) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    var shopTooltip by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(shopTooltip) {
            Modifier.tooltip {
                Text("reach ${value.minReach}~${value.maxReach}\ncreative ${value.minCreativeReach}~${value.maxCreativeReach}\nhitbox_margin ${value.hitboxMargin}\nmob_factor ${value.mobFactor}")
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(
            "reach ${value.minReach}~${value.maxReach} · creative ${value.minCreativeReach}~${value.maxCreativeReach} · hitbox_margin ${value.hitboxMargin} · mob_factor ${value.mobFactor}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            onTextLayout = {
                shopTooltip = it.hasVisualOverflow
            },
        )
    }

    if (showDialog) {
        AttackRangeEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DataComponentSection(
                        key,
                        suffix = "min_reach",
                        fallback = "Min Reach",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.minReach,
                            { editing = editing.copy(minReach = it) },
                            valueRange = 0f..64.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(
                        key,
                        suffix = "max_reach",
                        fallback = "Max Reach",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.maxReach,
                            { editing = editing.copy(maxReach = it) },
                            valueRange = 0f..64.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DataComponentSection(
                        key,
                        suffix = "min_creative_reach",
                        fallback = "Min Creative Reach",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.minCreativeReach,
                            { editing = editing.copy(minCreativeReach = it) },
                            valueRange = 0f..64.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(
                        key,
                        suffix = "max_creative_reach",
                        fallback = "Max Creative Reach",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.maxCreativeReach,
                            { editing = editing.copy(maxCreativeReach = it) },
                            valueRange = 0f..64.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DataComponentSection(
                        key,
                        suffix = "hitbox_margin",
                        fallback = "Hitbox Margin",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.hitboxMargin,
                            { editing = editing.copy(hitboxMargin = it) },
                            valueRange = 0f..1.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DataComponentSection(
                        key,
                        suffix = "mob_factor",
                        fallback = "Mob Factor",
                        modifier = Modifier.weight(1f),
                    ) {
                        FloatField(
                            editing.mobFactor,
                            { editing = editing.copy(mobFactor = it) },
                            valueRange = 0f..2.0f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
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
