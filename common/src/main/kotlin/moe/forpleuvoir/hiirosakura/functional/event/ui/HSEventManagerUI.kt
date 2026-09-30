package moe.forpleuvoir.hiirosakura.functional.event.ui

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.event.EventTypes
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManager
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.ui.HSUiDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.PagePanel
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/** 事件列表的排版常量。 */
private object EventRowDefaults {

    /** 事件类型选择器宽度。 */
    val SelectorWidth: Dp = 360.dp

    /** 行内边距。 */
    val RowPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /** 行内元素间距。 */
    val ItemSpacing: Dp = 8.dp

    /** 行尾操作按钮之间的间距。 */
    val ActionSpacing: Dp = 16.dp

    /** 相邻两行的间距。 */
    val RowSpacing: Dp = 2.dp
}

/** 事件类型过滤项：全部 + 各事件类型 id。 */
private val filterList by lazy {
    buildList {
        add("all")
        addAll(EventTypes.ids)
    }
}

/**
 * 事件订阅管理器：顶部一行筛选与订阅入口 + 订阅列表。
 *
 * 内容整体坐在一张内嵌面板上（与配置页同款骨架），列表与滚动条并列、行悬停走高亮底。
 */
@Composable
fun HSEventManagerUI(
    modifier: Modifier = Modifier,
) {
    var filteredType by remember { mutableStateOf("all") }

    var editingSubscriber by remember { mutableStateOf<Int?>(null) }

    PagePanel(
        modifier = modifier.fillMaxSize().padding(ConfigManagerDefaults.ContentPadding),
        toolbar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EventTypeSelector(
                    filteredType,
                    { filteredType = it },
                    items = filterList,
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filter, scale = HSUiDefaults.ICON_SCALE)
                            Spacer(Modifier.width(EventRowDefaults.ItemSpacing))
                            Text(EventTypes.id2Text(it), modifier = Modifier.tooltip {
                                Text(EventTypes.id2TextComment(it))
                            })
                        }
                    },
                    modifier = Modifier.width(EventRowDefaults.SelectorWidth),
                    contentPadding = PaddingValues(0.dp),
                )

                Button(onClick = { editingSubscriber = -1 }) {
                    Icon(Icons.Add)
                    Spacer(Modifier.width(EventRowDefaults.ItemSpacing))
                    Text(component = HSLang.Event.subscribe)
                }
            }
        },
    ) {
        if (HSEventManager.subscribers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                HSEventManager.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }

            Row(Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(EventRowDefaults.RowSpacing),
                ) {
                    itemsIndexed(
                        HSEventManager.subscribers,
                        key = { _, keyed -> keyed.key }
                    ) { index, (key, subscriber) ->
                        if (filteredType == "all" || subscriber.eventTypeId == filteredType) {
                            ReorderableItem(
                                reorderableLazyListState, key,
                                animateItemModifier = hsItemAnimation(),
                            ) {
                                EventSubscriberRow(
                                    subscriber = subscriber,
                                    // 拖拽手势只能在 ReorderableItem 的作用域里取，因此在调用点算好再传进去
                                    dragHandleModifier = Modifier.draggableHandle(),
                                    onEdit = { editingSubscriber = index },
                                    onRemove = { HSEventManager.remove(index) },
                                )
                            }
                        }
                    }
                }

                ScrollbarColumn(lazyListState)
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

/**
 * 单条订阅：拖拽手柄、事件类型、名称在左，启用开关与操作按钮在右。
 *
 * 行不画容器，悬停时由 [hoverHighlight] 铺一层高亮（与配置行、任务行同款反馈）。
 *
 * @param dragHandleModifier 拖拽手势修饰符，由调用方在 [ReorderableItem] 作用域内取好后传入
 */
@Composable
private fun EventSubscriberRow(
    subscriber: HSEventSubscriber,
    dragHandleModifier: Modifier,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val iconScale = LocalSokitsuPixelScale.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(Modifier.fillMaxWidth().hoverHighlight(interactionSource)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .padding(EventRowDefaults.RowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EventRowDefaults.ItemSpacing),
        ) {
            DragHandle(
                modifier = dragHandleModifier,
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            )

            Text(
                EventTypes.id2Text(subscriber.eventTypeId),
                color = SokitsuTheme.colorScheme.primary,
                maxLines = 1,
            )

            Icon(Icons.Forward, scale = HSUiDefaults.ICON_SCALE)

            Text(
                component = InlineStyleText(subscriber.name),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            var enabled by remember { mutableStateOf(subscriber.enabled) }
            LaunchedEffect(enabled) {
                subscriber.enabled = enabled
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(EventRowDefaults.ActionSpacing),
            ) {
                Switch(enabled, onCheckedChange = { enabled = it }, Modifier.tooltip {
                    Text(component = HSLang.Common.enable)
                })

                IconButton(onEdit, Modifier.tooltip {
                    Text(component = HSLang.Event.subscriberEditor)
                }) {
                    Icon(Icons.Edit, scale = iconScale)
                }

                RemoveConfirmButton(
                    message = subscriber.name,
                    onConfirm = onRemove,
                    iconScale = iconScale,
                    contentPadding = EditDialogContentDefaults.iconPadding,
                )
            }
        }
    }
}
