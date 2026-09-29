package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.InlineEditField
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.config.item.pair
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.*
import moe.forpleuvoir.ibukigourd.ui.editdialog.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.nebula.serialization.codec.Codec

typealias BlockInfoItemStackPair = Pair<BlockInfoMatcher, ItemStackMatcher>

val BlockInfoItemStackPairCodec = Codec.pair(BlockInfoMatcher, ItemStackMatcher)

val BlockInfoItemStackPair.block get() = first
val BlockInfoItemStackPair.item get() = second

context(group: ConfigGroup)
fun configBlockInfoItemStackMap(name: String, defaultValue: Map<String, BlockInfoItemStackPair>) =
    configMap(name, defaultValue, BlockInfoItemStackPairCodec)

//------------ UI Wrapper ------------\\

/**
 * 映射条目卡片的排版常量。
 *
 * 与连锁开门规则卡片同一套口径：卡片宽度不写死，浮层只给上下限，列数按可用宽度算。
 */
private object BlockInfoItemStackPairCardDefaults {

    /** 一张卡片的最小宽度：标签列 + 编辑器列 + 卡片内边距。 */
    val MinCardWidth: Dp = 560.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 2

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 2

    /** 卡片间距，取卡片列表的缺省值。 */
    val CardSpacing: Dp = EditDialogContentDefaults.cardSpacing

    /** 卡片列表右侧细滚动条的占位宽度，算列数时先扣掉。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 卡片内各行之间的纵向间距。 */
    val RowSpacing: Dp = 8.dp

    /** 标签列与编辑器之间的水平间距。 */
    val LabelGap: Dp = 8.dp

    /** 标签列左侧留白：标签不贴卡片边缘。 */
    val LabelStartPadding: Dp = 8.dp

    /** 标签列占行宽的权重（与 [EditorWeight] 配对比；各行一致 ⇒ 编辑器列等宽）。 */
    const val LabelWeight: Float = 0.32f

    /** 编辑器列占行宽的权重。 */
    const val EditorWeight: Float = 0.68f

    /** 浮层宽度下限：一行放得下 [MinColumns] 张卡片。 */
    val DialogMinWidth: Dp
        get() = MinCardWidth * MinColumns + CardSpacing * (MinColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层宽度上限：一行最多 [MaxColumns] 张卡片。 */
    val DialogMaxWidth: Dp
        get() = MinCardWidth * MaxColumns + CardSpacing * (MaxColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层高度上限：可视区放得下两行卡片并有富余。 */
    val DialogMaxHeight: Dp = 1100.dp

    /** 按浮层内容区的可用宽度算卡片列数。 */
    fun columnsFor(availableWidth: Dp): Int =
        ((availableWidth - ScrollbarAllowance + CardSpacing) / (MinCardWidth + CardSpacing))
            .toInt()
            .coerceIn(MinColumns, MaxColumns)
}

/** 浮层里编辑的一条映射：键与值分开存，键可改。 */
private data class EditableEntry<T>(val key: String, val value: T)

/**
 * 方块匹配器 → 物品匹配器的映射：**一条映射一张卡片**。
 *
 * 卡片体是「标签 + 编辑器」两列（键 / 目标方块 / 手持物品），键行尾是编辑按钮，
 * 卡片头部两端是拖拽手柄与删除按钮；条目增删、排序与编辑都在副本上做，确认时才写回配置。
 *
 * @param config 映射配置项
 * @param modifier 作用于配置页上那一行
 * @param editorDialogTitle 编辑浮层标题
 * @param keyLabel 键行的标签
 * @param reverseValueColumnOrder 卡片内两列匹配器的先后顺序：true 时手持物品在前
 * @param dialogModifier 附加到浮层的尺寸修饰；缺省用卡片布局的下限 / 上限
 */
@Composable
fun BlockInfoItemStackPairMapWrapper(
    config: ConfigMap<BlockInfoItemStackPair>,
    modifier: Modifier = Modifier,
    editorDialogTitle: @Composable (() -> Unit)? = { ConfigDialogTitle(config) },
    keyLabel: @Composable () -> Unit = { Text(component = IGLang.ConfigWrapper.mapKey) },
    reverseValueColumnOrder: Boolean = false,
    dialogModifier: Modifier = Modifier
        .widthIn(
            min = BlockInfoItemStackPairCardDefaults.DialogMinWidth,
            max = BlockInfoItemStackPairCardDefaults.DialogMaxWidth,
        )
        .heightIn(max = BlockInfoItemStackPairCardDefaults.DialogMaxHeight),
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.mapConfigWrapperText(size), modifier) { showEditDialog = true }

    if (showEditDialog) {
        val editingValue = rememberKeyedList(config.entries.map { EditableEntry(it.key, it.value) })
        var showAddDialog by remember { mutableStateOf(false) }
        // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
        val cardGridState = rememberLazyGridState()
        val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

        FlexibleDialog(
            onDismissRequest = { showEditDialog = false },
            onConfirmRequest = {
                config.clear()
                editingValue.entries.forEach { (_, entry) -> config[entry.key] = entry.value }
                true
            },
            title = editorDialogTitle,
            modifier = dialogModifier,
            content = {
                CompositionLocalProvider(ItemBrowserDefaults.LocalItemIconSize provides 32.dp) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .fabScrollVisibility(addButtonVisibility),
                    ) {
                        EditDialogContent(
                            modifier = Modifier.fillMaxSize(),
                            header = {},
                        ) { _ ->
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                EditDialogContentCards(
                                    state = editingValue,
                                    lazyGridState = cardGridState,
                                    columns = BlockInfoItemStackPairCardDefaults.columnsFor(maxWidth),
                                    removeButton = { index, _ ->
                                        RemoveConfirmButton(
                                            message = editingValue.entries[index].value.key,
                                            onConfirm = { editingValue.removeAt(index) },
                                            iconScale = LocalIconScale.current,
                                            contentPadding = EditDialogContentDefaults.iconPadding,
                                        )
                                    },
                                ) { _, entry, onValueChange ->
                                    BlockInfoItemStackPairCard(
                                        entry = entry,
                                        onValueChange = onValueChange,
                                        isDuplicateKey = { newKey ->
                                            newKey != entry.key && editingValue.entries.any { it.value.key == newKey }
                                        },
                                        keyLabel = keyLabel,
                                        reverseValueColumnOrder = reverseValueColumnOrder,
                                    )
                                }
                            }
                        }

                        EditDialogAddButton(
                            visibility = addButtonVisibility,
                            modifier = Modifier.align(Alignment.BottomEnd),
                        ) {
                            Button(
                                onClick = { showAddDialog = true },
                                contentPadding = ConfigControlDefaults.IconButtonPadding,
                            ) {
                                Icon(Icons.Add, scale = configIconScale())
                            }
                        }
                    }
                }
            },
        )

        if (showAddDialog) {
            AddEntryDialog(
                existingKeys = editingValue.entries.map { it.value.key },
                keyLabel = keyLabel,
                reverseValueColumnOrder = reverseValueColumnOrder,
                onDismissRequest = { showAddDialog = false },
                onConfirm = { entry ->
                    editingValue.add(entry)
                    showAddDialog = false
                },
            )
        }
    }
}

/** 一条映射的卡片体：键 / 两个匹配器各占一行，标签列对齐、编辑器等宽。 */
@Composable
private fun BlockInfoItemStackPairCard(
    entry: EditableEntry<BlockInfoItemStackPair>,
    onValueChange: (EditableEntry<BlockInfoItemStackPair>) -> Unit,
    isDuplicateKey: (String) -> Boolean,
    keyLabel: @Composable () -> Unit,
    reverseValueColumnOrder: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BlockInfoItemStackPairCardDefaults.RowSpacing),
    ) {
        BlockInfoItemStackPairRow(label = keyLabel) {
            KeyField(
                key = entry.key,
                isDuplicate = isDuplicateKey,
                onKeyChange = { onValueChange(entry.copy(key = it)) },
            )
        }

        val blockRow: @Composable () -> Unit = {
            BlockInfoMatcherDisplayerInnerEditor(
                value = entry.value.block,
                onValueChange = { onValueChange(entry.copy(value = entry.value.copy(first = it))) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        val itemRow: @Composable () -> Unit = {
            ItemStackMatcherDisplayerInnerEditor(
                value = entry.value.item,
                onValueChange = { onValueChange(entry.copy(value = entry.value.copy(second = it))) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        BlockInfoItemStackPairRow(label = { Text(component = HSLang.BlockInfoMatcher.targetBlock) }) {
            if (reverseValueColumnOrder) itemRow() else blockRow()
        }
        BlockInfoItemStackPairRow(label = { Text(component = HSLang.ItemStackMatcher.handheldItem) }) {
            if (reverseValueColumnOrder) blockRow() else itemRow()
        }
    }
}

/**
 * 一行「标签 + 编辑器」：两列按 [BlockInfoItemStackPairCardDefaults] 的权重比例分宽，
 * 各行比例一致 ⇒ 编辑器列等宽；行高由内容自然决定。
 */
@Composable
private fun BlockInfoItemStackPairRow(
    label: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(BlockInfoItemStackPairCardDefaults.LabelGap),
    verticalAlignment = Alignment.CenterVertically,
) {
    val rowScope = this
    val editorModifier = Modifier.weight(BlockInfoItemStackPairCardDefaults.EditorWeight)

    Box(
        modifier = Modifier
            .weight(BlockInfoItemStackPairCardDefaults.LabelWeight)
            .padding(start = BlockInfoItemStackPairCardDefaults.LabelStartPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        label()
    }
    Box(modifier = editorModifier) {
        rowScope.content()
    }
}

/** 键的展示与编辑：框内右端的编辑按钮，编辑走浮层（键判重）。 */
@Composable
private fun KeyField(
    key: String,
    isDuplicate: (String) -> Boolean,
    onKeyChange: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    InlineEditField(
        onEdit = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(key, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    if (showDialog) {
        KeyEditDialog(
            key = key,
            isDuplicate = isDuplicate,
            onDismissRequest = { showDialog = false },
            onConfirm = {
                onKeyChange(it)
                showDialog = false
            },
        )
    }
}

/** 键编辑浮层：输入新键，重复时禁用确认。 */
@Composable
private fun KeyEditDialog(
    key: String,
    isDuplicate: (String) -> Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val state = rememberTextFieldState(key)
    val newKey = state.text.toString()
    val duplicated = remember(newKey) { isDuplicate(newKey) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.edit) },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    state = state,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    modifier = Modifier.fillMaxWidth()
                        .thenIf(duplicated) {
                            Modifier.tooltip(pinned = duplicated) {
                                Text(component = IGLang.ConfigWrapper.keyExists(newKey))
                            }
                        },
                    isError = duplicated,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newKey) },
                enabled = !duplicated && newKey.isNotEmpty(),
            ) {
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

/** 新增映射浮层：键 + 两个匹配器，与卡片体同一套行布局。 */
@Composable
private fun AddEntryDialog(
    existingKeys: List<String>,
    keyLabel: @Composable () -> Unit,
    reverseValueColumnOrder: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (EditableEntry<BlockInfoItemStackPair>) -> Unit,
) {
    val keyState = rememberTextFieldState("")
    val newKey = keyState.text.toString()
    val duplicated = remember(newKey) { existingKeys.any { it == newKey } }

    var block by remember { mutableStateOf(BlockInfoMatcher.targetBlockMatcher) }
    var item by remember { mutableStateOf(ItemStackMatcher.handheldItemMatcher) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.add) },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BlockInfoItemStackPairRow(label = keyLabel) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            state = keyState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            modifier = Modifier.fillMaxWidth()
                                .thenIf(duplicated) {
                                    Modifier.tooltip(pinned = duplicated) {
                                        Text(component = IGLang.ConfigWrapper.keyExists(newKey))
                                    }
                                },
                            isError = duplicated,
                        )
                    }
                }
                BlockInfoItemStackPairRow(label = { Text(component = HSLang.BlockInfoMatcher.targetBlock) }) {
                    if (reverseValueColumnOrder) {
                        ItemStackMatcherDisplayerInnerEditor(
                            value = item,
                            onValueChange = { item = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        BlockInfoMatcherDisplayerInnerEditor(
                            value = block,
                            onValueChange = { block = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                BlockInfoItemStackPairRow(label = { Text(component = HSLang.ItemStackMatcher.handheldItem) }) {
                    if (reverseValueColumnOrder) {
                        BlockInfoMatcherDisplayerInnerEditor(
                            value = block,
                            onValueChange = { block = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        ItemStackMatcherDisplayerInnerEditor(
                            value = item,
                            onValueChange = { item = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(EditableEntry(newKey, block to item)) },
                enabled = !duplicated && newKey.isNotEmpty(),
            ) {
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
