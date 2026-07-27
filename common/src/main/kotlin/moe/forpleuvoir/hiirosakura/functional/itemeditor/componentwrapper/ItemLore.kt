package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberHideActionState
import moe.forpleuvoir.ibukigourd.ui.skia.LocalSkiaSurface
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ItemLore
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ItemLoreComponentWrapper(
    key: Identifier,
    value: ItemLore,
    onValueChange: (ItemLore) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }

        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()
        val surface = LocalSkiaSurface.current
        LaunchedEffect(value) {
            while (isActive) {
                withFrameNanos {
                    if (hovered)
                        surface.postRender {
                            val guiScale = mc.window.guiScale.toFloat()
                            val density = 1f / guiScale
                            val mouseX = (mc.mouseHandler.xpos() * density).toInt()
                            val mouseY = (mc.mouseHandler.ypos() * density).toInt()
                            setTooltipForNextFrame(value.styledLines.map { it.visualOrderText }, mouseX, mouseY)
                        }
                }
            }
        }
        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .width(DataComponentEditorDefaults.entrySize.width)
                .hoverable(interactionSource),
            label = {
                Text(IGLang.ConfigWrapper.listConfigWrapperText(value.styledLines().size), overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            ItemLoreComponentEditDialog(
                value,
                onValueChange,
                { Text(key) },
                { showDialog = false }
            )
        }

    }
}

@Composable
private fun ItemLoreComponentEditDialog(
    value: ItemLore,
    onValueChange: (ItemLore) -> Unit,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val editingLines = rememberKeyedList(value.lines)
    var nextKey by remember { mutableLongStateOf(editingLines.size.toLong()) }
    val maxSize = ItemLore.MAX_LINES
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier
            .padding(24.dp)
            .size(1000.dp, 720.dp),
        onConfirmRequest = {
            onValueChange(ItemLore(editingLines.values().take(maxSize).toMutableList()))
            true
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                var editingComponent by remember { mutableStateOf<Int?>(null) }
                var editingComponentInlineDialog by remember { mutableStateOf<Int?>(null) }

                val lazyListState = rememberLazyListState()
                if (editingLines.isEmpty()) {
                    Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val hapticFeedback = LocalHapticFeedback.current
                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        editingLines.moveElement(from.index, to.index)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    }
                    val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(
                            editingLines,
                            key = { _, keyed -> keyed.key }
                        ) { index, (key, component) ->
                            ReorderableItem(reorderableLazyListState, key) { isDragging ->
                                val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                val handleInteraction = remember { MutableInteractionSource() }
                                val handleHovered by handleInteraction.collectIsHoveredAsState()
                                Card(
                                    modifier = Modifier.fillMaxWidth().scale(scale)
                                ) {
                                    Row(
                                        Modifier.padding(12.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            DragHandle(
                                                hapticFeedback,
                                                handleInteraction,
                                                handleHovered,
                                                isDragging
                                            )
                                            Text(component)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton({
                                                if (isQuickAction)
                                                    editingComponentInlineDialog = index
                                                else
                                                    editingComponent = index
                                            }, Modifier.plainTooltip {
                                                Text(IGLang.Misc.edit)
                                            }) {
                                                Icon(Icons.EditNote, null)
                                            }

                                            RemoveConfirmButton(
                                                component.plainText,
                                                { editingLines.removeAt(index) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(lazyListState),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }

                FloatingActionButton(
                    onClick = {
                        if (isQuickAction)
                            editingComponentInlineDialog = -1
                        else
                            editingComponent = -1
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .fabVisibilityAnimation(
                            rememberFabVisibilityByScroll(lazyListState),
                            shouldHide = rememberHideActionState() || editingLines.size >= maxSize
                        )
                ) {
                    Icon(Icons.Add, IGLang.Misc.add.plainText)
                }

                fun value(idx: Int): Component = editingLines.getOrNull(idx)?.value ?: Texts.literal("")

                editingComponent?.let { idx ->
                    RichTextEditorDialog(
                        value(idx),
                        {
                            if (idx != -1)
                                editingLines[idx] = editingLines[idx].copy(value = it)
                            else
                                editingLines.add(Keyed(nextKey++, it))
                        },
                        title
                    ) { editingComponent = null }
                }

                editingComponentInlineDialog?.let { idx ->
                    InlineStyleTextEditorDialog(
                        value(idx),
                        {
                            if (idx != -1)
                                editingLines[idx] = editingLines[idx].copy(value = it)
                            else
                                editingLines.add(Keyed(nextKey++, it))
                        },
                        title
                    ) { editingComponentInlineDialog = null }
                }

            }
        }
    )
}