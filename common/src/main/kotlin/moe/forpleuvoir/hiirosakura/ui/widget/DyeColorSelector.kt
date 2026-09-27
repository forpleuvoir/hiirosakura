package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenu
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import net.minecraft.world.item.DyeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Rect
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.dropdownMenuAnchor

@Composable
fun DyeColorSelector(
    value: DyeColor,
    onValueChange: (DyeColor) -> Unit,
    displayColor: (DyeColor) -> List<NebulaColor>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }


    var menuAnchorBounds by remember { mutableStateOf(Rect.Zero) }
    Box(modifier.dropdownMenuAnchor { menuAnchorBounds = it }) {
        Surface(
            modifier = Modifier
                .size(42.dp)
                .clickable { expanded = true },
            outlineColor = SokitsuTheme.colorScheme.outline,
            color = Color.Transparent,
        ) {
            DyeColorPreview(
                colors = displayColor(value),
                modifier = Modifier.padding(5.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            anchorBounds = menuAnchorBounds,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DyeColor.entries
                    .chunked(4)
                    .forEach { rowEntries ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            rowEntries.forEach { entry ->
                                val selected = entry == value

                                Surface(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            expanded = false
                                            if (!selected) {
                                                onValueChange(entry)
                                            }
                                        },
                                    outlineColor = if (selected) {
                                        SokitsuTheme.colorScheme.primary
                                    } else {
                                        SokitsuTheme.colorScheme.outline
                                    },
                                    color = Color.Transparent,
                                ) {
                                    DyeColorPreview(
                                        colors = displayColor(entry),
                                        modifier = Modifier.padding(5.dp).tooltip {
                                            Text(entry.translateText)
                                        },
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun DyeColorPreview(
    colors: List<NebulaColor>,
    modifier: Modifier = Modifier,
) {
    val previewColors = colors
        .take(4)
        .map { it.toComposeColor() }


    val emptyColor = SokitsuTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp)),
    ) {
        when (previewColors.size) {
            0    -> {
                drawRect(emptyColor)
            }

            1    -> {
                drawRect(previewColors[0])
            }

            2    -> {
                val halfWidth = size.width / 2f

                drawRect(
                    color = previewColors[0],
                    size = Size(halfWidth, size.height),
                )
                drawRect(
                    color = previewColors[1],
                    topLeft = Offset(halfWidth, 0f),
                    size = Size(size.width - halfWidth, size.height),
                )
            }

            3    -> {
                val halfWidth = size.width / 2f
                val halfHeight = size.height / 2f

                drawRect(
                    color = previewColors[0],
                    size = Size(halfWidth, halfHeight),
                )
                drawRect(
                    color = previewColors[1],
                    topLeft = Offset(halfWidth, 0f),
                    size = Size(size.width - halfWidth, halfHeight),
                )
                drawRect(
                    color = previewColors[2],
                    topLeft = Offset(0f, halfHeight),
                    size = Size(size.width, size.height - halfHeight),
                )
            }

            else -> {
                val halfWidth = size.width / 2f
                val halfHeight = size.height / 2f

                previewColors.forEachIndexed { index, color ->
                    val column = index % 2
                    val row = index / 2

                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = column * halfWidth,
                            y = row * halfHeight,
                        ),
                        size = Size(halfWidth, halfHeight),
                    )
                }
            }
        }
    }
}