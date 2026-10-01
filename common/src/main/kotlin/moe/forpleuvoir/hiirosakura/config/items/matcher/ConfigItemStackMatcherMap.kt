package moe.forpleuvoir.hiirosakura.config.items.matcher

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.InlineEditField
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.*
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentList
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap

context(group: ConfigGroup)
fun configItemStackMatcherMap(name: String, defaultValue: Map<String, ItemStackMatcher>) =
    configMap(name, defaultValue, ItemStackMatcher)

//------------ UI Wrapper ------------\\

/**
 * 物品匹配器映射：行上显示条目数，点开在浮层里编辑（键值对增删 + 拖拽排序）。
 *
 * 浮层里是一张两列表格：键列只做展示、改键走框内右端的编辑按钮，值列用 [ItemStackMatcherDisplayerInnerEditor]。
 * 新增条目取一个不冲突的默认键与当前手持物品匹配器；编辑在副本上进行，确认时才整体写回配置。
 *
 * @param config 映射配置项
 * @param modifier 作用于配置页上那一行
 * @param keyHeader 键列表头
 * @param valueHeader 值列表头
 */
@Composable
fun ItemStackMatcherMapConfigWrapper(
    config: ConfigMap<ItemStackMatcher>,
    modifier: Modifier = Modifier,
    keyHeader: @Composable TableCellScope.() -> Unit = { Text(component = IGLang.ConfigWrapper.mapKey) },
    valueHeader: @Composable TableCellScope.() -> Unit = { Text(component = HSLang.ItemStackMatcher.title) },
) {
    var editing by remember(config) { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.mapConfigWrapperText(size), modifier) { editing = true }

    if (editing) {
        ItemStackMatcherMapEditDialog(
            config = config,
            keyHeader = keyHeader,
            valueHeader = valueHeader,
            onDismiss = { editing = false },
        )
    }
}

/** 映射编辑浮层：键 / 值两列 + 增删 + 拖拽排序；条目顺序按 `LinkedHashMap` 保留。 */
@Composable
private fun ItemStackMatcherMapEditDialog(
    config: ConfigMap<ItemStackMatcher>,
    keyHeader: @Composable TableCellScope.() -> Unit,
    valueHeader: @Composable TableCellScope.() -> Unit,
    onDismiss: () -> Unit,
) {
    val keyed = rememberKeyedList(config.getValue().entries.map { it.key to it.value }, key = config)
    // 正在改键的条目：存条目 key 而不是下标 —— 拖拽重排后下标会变，key 不会
    var editingKey by remember { mutableStateOf<Long?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            config.setValue(keyed.entries.values().toMap())
            true
        },
        title = { ConfigDialogTitle(config) },
        minWidth = ConfigDialogDefaults.MinWidth,
        maxHeight = ConfigDialogDefaults.MaxHeight,
        content = {
            EditDialogContent(
                modifier = Modifier.width(ConfigDialogDefaults.ContentWidth),
                // 表头交给表格自己（键 / 值两列由表格保证跨行对齐），这里不再叠一层
                header = {},
                addButton = {
                    Button(
                        onClick = {
                            keyed.add(uniqueKey(keyed.entries.values()) to ItemStackMatcher.handheldItemMatcher)
                        },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                },
            ) { listState ->
                EditDialogContentList(
                    state = keyed,
                    lazyListState = listState,
                    removeButton = { index, entry ->
                        RemoveConfirmButton(
                            message = entry.first,
                            onConfirm = { keyed.removeAt(index) },
                            iconScale = LocalIconScale.current,
                            contentPadding = EditDialogContentDefaults.iconPadding,
                        )
                    },
                    columns = {
                        column(
                            width = ConfigDialogDefaults.KeyColumnWidth,
                            header = keyHeader,
                        ) { _, entry ->
                            // 键只做展示，改键走框内右端的编辑按钮：行内输入没法在提交时拦住重复键
                            InlineEditField(
                                onEdit = { editingKey = entry.key },
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(entry.value.first, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        column(
                            width = ConfigDialogDefaults.FillColumnWidth,
                            alignment = Alignment.CenterStart,
                            header = valueHeader,
                        ) { index, entry ->
                            ItemStackMatcherDisplayerInnerEditor(
                                value = entry.value.second,
                                onValueChange = { keyed.setValue(index, entry.value.first to it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                )

                // 键浮层叠在编辑浮层之上；条目被删掉（key 找不到）时直接不组合
                val target = editingKey
                val index = target?.let { key -> keyed.entries.indexOfFirst { it.key == key } } ?: -1
                if (target != null && index >= 0) {
                    MapKeyEditDialog(
                        initial = keyed.entries[index].value.first,
                        isTaken = { candidate ->
                            keyed.entries.any { it.key != target && it.value.first == candidate }
                        },
                        onDismiss = { editingKey = null },
                        onConfirm = { newKey ->
                            keyed.setValue(index, newKey to keyed.entries[index].value.second)
                            editingKey = null
                        },
                    )
                }
            }
        },
    )
}

/**
 * 键编辑浮层：确认时校验新键是否与**其它**条目撞车。
 *
 * 撞车时输入框转错误态、并把提示钉在输入框上；改动文本即清掉错误。
 *
 * @param initial 当前键
 * @param isTaken 该键是否已被其它条目占用
 * @param onDismiss 关闭浮层
 * @param onConfirm 接受新键
 */
@Composable
private fun MapKeyEditDialog(
    initial: String,
    isTaken: (String) -> Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var key by remember { mutableStateOf(initial) }
    var showError by remember { mutableStateOf(false) }
    val taken = isTaken(key)

    FlexibleDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            if (taken) {
                showError = true
                false
            } else {
                onConfirm(key)
                true
            }
        },
        title = { Text(component = IGLang.ConfigWrapper.mapKey) },
        content = {
            StringValueField(
                value = key,
                onValueChange = {
                    key = it
                    showError = false
                },
                modifier = Modifier
                    .width(ConfigControlDefaults.ControlWidth)
                    .tooltip(pinned = showError) { Text(component = IGLang.ConfigWrapper.keyExists(key)) },
                isError = taken,
            )
        },
    )
}

/** 生成一个当前不冲突的默认键（`key1`、`key2`…）。 */
private fun uniqueKey(existing: List<Pair<String, ItemStackMatcher>>): String {
    val used = existing.mapTo(mutableSetOf()) { it.first }
    var index = 1
    while ("key$index" in used) index++
    return "key$index"
}
