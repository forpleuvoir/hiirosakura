package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import net.minecraft.world.item.DyeColor
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

@Composable
fun DyeColorSelector(
    value: DyeColor,
    onValueChange: (DyeColor) -> Unit,
    displayColor: (DyeColor) -> List<NebulaColor>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        // 当前选中的颜色，同时作为弹出层锚点
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.size(42.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
            ),
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
            shape = MaterialTheme.shapes.large,
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
                                    onClick = {
                                        expanded = false
                                        if (!selected) {
                                            onValueChange(entry)
                                        }
                                    },
                                    modifier = Modifier.size(42.dp),
                                    shape = MaterialTheme.shapes.small,
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant
                                        },
                                    ),
                                    color = Color.Transparent,
                                ) {
                                    DyeColorPreview(
                                        colors = displayColor(entry),
                                        modifier = Modifier.padding(5.dp).plainTooltip {
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
        .map { it.toComposeColor }

    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

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