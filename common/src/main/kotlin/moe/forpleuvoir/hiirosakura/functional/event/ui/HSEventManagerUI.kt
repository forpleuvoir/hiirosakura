package moe.forpleuvoir.hiirosakura.functional.event.ui

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
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.event.EventTypes
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.hiirosakura.ui.icon.HSIcons
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.FilterList
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Link
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.NotificationAdd
import moe.forpleuvoir.hiirosakura.ui.compat.FilledTonalButton

private val filterList by lazy {
    buildList {
        add("all")
        addAll(EventTypes.ids)
    }
}

@Composable
fun HSEventManagerUI(
    modifier: Modifier = Modifier,
) {
    var filteredType by remember { mutableStateOf("all") }

    var editingSubscriber by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier.fillMaxSize().padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            EventTypeSelector(
                filteredType,
                { filteredType = it },
                items = filterList,
                content = {
                    Row {
                        VectorIcon(HSIcons.FilterList)
                        Spacer(Modifier.width(8.dp))
                        Text(EventTypes.id2Text(it), modifier = Modifier.tooltip {
                            Text(EventTypes.id2TextComment(it))
                        })
                    }
                },
                modifier = Modifier.width(360.dp)
            )
            FilledTonalButton(onClick = {
                editingSubscriber = -1
            }) {
                VectorIcon(HSIcons.NotificationAdd, contentDescription = "")
                Spacer(Modifier.width(8.dp))
                Text(component = HSLang.Event.subscribe)
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (HSEventManager.subscribers.isEmpty()) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            } else {
                val lazyListState = rememberLazyListState()
                val hapticFeedback = LocalHapticFeedback.current
                val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    HSEventManager.moveElement(from.index, to.index)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }

                val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                    state = lazyListState
                ) {
                    itemsIndexed(
                        HSEventManager.subscribers,
                        key = { _, keyed -> keyed.key }
                    ) { index, (key, subscriber) ->
                        if (filteredType == "all" || subscriber.eventTypeId == filteredType) {
                            ReorderableItem(reorderableLazyListState, key) { isDragging ->
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
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            DragHandle(modifier = Modifier)
                                            Text(
                                                EventTypes.id2Text(subscriber.eventTypeId),
                                                color = SokitsuTheme.colorScheme.primary
                                            )
                                            VectorIcon(HSIcons.Link)
                                            Text(component = InlineStyleText(subscriber.name))
                                        }


                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            var enabled by remember { mutableStateOf(subscriber.enabled) }
                                            LaunchedEffect(enabled) {
                                                subscriber.enabled = enabled
                                            }
                                            Switch(enabled, onCheckedChange = { enabled = it }, Modifier.tooltip {
                                                Text(component = HSLang.Common.enable)
                                            })

                                            IconButton({
                                                editingSubscriber = index
                                            }, Modifier.tooltip {
                                                Text(component = HSLang.Event.subscriberEditor)
                                            }) {
                                                Icon(Icons.Edit)
                                            }

                                            RemoveConfirmButton(
                                                subscriber.name,
                                                onConfirm = { HSEventManager.remove(index) },
                                            )
                                        }
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

    editingSubscriber?.let { idx ->
        HSEventSubscriberEditorDialog(
            if (idx == -1) HSEventSubscriber(
                "",
                true,
                if (filteredType != "all") filteredType else EventTypes.ids.first(),
                ExecutorType.Script,
                ScriptExecutor("")
            )
            else HSEventManager.subscribers[idx].value,
            {
                if (idx == -1) Text(component = HSLang.Event.subscribe)
                else Text(component = HSLang.Event.subscriberEditor)
            },
            { editingSubscriber = null },
            {
                if (idx == -1) {
                    HSEventManager.add(it)
                } else {
                    HSEventManager[idx] = it
                }
            }
        )
    }
}