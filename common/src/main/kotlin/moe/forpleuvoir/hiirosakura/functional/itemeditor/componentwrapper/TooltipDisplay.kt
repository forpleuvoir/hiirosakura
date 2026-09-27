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
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.DataComponentTypeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.hiirosakura.util.asText
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.TooltipDisplay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.HorizontalDivider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

@Composable
fun TooltipDisplayComponentWrapper(
    key: Identifier,
    value: TooltipDisplay,
    onValueChange: (TooltipDisplay) -> Unit,
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
                .tooltip {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(key, suffix = "hide_tooltip", fallback = "HideTooltip")
                            Spacer(Modifier.width(12.dp))
                            Text(value.hideTooltip.toString())
                        }
                        HorizontalDivider()
                        if (value.hiddenComponents.isEmpty()) {
                            Text(component = IGLang.Misc.hasNothing)
                        } else {
                            value.hiddenComponents.take(10).forEach { component ->
                                Text(component.keyOrUnknown(registryAccess!!))
                            }

                            if (value.hiddenComponents.size > 10) Text("...")
                        }
                    }
                }
                .width(DataComponentEditorDefaults.entrySize.width),
        ) {
Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.hiddenComponents.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
IconButton(onClick = {
    showDialog = true
}) {
    Icon(Icons.Edit)
}
        }
if (showDialog) {
            TooltipDisplayEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                title = { Text(key) },
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

@Composable
fun TooltipDisplayEditorDialog(
    key: Identifier,
    value: TooltipDisplay,
    onValueChange: (TooltipDisplay) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var enabled by remember { mutableStateOf(value.hideTooltip) }

    val list = rememberKeyedList(value.hiddenComponents.toList())

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.padding(24.dp).width(900.dp).height(760.dp),
        title = title,
        onConfirmRequest = {
            onValueChange(TooltipDisplay(enabled, ReferenceLinkedOpenHashSet(list.entries.values())))
            true
        },
        content = {
            Column {
                val availableComponents = BuiltInRegistries.DATA_COMPONENT_TYPE.toList() - list.entries.values().toSet()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(key, suffix = "hide_tooltip", fallback = "HideTooltip")
                        Spacer(Modifier.width(8.dp))
                        Switch(enabled, { enabled = it })
                    }


                    var type by remember(availableComponents) { mutableStateOf(availableComponents.first()) }
                    DataComponentTypeSelector(
                        type,
                        {
                            type = it
                            list.add(it)
                        },
                        label = { Text(component = HSLang.ItemEditor.addItemComponent) },
                        content = {
                            Text(it.keyOrUnknown.toString(), overflow = TextOverflow.Ellipsis, maxLines = 1)
                        },
                        items = availableComponents,
                        modifier = Modifier.fillMaxWidth(0.75f),
                        searchFilter = { str, type ->
                            str in type.keyOrUnknown(registryAccess!!).toString() || str in type.keyOrUnknown(registryAccess!!).asTranslateText().plainText
                        }
                    )
                }
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val lazyListState = rememberLazyListState()

                    if (list.entries.isEmpty()) {
                        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val hapticFeedback = LocalHapticFeedback.current
                        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                            list.move(from.index, to.index)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                        }

                        val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                            state = lazyListState
                        ) {
                            itemsIndexed(list.entries,
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
                                            Modifier.padding(12.dp).fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            ) {
                                                DragHandle(modifier = Modifier)
                                                DataComponentTypeSelector(
                                                    component,
                                                    {
                                                        list.setValue(index, it)
                                                    },
                                                    items = listOf(component) + availableComponents,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    searchFilter = { str, type ->
                                                        str in type.keyOrUnknown(registryAccess!!).toString() || str in type.keyOrUnknown(registryAccess!!)
                                                            .asTranslateText().plainText
                                                    }
                                                )
                                            }


                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {

                                                RemoveConfirmButton(
                                                    component.keyOrUnknown(registryAccess!!).asText().plainText,
                                                    onConfirm = { list.removeAt(index) },
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

                }
            }
        }
    )


}
