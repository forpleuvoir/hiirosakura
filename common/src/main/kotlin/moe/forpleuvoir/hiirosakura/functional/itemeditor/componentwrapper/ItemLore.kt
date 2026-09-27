package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberHideActionState
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ItemLore
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

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

        FlatButton(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .width(DataComponentEditorDefaults.entrySize.width)
                .vanillaTooltip(value.styledLines),
        ) {
Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.styledLines().size), overflow = TextOverflow.Ellipsis, maxLines = 1)
IconButton(onClick = {
    showDialog = true
}) {
    Icon(Icons.Edit)
}
        }
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

    val maxSize = ItemLore.MAX_LINES
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier
            .padding(24.dp)
            .size(1000.dp, 720.dp),
        onConfirmRequest = {
            onValueChange(ItemLore(editingLines.entries.values().take(maxSize).toMutableList()))
            true
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                var editingComponent by remember { mutableStateOf<Int?>(null) }

                var editingComponentInlineDialog by remember { mutableStateOf<Int?>(null) }


                val lazyListState = rememberLazyListState()
                if (editingLines.entries.isEmpty()) {
                    Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                } else {
                    val hapticFeedback = LocalHapticFeedback.current
                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        editingLines.move(from.index, to.index)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = if (lazyListState.canScroll) 12.dp else 0.dp).fillMaxSize(),
                        state = lazyListState
                    ) {
                        itemsIndexed(editingLines.entries,
                            key = { _, keyed -> keyed.key }
                        ) { index, (key, component) ->
                            ReorderableItem(
                                reorderableLazyListState, key,
                                animateItemModifier = hsItemAnimation(),
                            ) { isDragging ->
                                val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                val handleInteraction = remember { MutableInteractionSource() }

                                val handleHovered by handleInteraction.collectIsHoveredAsState()
                                Surface(
                                    modifier = Modifier.fillMaxWidth().scale(scale)
                                ) {
                                    Row(
                                        Modifier.padding(4.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            DragHandle(modifier = Modifier)
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
                                            }, Modifier.tooltip {
                                                Text(component = IGLang.Misc.edit)
                                            }) {
                                                Icon(Icons.Edit)
                                            }

                                            RemoveConfirmButton(
                                                component.plainText,
                                                onConfirm = { { editingLines.removeAt(index) } },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    VerticalScroller(
                        adapter = rememberScrollerAdapter(lazyListState),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }

                Button(
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
                        .fabScrollVisibility(
                            rememberFabScrollVisibility(lazyListState),
                        ),
                ) {
                    Icon(Icons.Add)
                }
                fun value(idx: Int): Component = editingLines.entries.getOrNull(idx)?.value ?: Texts.literal("")

                editingComponent?.let { idx ->
                    RichTextEditorDialog(
                        value(idx),
                        {
                            if (idx != -1)
                                editingLines.setValue(idx, it)
                            else
                                editingLines.add(it)
                        },
                        title
                    ) { editingComponent = null }
                }

                editingComponentInlineDialog?.let { idx ->
                    InlineStyleTextEditorDialog(
                        value(idx),
                        {
                            if (idx != -1)
                                editingLines.setValue(idx, it)
                            else
                                editingLines.add(it)
                        },
                        title
                    ) { editingComponentInlineDialog = null }
                }

            }
        }
    )
}
