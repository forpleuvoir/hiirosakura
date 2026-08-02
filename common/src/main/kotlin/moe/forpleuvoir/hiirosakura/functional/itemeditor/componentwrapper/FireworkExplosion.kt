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
import moe.forpleuvoir.hiirosakura.ui.widget.ColorListEditor
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.FireworkExplosion

@Composable
fun FireworkExplosionComponentWrapper(
    key: Identifier,
    value: FireworkExplosion,
    onValueChange: (FireworkExplosion) -> Unit,
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
            FireworkExplosionEditorDialog(
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                key = key
            )
        }
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
        title = { Text(key) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EnumSelector(
                    shape,
                    { shape = it },
                    label = { Text(key, suffix = "shape") },
                    modifier = Modifier.height(64.dp).fillMaxWidth()
                )
                ColorListEditor(
                    colors = colors,
                    onColorsChange = { colors = it },
                    label = { Text(key, suffix = "colors") },
                )
                ColorListEditor(
                    colors = fadeColors,
                    onColorsChange = { fadeColors = it },
                    label = { Text(key, suffix = "fade_colors") },
                )
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