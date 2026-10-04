package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.ColorListEditor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.appendNewLine
import moe.forpleuvoir.ibukigourd.text.buildText
import moe.forpleuvoir.ibukigourd.text.flat
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.FireworkExplosion
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.configwrapper.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag

@Composable
fun FireworkExplosionComponentWrapper(
    key: Identifier,
    value: FireworkExplosion,
    onValueChange: (FireworkExplosion) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(mc.player != null) {
            Modifier.tooltip {
                val tooltip = mutableListOf<Component>()
                value.addToTooltip(Item.TooltipContext.EMPTY, { tooltip.add(it) }, TooltipFlag.NORMAL, mc.player!!)
                val base: MutableComponent = Literal("")
                for ((index, element) in tooltip.withIndex()) {
                    base.append(element)
                    if (index != tooltip.lastIndex) base.appendNewLine()
                }
                Text(base)
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(
            "shape ${value.shape.serializedName} · colors ${value.colors.size} · fade_colors ${value.fadeColors.size} · trail ${value.hasTrail} · twinkle ${value.hasTwinkle}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }

    if (showDialog) {
        FireworkExplosionEditorDialog(
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            key = key
        )
    }
}

@Composable
private fun FireworkExplosionEditorDialog(
    value: FireworkExplosion,
    onValueChange: (FireworkExplosion) -> Unit,
    onDismissRequest: () -> Unit,
    key: Identifier,
) {
    var shape by remember { mutableStateOf(value.shape) }

    var colors by remember { mutableStateOf(value.colors) }

    var fadeColors by remember { mutableStateOf(value.fadeColors) }

    var hasTrail by remember { mutableStateOf(value.hasTrail) }

    var hasTwinkle by remember { mutableStateOf(value.hasTwinkle) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = {
            onValueChange(FireworkExplosion(shape, colors, fadeColors, hasTrail, hasTwinkle))
            true
        },
        title = { DataComponentDialogTitle(key) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DataComponentField(key, suffix = "shape", modifier = Modifier.fillMaxWidth()) {
                    EnumSelector(
                        selected = shape,
                        onSelect = { shape = it },
                        items = FireworkExplosion.Shape.entries,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DataComponentField(key, suffix = "colors") {
                    ColorListEditor(
                        colors = colors,
                        onColorsChange = { colors = it },
                    )
                }
                DataComponentField(key, suffix = "fade_colors") {
                    ColorListEditor(
                        colors = fadeColors,
                        onColorsChange = { fadeColors = it },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "has_trail")
                    Switch(hasTrail, { hasTrail = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, suffix = "has_twinkle")
                    Switch(hasTwinkle, { hasTwinkle = it })
                }
            }
        }
    )
}