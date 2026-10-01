package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.colorpicker.ColorPicker
import moe.forpleuvoir.ibukigourd.ui.colorpicker.LocalColorPickerEnableAlpha
import moe.forpleuvoir.ibukigourd.util.toNebulaColor

@Composable
fun ColorListEditor(
    colors: IntList,
    onColorsChange: (IntList) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                    .background(pendingColor.toComposeColor())
                    .border(1.dp, SokitsuTheme.colorScheme.outline, CircleShape)
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
                    title = { DataComponentDialogTitle(component = IGLang.Misc.edit) },
                    content = {
                        CompositionLocalProvider(LocalColorPickerEnableAlpha provides false) {
                            ColorPicker(pendingColor.toComposeColor(), { pendingColor = it.toNebulaColor() })
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
            Icon(Icons.Add)
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
                title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                content = {
                    CompositionLocalProvider(LocalColorPickerEnableAlpha provides false) {
                        ColorPicker(newColor.toComposeColor(), { newColor = it.toNebulaColor() })
                    }
                }
            )
        }
    }
}