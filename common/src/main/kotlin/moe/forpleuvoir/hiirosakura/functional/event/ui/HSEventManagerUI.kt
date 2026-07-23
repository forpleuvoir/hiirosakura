package moe.forpleuvoir.hiirosakura.functional.event.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.event.EventTypes
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.ui.icon.default.FilterList
import moe.forpleuvoir.hiirosakura.ui.icon.default.Link
import moe.forpleuvoir.hiirosakura.ui.icon.default.Link2
import moe.forpleuvoir.hiirosakura.ui.icon.default.NotificationAdd
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.DragHandle
import moe.forpleuvoir.ibukigourd.ui.preset.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import org.spongepowered.asm.util.IConsumer
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
                        Icon(Icons.FilterList, null)
                        Spacer(Modifier.width(8.dp))
                        Text(EventTypes.id2Text(it), modifier = Modifier.plainTooltip {
                            Text(EventTypes.id2TextComment(it))
                        })
                    }
                },
                modifier = Modifier.width(360.dp)
            )
            Button(onClick = {
                editingSubscriber = -1
            }) {
                Icon(Icons.NotificationAdd, contentDescription = "")
                Spacer(Modifier.width(8.dp))
                Text(HSLang.Event.subscribe)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (HSEventManager.subscribers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                HSEventManager.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize(), state = lazyListState) {
                itemsIndexed(
                    HSEventManager.subscribers,
                    key = { _, keyed -> keyed.key }
                ) { index, (key, subscriber) ->
                    if (filteredType == "all" || subscriber.eventTypeId == filteredType) {
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
                                        Text(
                                            Texts.translatable("${HiiroSakura.MOD_ID}.event.${subscriber.eventTypeId}"),
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Icon(Icons.Link, null)
                                        Text(InlineStyleText(subscriber.name))
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        var enabled by remember { mutableStateOf(subscriber.enabled) }
                                        LaunchedEffect(enabled) {
                                            subscriber.enabled = enabled
                                        }
                                        Switch(enabled, onCheckedChange = { enabled = it }, Modifier.plainTooltip {
                                            Text(HSLang.Common.enable)
                                        })

                                        IconButton({
                                            editingSubscriber = index
                                        }, Modifier.plainTooltip {
                                            Text(HSLang.Event.subscriberEditor)
                                        }) {
                                            Icon(Icons.EditNote, null)
                                        }

                                        RemoveConfirmButton(
                                            subscriber.name,
                                            { HSEventManager.remove(index) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
                if (idx == -1) Text(HSLang.Event.subscribe)
                else Text(HSLang.Event.subscriberEditor)
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