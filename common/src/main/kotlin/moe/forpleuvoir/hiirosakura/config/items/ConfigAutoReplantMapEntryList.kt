package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogTitle
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigListRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asDerivedState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configAutoReplantEntryList(name: String, defaultValue: List<AutoReplant.Entry>) = configList(name, defaultValue, AutoReplant.Entry)


//------------ UI Wrapper ------------\\

/** 自动补种条目卡片的排版常量。 */
private object AutoReplantCardDefaults {

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

/**
 * 自动补种条目列表：**一个条目一张卡片**。
 *
 * 卡片体是「标签 + 编辑器」三行（目标方块 / 补种物品 / 地面方块），卡片头部两端是拖拽手柄与删除按钮；
 * 条目增删、排序与编辑都在副本上做，确认时才写回配置。
 *
 * @param config 条目列表配置项
 * @param modifier 作用于配置页上那一行
 * @param editorDialogTitle 编辑浮层标题
 * @param dialogModifier 附加到浮层的尺寸修饰；缺省用卡片布局的下限 / 上限
 */
@Composable
fun AutoReplantEntryListConfigWrapper(
    config: ConfigList<AutoReplant.Entry>,
    editorDialogTitle: @Composable (() -> Unit)? = { ConfigDialogTitle(config) },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier
        .widthIn(min = AutoReplantCardDefaults.DialogMinWidth, max = AutoReplantCardDefaults.DialogMaxWidth)
        .heightIn(max = AutoReplantCardDefaults.DialogMaxHeight),
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.listConfigWrapperText(size), modifier) { showEditDialog = true }

    if (showEditDialog) {
        val editingValue = rememberKeyedList(config)
        var showAddDialog by remember { mutableStateOf(false) }
        // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
        val cardGridState = rememberLazyGridState()
        val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

        FlexibleDialog(
            onDismissRequest = { showEditDialog = false },
            onConfirmRequest = {
                config.clear()
                editingValue.entries.forEach { (_, value) -> config.add(value) }
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
                                    columns = AutoReplantCardDefaults.columnsFor(maxWidth),
                                    removeButton = { index, _ ->
                                        RemoveConfirmButton(
                                            message = "#${index + 1}",
                                            onConfirm = { editingValue.removeAt(index) },
                                            iconScale = LocalIconScale.current,
                                            contentPadding = EditDialogContentDefaults.iconPadding,
                                        )
                                    },
                                ) { _, value, onValueChange ->
                                    AutoReplantEntryForm(value, onValueChange)
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
            var entry by remember { mutableStateOf(AutoReplant.Entry()) }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(component = IGLang.Misc.add) },
                text = {
                    AutoReplantEntryForm(
                        value = entry,
                        onValueChange = { entry = it },
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editingValue.add(entry)
                            showAddDialog = false
                        }
                    ) {
                        Text(component = IGLang.Misc.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(component = IGLang.Misc.cancel)
                    }
                },
            )
        }
    }
}

/** 条目表单：三个匹配器各占一行；卡片体与新增浮层共用同一份。 */
@Composable
private fun AutoReplantEntryForm(
    value: AutoReplant.Entry,
    onValueChange: (AutoReplant.Entry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AutoReplantCardDefaults.RowSpacing),
    ) {
        AutoReplantRow(
            label = {
                Text(
                    component = HSLang.AutoReplant.mapEntryTargetBlock,
                    modifier = Modifier.tooltip { Text(component = HSLang.AutoReplant.mapEntryTargetBlockComment) },
                    maxLines = 1,
                )
            },
        ) {
            BlockInfoMatcherDisplayerInnerEditor(
                value = value.targetBlock,
                onValueChange = { onValueChange(value.copy(targetBlock = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        AutoReplantRow(
            label = {
                Text(
                    component = HSLang.AutoReplant.mapEntryReplantItem,
                    modifier = Modifier.tooltip { Text(component = HSLang.AutoReplant.mapEntryReplantItemComment) },
                    maxLines = 1,
                )
            },
        ) {
            ItemStackMatcherDisplayerInnerEditor(
                value = value.replantItem,
                onValueChange = { onValueChange(value.copy(replantItem = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        AutoReplantRow(
            label = {
                Text(
                    component = HSLang.AutoReplant.mapEntryGroundBlock,
                    modifier = Modifier.tooltip { Text(component = HSLang.AutoReplant.mapEntryGroundBlockComment) },
                    maxLines = 1,
                )
            },
        ) {
            BlockInfoMatcherDisplayerInnerEditor(
                value = value.groundBlock,
                onValueChange = { onValueChange(value.copy(groundBlock = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 一行「标签 + 编辑器」：两列按 [AutoReplantCardDefaults] 的权重比例分宽，
 * 各行比例一致 ⇒ 编辑器列等宽；行高由内容自然决定。
 */
@Composable
private fun AutoReplantRow(
    label: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(AutoReplantCardDefaults.LabelGap),
    verticalAlignment = Alignment.CenterVertically,
) {
    val rowScope = this
    val editorModifier = Modifier.weight(AutoReplantCardDefaults.EditorWeight)

    Box(
        modifier = Modifier
            .weight(AutoReplantCardDefaults.LabelWeight)
            .padding(start = AutoReplantCardDefaults.LabelStartPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        label()
    }
    Box(modifier = editorModifier) {
        rowScope.content()
    }
}
