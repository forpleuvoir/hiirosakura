package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.colorpicker.ColorPicker
import moe.forpleuvoir.ibukigourd.ui.colorpicker.ColorPickerDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.util.contrasting
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.ibukigourd.util.toNebulaColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

@Composable
fun ColorListEditor(
    colors: IntList,
    onColorsChange: (IntList) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingColorIndex by remember { mutableStateOf(-1) }
    DisplayField(
        modifier = modifier,
        trailingIcon = {
            IconButton(onClick = { editingColorIndex = colors.size }) { Icon(Icons.Add) }
        }
    ) {
        val scrollState = rememberScrollState()
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            colors.fastForEachIndexed { index, c ->
                val color = NebulaColor.fromRGB(c).toComposeColor()
                val interactionSource = remember { MutableInteractionSource() }
                val hovered by interactionSource.collectIsHoveredAsState()
                val outlineColor = if (hovered) color.contrasting() else SokitsuTheme.colorScheme.outline
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { editingColorIndex = index }
                        .hoverable(interactionSource)
                        .pointerHoverIcon(PointerIcon.Hand),
                    outlineColor = outlineColor,
                    color = color,
                ) {}
            }
        }
    }

    if (editingColorIndex != -1) {
        val isAdd = editingColorIndex == colors.size
        var editingColor by remember {
            mutableStateOf(
                if (isAdd) Color.Red
                else NebulaColor.fromRGB(colors.getInt(editingColorIndex)).toComposeColor()
            )
        }
        AlertDialog(
            onDismissRequest = { editingColorIndex = -1 },
            confirmButton = {
                FlatButton(onClick = {
                    val list = IntArrayList(colors)
                    val newColor = editingColor.toNebulaColor().rgb
                    if (isAdd) {
                        list.add(newColor)
                    } else {
                        list.set(editingColorIndex, newColor)
                    }
                    onColorsChange(list)
                    editingColorIndex = -1
                }) {
                    Text(IGLang.Misc.confirm)
                }
            },
            dismissButton = {
                FlatButton(onClick = { editingColorIndex = -1 }) {
                    Text(IGLang.Misc.cancel)
                }
            },
            title = { if (isAdd) Text(IGLang.Misc.add) else Text(IGLang.Misc.edit) },
            text = {
                ColorPicker(
                    color = editingColor,
                    onValueChange = { editingColor = it },
                )
            },
            minWidth = ColorPickerDefaults.DialogWidth,
            maxWidth = ColorPickerDefaults.DialogWidth,
        )
    }
}