package moe.forpleuvoir.hiirosakura.ui.configwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogTitle
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigListRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringValueField
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asDerivedState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentList
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.config.item.ConfigList

/*
 * 字符串列表的配置行与编辑浮层。
 *
 * 行与浮层走 IG 的列表骨架（`ConfigListRow` / `ConfigDialogDefaults` / `EditDialogContent(List)`），
 * 元素控件用 IG 的 [StringValueField]；新增走单独的输入浮层，初值由调用方在浮层里填。
 */

/** 新增浮层里「标签 + 编辑器」两列的排版常量。 */
private object StringListAddDialogDefaults {

    /** 标签列与编辑器之间的水平间距。 */
    val LabelGap: Dp = 8.dp

    /** 标签列左侧留白：标签不贴浮层边缘。 */
    val LabelStartPadding: Dp = 8.dp

    /** 标签列占行宽的权重。 */
    const val LabelWeight: Float = 0.32f

    /** 编辑器列占行宽的权重。 */
    const val EditorWeight: Float = 0.68f
}

/**
 * 字符串列表（如聊天过滤表达式）的配置行与编辑浮层。
 *
 * @param config 字符串列表配置项
 * @param modifier 作用于配置页上那一行
 * @param addContentLabel 新增浮层的输入框标签
 * @param contentHeader 内容列表头
 */
@Composable
fun StringListConfigWrapper(
    config: ConfigList<String>,
    modifier: Modifier = Modifier,
    addContentLabel: @Composable () -> Unit = {},
    contentHeader: @Composable () -> Unit = { Text(component = config.translateText) },
) {
    var editing by remember(config) { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.listConfigWrapperText(size), modifier) { editing = true }

    if (editing) {
        val keyed = rememberKeyedList(config.getValue(), key = config)
        var showAddDialog by remember { mutableStateOf(false) }

        FlexibleDialog(
            onDismissRequest = { editing = false },
            onConfirmRequest = {
                config.setValue(keyed.entries.values())
                true
            },
            title = { ConfigDialogTitle(config) },
            minWidth = ConfigDialogDefaults.MinWidth,
            maxHeight = ConfigDialogDefaults.MaxHeight,
            content = {
                EditDialogContent(
                    modifier = Modifier.width(ConfigDialogDefaults.ContentWidth),
                    // 表头交给表格自己（列宽与单元格天然对齐），这里不再叠一层
                    header = {},
                    addButton = {
                        Button(
                            onClick = { showAddDialog = true },
                            contentPadding = ConfigControlDefaults.IconButtonPadding,
                        ) {
                            Icon(Icons.Add, scale = configIconScale())
                        }
                    },
                ) { listState ->
                    EditDialogContentList(
                        state = keyed,
                        lazyListState = listState,
                        removeButton = { index, text ->
                            RemoveConfirmButton(
                                message = text,
                                onConfirm = { keyed.removeAt(index) },
                                iconScale = LocalIconScale.current,
                                contentPadding = EditDialogContentDefaults.iconPadding,
                            )
                        },
                        columns = {
                            column(
                                width = ConfigDialogDefaults.FillColumnWidth,
                                alignment = Alignment.CenterStart,
                                header = { contentHeader() },
                            ) { index, entry ->
                                StringValueField(
                                    value = entry.value,
                                    onValueChange = { keyed.setValue(index, it) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        },
                    )
                }
            },
        )

        if (showAddDialog) {
            SingleLineAddDialog(
                label = addContentLabel,
                onDismissRequest = { showAddDialog = false },
                onConfirm = {
                    keyed.add(it)
                    showAddDialog = false
                },
            )
        }
    }
}

/**
 * 字符串对列表（如聊天注入的 表达式 → 替换内容）的配置行与编辑浮层。
 *
 * @param config 字符串对列表配置项
 * @param modifier 作用于配置页上那一行
 * @param firstHead 前项列表头
 * @param addFirstLabel 新增浮层的前项标签
 * @param secondHead 后项列表头
 * @param addSecondLabel 新增浮层的后项标签
 */
@Composable
fun StringPairListConfigWrapper(
    config: ConfigList<Pair<String, String>>,
    modifier: Modifier = Modifier,
    firstHead: @Composable () -> Unit,
    addFirstLabel: @Composable () -> Unit,
    secondHead: @Composable () -> Unit,
    addSecondLabel: @Composable () -> Unit,
) {
    var editing by remember(config) { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.listConfigWrapperText(size), modifier) { editing = true }

    if (editing) {
        val keyed = rememberKeyedList(config.getValue(), key = config)
        var showAddDialog by remember { mutableStateOf(false) }

        FlexibleDialog(
            onDismissRequest = { editing = false },
            onConfirmRequest = {
                config.setValue(keyed.entries.values())
                true
            },
            title = { ConfigDialogTitle(config) },
            minWidth = ConfigDialogDefaults.MinWidth,
            maxHeight = ConfigDialogDefaults.MaxHeight,
            content = {
                EditDialogContent(
                    modifier = Modifier.width(ConfigDialogDefaults.ContentWidth),
                    // 表头交给表格自己（列宽与单元格天然对齐），这里不再叠一层
                    header = {},
                    addButton = {
                        Button(
                            onClick = { showAddDialog = true },
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
                                width = ConfigDialogDefaults.FillColumnWidth,
                                alignment = Alignment.CenterStart,
                                header = { firstHead() },
                            ) { index, entry ->
                                StringValueField(
                                    value = entry.value.first,
                                    onValueChange = { keyed.setValue(index, entry.value.copy(first = it)) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            column(
                                width = ConfigDialogDefaults.FillColumnWidth,
                                alignment = Alignment.CenterStart,
                                header = { secondHead() },
                            ) { index, entry ->
                                StringValueField(
                                    value = entry.value.second,
                                    onValueChange = { keyed.setValue(index, entry.value.copy(second = it)) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        },
                    )
                }
            },
        )

        if (showAddDialog) {
            PairAddDialog(
                firstLabel = addFirstLabel,
                secondLabel = addSecondLabel,
                onDismissRequest = { showAddDialog = false },
                onConfirm = {
                    keyed.add(it)
                    showAddDialog = false
                },
            )
        }
    }
}

/** 单行新增浮层：输入一条内容后交给调用方追加。 */
@Composable
private fun SingleLineAddDialog(
    label: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.add) },
        text = {
            StringListAddDialogRow(label = label) {
                StringValueField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text(component = IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = IGLang.Misc.cancel)
            }
        },
    )
}

/** 字符串对新增浮层：前后两项各一行「标签 + 输入框」。 */
@Composable
private fun PairAddDialog(
    firstLabel: @Composable () -> Unit,
    secondLabel: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: (Pair<String, String>) -> Unit,
) {
    var first by remember { mutableStateOf("") }
    var second by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.add) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StringListAddDialogRow(label = firstLabel) {
                    StringValueField(value = first, onValueChange = { first = it }, modifier = Modifier.fillMaxWidth())
                }
                StringListAddDialogRow(label = secondLabel) {
                    StringValueField(value = second, onValueChange = { second = it }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(first to second) }) {
                Text(component = IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = IGLang.Misc.cancel)
            }
        },
    )
}

/** 新增浮层里的一行「标签 + 编辑器」：标签列窄、编辑器列等宽。 */
@Composable
private fun StringListAddDialogRow(
    label: @Composable () -> Unit,
    content: @Composable () -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(StringListAddDialogDefaults.LabelGap),
    verticalAlignment = Alignment.CenterVertically,
) {
    Box(
        modifier = Modifier
            .weight(StringListAddDialogDefaults.LabelWeight)
            .padding(start = StringListAddDialogDefaults.LabelStartPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        label()
    }
    Box(modifier = Modifier.weight(StringListAddDialogDefaults.EditorWeight)) {
        content()
    }
}
