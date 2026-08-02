package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

@Composable
fun ColorListEditor(
    colors: IntList,
    onColorsChange: (IntList) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedLabelBox(
        modifier = modifier.fillMaxWidth(),
        label = label,
        contentPadding = PaddingValues(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (i in colors.indices) {
                val color = colors.getInt(i)
                var showColorPicker by remember(i) { mutableStateOf(false) }
                var pendingColor by remember(i) { mutableStateOf(NebulaColor.fromRGB(color)) }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(pendingColor.toComposeColor)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable { showColorPicker = true }
                )
                if (showColorPicker) {
                    SimpleAlertDialog(
                        onDismissRequest = { showColorPicker = false },
                        onConfirmRequest = {
                            val list = IntArrayList(colors)
                            list.set(i, pendingColor.argb and 0xFFFFFF)
                            onColorsChange(list)
                            true
                        },
                        title = { Text(IGLang.Misc.edit) },
                        content = {
                            CompositionLocalProvider(LocalColorPickerEnableAlpha provides false) {
                                ColorPicker(pendingColor, { pendingColor = it })
                            }
                        }
                    )
                }
            }
            var showAddPicker by remember { mutableStateOf(false) }
            var newColor by remember { mutableStateOf(NebulaColor.fromRGB(0xFF0000)) }
            IconButton(
                onClick = { showAddPicker = true },
            ) {
                Icon(Icons.Add, null)
            }
            if (showAddPicker) {
                SimpleAlertDialog(
                    onDismissRequest = { showAddPicker = false },
                    onConfirmRequest = {
                        val list = IntArrayList(colors)
                        list.add(newColor.rgb)
                        onColorsChange(list)
                        true
                    },
                    title = { Text(IGLang.Misc.add) },
                    content = {
                        CompositionLocalProvider(LocalColorPickerEnableAlpha provides false) {
                            ColorPicker(newColor, { newColor = it })
                        }
                    }
                )
            }
        }
    }
}