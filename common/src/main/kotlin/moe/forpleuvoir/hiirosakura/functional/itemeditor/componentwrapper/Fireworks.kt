package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.ReorderableEditorVerticalGrid
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.util.*
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import sh.calvin.reorderable.ReorderableCollectionItemScope
import moe.forpleuvoir.nebula.common.color.Color as NebulaColor

@Composable
fun FireworksComponentWrapper(
    key: Identifier,
    value: Fireworks,
    onValueChange: (Fireworks) -> Unit,
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
            FireworksEditorDialog(
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
fun FireworksEditorDialog(
    key: Identifier,
    value: Fireworks,
    onValueChange: (Fireworks) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    var flightDuration by remember { mutableIntStateOf(value.flightDuration) }
    val explosions = rememberKeyedList(value.explosions)
    var nextKey by remember { mutableLongStateOf(explosions.size.toLong()) }

    FlexibleDialog(
        onDismissRequest,
        onConfirmRequest = {
            onValueChange(Fireworks(flightDuration, explosions.values().toMutableList()))
            true
        },
        modifier = Modifier
            .width(930.dp)
            .height(760.dp)
            .padding(24.dp),
        title = title,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IntField(
                    flightDuration,
                    { flightDuration = it.coerceIn(0..255) },
                    label = { Text(key, suffix = "flight_duration") },
                    range = 0..255,
                    modifier = Modifier.height(64.dp).fillMaxWidth()
                )
                OutlinedLabelBox(
                    modifier = Modifier.fillMaxSize(),
                    label = { Text(key, suffix = "explosions") },
                    contentPadding = PaddingValues(8.dp),
                ) {
                    ReorderableEditorVerticalGrid(
                        explosions,
                        key = { it.key },
                        onMove = explosions::moveElement,
                        columns = GridCells.Adaptive(360.dp),
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = { lazyGridState ->
                            FloatingActionButton(
                                onClick = {
                                    if (explosions.size < Fireworks.MAX_EXPLOSIONS) explosions.add(Keyed(nextKey++, FireworkExplosion.DEFAULT))
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .size(40.dp)
                                    .fabVisibilityAnimation(rememberFabVisibilityByScroll(lazyGridState))
                            ) {
                                Icon(Icons.Add, IGLang.Misc.add.plainText)
                            }
                        },
                        itemContent = { index, explosion, isDragging, hapticFeedback ->
                            val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                            val handleInteraction = remember { MutableInteractionSource() }
                            val handleHovered by handleInteraction.collectIsHoveredAsState()
                            ExplosionCard(
                                explosion = explosion.value,
                                onValueChange = { explosions[index] = explosion.copyValue(it) },
                                onRemove = { explosions.removeAt(index) },
                                key = key,
                                hapticFeedback = hapticFeedback,
                                handleInteraction = handleInteraction,
                                handleHovered = handleHovered,
                                isDragging = isDragging,
                                modifier = Modifier.scale(scale)
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ReorderableCollectionItemScope.ExplosionCard(
    explosion: FireworkExplosion,
    onValueChange: (FireworkExplosion) -> Unit,
    onRemove: () -> Unit,
    key: Identifier,
    hapticFeedback: HapticFeedback,
    handleInteraction: MutableInteractionSource,
    handleHovered: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DragHandle(hapticFeedback, handleInteraction, handleHovered, isDragging)
                RemoveConfirmButton(key.asTranslateText(suffix = "explosion").plainText, onRemove)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EnumSelector(
                    explosion.shape,
                    { onValueChange(FireworkExplosion(it, explosion.colors, explosion.fadeColors, explosion.hasTrail, explosion.hasTwinkle)) },
                    label = { Text(key, suffix = "shape") },
                    modifier = Modifier.weight(1f)
                )
            }
            ColorRow(
                key = key,
                labelSuffix = "colors",
                colors = explosion.colors,
                onColorsChange = { onValueChange(FireworkExplosion(explosion.shape, it, explosion.fadeColors, explosion.hasTrail, explosion.hasTwinkle)) },
            )
            ColorRow(
                key = key,
                labelSuffix = "fade_colors",
                colors = explosion.fadeColors,
                onColorsChange = { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, it, explosion.hasTrail, explosion.hasTwinkle)) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(key, suffix = "has_trail")
                Spacer(Modifier.weight(1f))
                Switch(explosion.hasTrail, { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, explosion.fadeColors, it, explosion.hasTwinkle)) })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(key, suffix = "has_twinkle")
                Spacer(Modifier.weight(1f))
                Switch(explosion.hasTwinkle, { onValueChange(FireworkExplosion(explosion.shape, explosion.colors, explosion.fadeColors, explosion.hasTrail, it)) })
            }
        }
    }
}

@Composable
private fun ColorRow(
    key: Identifier,
    labelSuffix: String,
    colors: IntList,
    onColorsChange: (IntList) -> Unit,
) {
    OutlinedLabelBox(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(key, suffix = labelSuffix) },
        contentPadding = PaddingValues(8.dp),
    ) {
        val scrollState = rememberScrollState()
        var addCount by remember { mutableIntStateOf(0) }
        LaunchedEffect(addCount) {
            scrollState.animateScrollTo(Int.MAX_VALUE)
        }
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (i in colors.indices) {
                val color = colors.getInt(i)
                ColorCircle(
                    color = color,
                    onEdit = { newColor ->
                        val list = IntArrayList(colors)
                        list.set(i, newColor.argb and 0xFFFFFF)
                        onColorsChange(list)
                    },
                    onRemove = {
                        val list = IntArrayList(colors)
                        list.removeInt(i)
                        onColorsChange(list)
                    },
                )
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
                        addCount++
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ColorCircle(
    color: Int,
    onEdit: (NebulaColor) -> Unit,
    onRemove: () -> Unit,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showContextMenu by remember { mutableStateOf(false) }
    var pendingColor by remember(color) { mutableStateOf(NebulaColor.fromRGB(color)) }

    Box {
        IconButton(
            onClick = { showColorPicker = true },
            modifier = Modifier
                .size(28.dp)
                .onPointerEvent(PointerEventType.Press) { event ->
                    if (event.button == PointerButton.Secondary) {
                        showContextMenu = true
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(pendingColor.toComposeColor)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
        }
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
        ) {
            DropdownMenuItem(
                text = { Text(IGLang.Misc.edit) },
                onClick = {
                    showContextMenu = false
                    showColorPicker = true
                },
            )
            DropdownMenuItem(
                text = { Text(IGLang.Misc.remove) },
                onClick = {
                    showContextMenu = false
                    onRemove()
                },
            )
        }
        if (showColorPicker) {
            SimpleAlertDialog(
                onDismissRequest = { showColorPicker = false },
                onConfirmRequest = {
                    onEdit(pendingColor)
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
}