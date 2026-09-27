package moe.forpleuvoir.hiirosakura.ui.configwrapper

/*
 * 旧版 IbukiGourd（0.11.1）的 `ui.configwrapper.{ListConfigWrapperDefaults,MapConfigWrapperDefaults}`
 * 在本项目的兼容层。
 *
 * 新版上游把配置 GUI 重写为 `ConfigListWrapper` / `ConfigMapWrapper`，内建浮层的元素控件固定走
 * `ConfigElementEditor`（internal、按运行时类型分发），不再提供「自定义元素控件」的注入点与
 * `*ConfigWrapperDefaults` 这套浮层骨架。本项目的 7 个自定义配置项（匹配器、连锁开门、自动补种、
 * 音效、聊天气泡…）依赖这套骨架来塞自己的编辑器，因此在这里按旧签名重建：**状态语义与旧版一致**
 * （编辑副本、确认时写回），渲染换成 sokitsu 组件。
 *
 * 与上游的差异：分段按钮/浮动按钮/图标等换成 sokitsu 对应物；列宽等仍走组合本地量。
 */

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asDerivedState
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.HorizontalDivider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.ProvideTextStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.util.moveElement
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.ConfigMap
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.window.DialogProperties

/** 旧版 `ConfigRowWrapper` 的几何量（新版已移除，见 `ui/configwrapper/ConfigRowWrapper.kt`）。 */
object ConfigRowWrapperCompat {
    val entrySize: DpSize = DpSize(220.dp, 40.dp)
    val spacing: Dp = 8.dp
}




/**
 * 旧版 `rememberKeyedList` 的等价物：返回 `SnapshotStateList<Keyed<T>>`。
 *
 * 新版上游把它改成了返回 `KeyedListState<T>`，旧调用点（`data[i] = x` / `add(Keyed(...))` / `forEach`）
 * 依赖列表语义，这里按旧行为提供。
 */
@Composable
fun <T> rememberKeyedStateList(list: List<T>): SnapshotStateList<Keyed<T>> = remember(list) {
    mutableStateListOf<Keyed<T>>().apply {
        var nextKey = 0L
        list.forEach { add(Keyed(nextKey++, it)) }
    }
}

object ListConfigWrapperDefaults {

    @Composable
    fun <E : Any> RowWrapper(
        config: ConfigList<E>,
        modifier: Modifier = Modifier,
        editAction: () -> Unit,
    ) = ConfigRowWrapper(config, modifier = modifier) {
        Row(
            modifier = Modifier.size(ConfigRowWrapperCompat.entrySize),
            horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapperCompat.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val displaySize by config.asDerivedState { it.size }
            FlatButton(
                onClick = {},
                modifier = Modifier.weight(1f).height(40.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(component = IGLang.ConfigWrapper.listConfigWrapperText(displaySize), modifier = Modifier.fillMaxWidth())
                }
            }
            IconButton(onClick = editAction) {
                Icon(Icons.Edit)
            }
        }
    }

    @Composable
    fun <E : Any> EditDialog(
        config: ConfigList<E>,
        modifier: Modifier = Modifier,
        onDismissRequest: () -> Unit,
        title: @Composable (() -> Unit)? = { Text(component = InlineStyleText(config.translateText.plainText)) },
        content: @Composable (data: SnapshotStateList<E>) -> Unit
    ) {
        val editingValue = remember { config.toList().toMutableStateList() }
        FlexibleDialog(
            onDismissRequest = onDismissRequest,
            title = title,
            modifier = modifier,
            onConfirmRequest = {
                config.clear()
                editingValue.forEach { config.add(it) }
                true
            },
            content = { content(editingValue) }
        )
    }

    @Composable
    fun <C : Any, E : Any> EditDialog(
        config: ConfigList<C>,
        editingValue: SnapshotStateList<E>,
        modifier: Modifier = Modifier,
        onDismissRequest: () -> Unit,
        title: @Composable (() -> Unit)? = { Text(component = InlineStyleText(config.translateText.plainText)) },
        onConfirmRequest: (data: SnapshotStateList<E>) -> Boolean,
        content: @Composable (data: SnapshotStateList<E>) -> Unit
    ) {
        FlexibleDialog(
            onDismissRequest = onDismissRequest,
            title = title,
            modifier = modifier,
            onConfirmRequest = { onConfirmRequest(editingValue) },
            content = { content(editingValue) }
        )
    }

    @Composable
    fun EditDialogContent(
        modifier: Modifier = Modifier,
        addDialog: @Composable (onDismissRequest: () -> Unit) -> Unit,
        header: @Composable (ColumnScope.() -> Unit) = { EditDialogContentHeader() },
        content: @Composable (ColumnScope.(LazyListState) -> Unit)
    ) = Box(modifier) {
        val lazyListState = rememberLazyListState()
        Column(modifier = Modifier.fillMaxSize()) {
            header()
            content(lazyListState)
        }

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) addDialog { showDialog = false }
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(40.dp)
        ) {
            Icon(Icons.Add)
        }
    }


    val LocalHeaderHeight = staticCompositionLocalOf { 40.dp }


    val LocalColumnSpacing = staticCompositionLocalOf { 8.dp }


    val LocalMoveColumnWidth = staticCompositionLocalOf { 60.dp }


    val LocalRemoveColumnWidth = staticCompositionLocalOf { 60.dp }

    @Composable
    fun RowScope.MoveColumn(
        content: @Composable BoxScope.() -> Unit
    ) = Box(
        Modifier.width(LocalMoveColumnWidth.current),
        contentAlignment = Alignment.Center,
        content = content
    )

    @Composable
    fun RowScope.RemoveColumn(
        content: @Composable BoxScope.() -> Unit
    ) = Box(
        Modifier.width(LocalRemoveColumnWidth.current),
        contentAlignment = Alignment.Center,
        content = content
    )

    @Composable
    fun EditDialogContentHeader(
        modifier: Modifier = Modifier
            .fillMaxWidth()
            .height(LocalHeaderHeight.current)
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        verticalAlignment: Alignment.Vertical = Alignment.Bottom,
        moveHeader: @Composable (RowScope.() -> Unit)? = {
            MoveColumn {
                Text(component = IGLang.ConfigWrapper.move)
            }
        },
        contentHeader: @Composable BoxScope.() -> Unit = {
            Text(component = IGLang.Misc.content)
        },
        removeHeader: @Composable RowScope.() -> Unit = {
            RemoveColumn {
                Text(component = IGLang.Misc.remove)
            }
        },
        divider: @Composable (() -> Unit)? = { HorizontalDivider() }
    ) {
        Row(
            modifier,
            horizontalArrangement,
            verticalAlignment,
        ) {
            ProvideTextStyle(SokitsuTheme.typography.button) {
                moveHeader?.let {
                    it()
                    Spacer(Modifier.width(LocalColumnSpacing.current))
                }

                Box(
                    Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    contentHeader()
                }
                Spacer(Modifier.width(LocalColumnSpacing.current))
                removeHeader()
            }
        }
        divider?.invoke()
    }

    @Composable
    fun <E : Any> EditDialogContentList(
        data: SnapshotStateList<E>,
        key: (E) -> Any,
        modifier: Modifier = Modifier,
        lazyListState: LazyListState = rememberLazyListState(),
        enableElementMove: Boolean = true,
        verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
        horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        entryRowModifier: Modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        element: @Composable RowScope.(value: E, (newValue: E) -> Unit) -> Unit,
    ) {
        Box(modifier = modifier) {
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                data.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 8.dp),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                itemsIndexed(data, key = { _, e -> key(e) }) { index, entry ->
                    ReorderableItem(reorderableLazyListState, key = key(entry)) { isDragging ->
                        val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                        val handleInteraction = remember { MutableInteractionSource() }

                        val handleHovered by handleInteraction.collectIsHoveredAsState()

                        Row(
                            modifier = entryRowModifier.scale(scale),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (enableElementMove) {
                                MoveColumn {
                                    DragHandle(modifier = Modifier)
                                }
                                Spacer(Modifier.width(LocalColumnSpacing.current))
                            }

                            element(entry) {
                                data[index] = it
                            }

                            Spacer(Modifier.width(LocalColumnSpacing.current))

                            RemoveColumn {
                                RemoveConfirmButton(
                                    message = "$index",
                                    onConfirm = { data.removeAt(index) },
                                )
                            }
                        }
                    }
                }
            }

            VerticalScroller(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollerAdapter(lazyListState),
            )
        }
    }
}

data class MapEntry<K, V>(
    val key: K,
    val value: V,
)

private typealias KeyedMapEntry<V> = Keyed<MapEntry<String, V>>

object MapConfigWrapperDefaults {

    @Composable
    fun <V : Any> RowWrapper(
        config: ConfigMap<V>,
        modifier: Modifier = Modifier,
        editAction: () -> Unit,
    ) = ConfigRowWrapper(config, modifier = modifier) {
        Row(
            modifier = Modifier.size(ConfigRowWrapperCompat.entrySize),
            horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapperCompat.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val displaySize by config.asDerivedState { it.size }
            FlatButton(
                onClick = {},
                modifier = Modifier.weight(1f).height(40.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(component = IGLang.ConfigWrapper.mapConfigWrapperText(displaySize), modifier = Modifier.fillMaxWidth())
                }
            }
            IconButton(onClick = editAction) {
                Icon(Icons.Edit)
            }
        }
    }

    @Composable
    fun <V : Any> EditDialog(
        config: ConfigMap<V>,
        modifier: Modifier = Modifier,
        onDismissRequest: () -> Unit,
        title: @Composable (() -> Unit)? = { Text(component = InlineStyleText(config.translateText.plainText)) },
        content: @Composable (data: SnapshotStateList<KeyedMapEntry<V>>) -> Unit
    ) {
        //编辑中的映射 确认之后写入config
        val editingValue = rememberKeyedStateList(config.entries.map { MapEntry(it.key, it.value) })
        FlexibleDialog(
            onDismissRequest = onDismissRequest,
            title = title,
            modifier = modifier,
            onConfirmRequest = {
                config.clear()
                editingValue.forEach { (_, entry) ->
                    val (key, value) = entry
                    config[key] = value
                }
                true
            },
            content = { content(editingValue) }
        )
    }

    @Composable
    fun EditDialogContent(
        modifier: Modifier = Modifier,
        addDialog: @Composable (onDismissRequest: () -> Unit) -> Unit,
        header: @Composable (ColumnScope.() -> Unit) = { EditDialogContentHeader() },
        content: @Composable (ColumnScope.(LazyListState) -> Unit)
    ) = Box(modifier) {
        val lazyListState = rememberLazyListState()
        Column(modifier = Modifier.fillMaxSize()) {
            header()
            content(lazyListState)
        }

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) addDialog { showDialog = false }
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(40.dp)
        ) {
            Icon(Icons.Add)
        }
    }


    val LocalHeaderHeight = staticCompositionLocalOf { 40.dp }


    val LocalKeyColumnWeight = staticCompositionLocalOf { 3f }


    val LocalValueColumnWeight = staticCompositionLocalOf { 5f }


    val LocalColumnSpacing = staticCompositionLocalOf { 8.dp }


    val LocalMoveColumnWidth = staticCompositionLocalOf { 60.dp }


    val LocalRemoveColumnWidth = staticCompositionLocalOf { 60.dp }

    @Composable
    fun RowScope.MoveColumn(
        content: @Composable BoxScope.() -> Unit
    ) = Box(
        Modifier.width(LocalMoveColumnWidth.current),
        contentAlignment = Alignment.Center,
        content = content
    )

    @Composable
    fun RowScope.RemoveColumn(
        content: @Composable BoxScope.() -> Unit
    ) = Box(
        Modifier.width(LocalRemoveColumnWidth.current),
        contentAlignment = Alignment.Center,
        content = content
    )

    @Composable
    fun EditDialogContentHeader(
        modifier: Modifier = Modifier
            .fillMaxWidth()
            .height(LocalHeaderHeight.current)
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        verticalAlignment: Alignment.Vertical = Alignment.Bottom,
        moveHeader: @Composable (RowScope.() -> Unit)? = {
            MoveColumn {
                Text(component = IGLang.ConfigWrapper.move)
            }
        },
        keyHeader: @Composable BoxScope.() -> Unit = {
            Text(component = IGLang.ConfigWrapper.mapKey)
        },
        valueHeader: @Composable BoxScope.() -> Unit = {
            Text(component = IGLang.ConfigWrapper.mapValue)
        },
        removeHeader: @Composable RowScope.() -> Unit = {
            RemoveColumn {
                Text(component = IGLang.Misc.remove)
            }
        },
        divider: @Composable (() -> Unit)? = { HorizontalDivider() }
    ) {
        Row(
            modifier,
            horizontalArrangement,
            verticalAlignment,
        ) {
            ProvideTextStyle(SokitsuTheme.typography.button) {
                //move
                moveHeader?.let {
                    it()
                    Spacer(Modifier.width(LocalColumnSpacing.current))
                }
                //key
                Box(
                    Modifier.weight(LocalKeyColumnWeight.current),
                    contentAlignment = Alignment.Center
                ) {
                    keyHeader()
                }
                Spacer(Modifier.width(LocalColumnSpacing.current))
                //value
                Box(
                    Modifier.weight(LocalValueColumnWeight.current),
                    contentAlignment = Alignment.Center
                ) {
                    valueHeader()
                }
                Spacer(Modifier.width(LocalColumnSpacing.current))
                //remove
                removeHeader()
            }
        }
        divider?.invoke()
    }

    @Composable
    fun <V : Any> EditDialogContentList(
        data: SnapshotStateList<KeyedMapEntry<V>>,
        key: (KeyedMapEntry<V>) -> Any = { it.key },
        modifier: Modifier = Modifier,
        lazyListState: LazyListState = rememberLazyListState(),
        enableElementMove: Boolean = true,
        verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
        horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        entryRowModifier: Modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        keyWrapper: @Composable RowScope.(key: String, (newKey: String) -> Unit) -> Unit = { key, onKeyChange ->
            KeyWrapper(
                key,
                onKeyChange,
                { newKey -> key == newKey || data.any { it.value.key == newKey } },
                modifier = Modifier.weight(LocalKeyColumnWeight.current)
            )
        },
        valueWrapper: @Composable RowScope.(value: V, (newValue: V) -> Unit) -> Unit
    ) {

        Box(
            modifier = modifier,
        ) {
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                data.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 8.dp),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                itemsIndexed(data, key = { _, entry -> key(entry) }) { index, entry ->
                    ReorderableItem(reorderableLazyListState, key = key(entry)) { isDragging ->
                        val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                        val (mapKey, value) = data[index].value
                        val handleInteraction = remember { MutableInteractionSource() }

                        val handleHovered by handleInteraction.collectIsHoveredAsState()

                        Row(
                            modifier = entryRowModifier.scale(scale),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            //移动手柄
                            if (enableElementMove) {
                                MoveColumn {
                                    DragHandle(modifier = Modifier)
                                }
                                Spacer(Modifier.width(LocalColumnSpacing.current))
                            }

                            //Key包装
                            keyWrapper(mapKey) { newKey ->
                                data[index] = Keyed(data[index].key, data[index].value.copy(key = newKey))
                            }
                            Spacer(Modifier.width(LocalColumnSpacing.current))

                            //Value包装
                            valueWrapper(value) { newValue ->
                                data[index] = Keyed(data[index].key, data[index].value.copy(value = newValue))
                            }
                            Spacer(Modifier.width(LocalColumnSpacing.current))
                            //移除按钮
                            RemoveColumn {
                                RemoveConfirmButton(mapKey, { data.removeAt(index) })
                            }
                        }
                    }
                }
            }

            VerticalScroller(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollerAdapter(lazyListState),
            )
        }
    }

    @Composable
    fun KeyWrapper(
        key: String,
        onKeyChange: (newKey: String) -> Unit,
        checkKeyDuplicate: (key: String) -> Boolean,
        keyDisplayer: @Composable (key: String) -> Unit = { Text(key, maxLines = 1) },
        modifier: Modifier = Modifier,
    ) {
        var showKeyEditDialog by remember { mutableStateOf(false) }

        FlatButton(
            onClick = {},
            modifier = modifier,
        ) {
            keyDisplayer(key)
            IconButton(onClick = { showKeyEditDialog = true }) {
                Icon(Icons.Edit)
            }
        }


        if (showKeyEditDialog) {
            val newKey = rememberTextFieldState(key)
            val isDuplicate = remember(newKey.text.toString()) {
                checkKeyDuplicate(newKey.text.toString())
            }
            AlertDialog(
                onDismissRequest = { showKeyEditDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(component = IGLang.Misc.edit)
                        Icon(Icons.ArrowRight)
                        Text(key)
                    }
                },
                text = {
                    OutlinedLabelBox(
                        label = {
                            if (isDuplicate) Text(component = IGLang.ConfigWrapper.keyExists(newKey.text.toString()))
                            else Text(component = IGLang.ConfigWrapper.mapKey)
                        },
                    ) {
                        TextField(
                            state = newKey,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            isError = isDuplicate,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (!isDuplicate) {
                                onKeyChange(newKey.text.toString())
                                showKeyEditDialog = false
                            }
                        },
                        enabled = !isDuplicate,
                    ) {
                        Text(component = IGLang.Misc.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showKeyEditDialog = false }) {
                        Text(component = IGLang.Misc.cancel)
                    }
                },
            )
        }
    }
}
