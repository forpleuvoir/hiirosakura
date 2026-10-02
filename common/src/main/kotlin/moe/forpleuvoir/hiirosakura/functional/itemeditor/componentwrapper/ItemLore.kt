package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentList

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplay
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ItemLore

@Composable
fun ItemLoreComponentWrapper(
    key: Identifier,
    value: ItemLore,
    onValueChange: (ItemLore) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.vanillaTooltip(value.styledLines),
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.styledLines().size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        ItemLoreComponentEditDialog(
            value,
            onValueChange,
            { DataComponentDialogTitle(key) },
            { showDialog = false }
        )
    }
}

/**
 * 行文本卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object ItemLoreCardDefaults {

    /** 一张卡片的最小宽度：行文本展示框 + 卡片内边距。 */
    val MinCardWidth: Dp = 480.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 1

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 2

    /** 卡片间距，取卡片列表的缺省值（算列数时按它反推浮层宽度）。 */
    val CardSpacing: Dp = EditDialogContentDefaults.cardSpacing

    /** 卡片列表右侧细滚动条的占位宽度，算列数时先扣掉。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 浮层宽度下限：一行放得下 [MinColumns] 张卡片。 */
    val DialogMinWidth: Dp
        get() = MinCardWidth * MinColumns + CardSpacing * (MinColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层宽度上限：一行最多 [MaxColumns] 张卡片。 */
    val DialogMaxWidth: Dp
        get() = MinCardWidth * MaxColumns + CardSpacing * (MaxColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层高度上限：可视区放得下数行卡片并有富余。 */
    val DialogMaxHeight: Dp = 800.dp

    /** 浮层尺寸约束：宽度只夹上下限，具体宽度由窗口决定。 */
    val DialogModifier: Modifier
        get() = Modifier
            .widthIn(min = DialogMinWidth, max = DialogMaxWidth)
            .heightIn(max = DialogMaxHeight)

    /**
     * 按浮层内容区的可用宽度算卡片列数：先扣掉滚动条占位，再按「一张卡片 + 一段间距」整除，
     * 结果夹在 [MinColumns]..[MaxColumns] 内。
     */
    fun columnsFor(availableWidth: Dp): Int =
        ((availableWidth - ScrollbarAllowance + CardSpacing) / (MinCardWidth + CardSpacing))
            .toInt()
            .coerceIn(MinColumns, MaxColumns)
}

/**
 * 行文本编辑浮层：一行一张卡片（拖拽手柄 + 编辑 / 删除），卡片体是行文本的展示框；
 * `-1` 表示新增行。
 *
 * @param value 待编辑的行文本
 * @param onValueChange 确认时的写回
 * @param title 浮层标题
 * @param onDismissRequest 关闭请求
 */
@Composable
private fun ItemLoreComponentEditDialog(
    value: ItemLore,
    onValueChange: (ItemLore) -> Unit,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val editingLines = rememberKeyedList(value.lines)

    val maxSize = ItemLore.MAX_LINES

    var editingComponent by remember { mutableStateOf<Int?>(null) }

    var editingComponentInlineDialog by remember { mutableStateOf<Int?>(null) }

    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = ItemLoreCardDefaults.DialogModifier,
        onConfirmRequest = {
            onValueChange(ItemLore(editingLines.entries.values().take(maxSize).toMutableList()))
            true
        },
        content = {
            EditDialogContent(
                modifier = Modifier.fillMaxSize(),
                header = {},
                addButton = {
                    Button(
                        onClick = {
                            if (isQuickAction)
                                editingComponentInlineDialog = -1
                            else
                                editingComponent = -1
                        },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                },
            ) { listState ->
                EditDialogContentList(
                    state = editingLines,
                    lazyListState = listState,
                    maxHeight = ItemLoreCardDefaults.DialogMaxHeight,
                    removeButton = { index, component ->
                        RemoveConfirmButton(
                            message = component.toString(),
                            onConfirm = { editingLines.removeAt(index) },
                            iconScale = LocalIconScale.current,
                            contentPadding = EditDialogContentDefaults.iconPadding,
                        )
                    },
                ) {
                    column(width = weight(1f)) { index, entry ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DataComponentDisplay(modifier = Modifier.weight(1f)) {
                                Text(entry.value, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                            IconButton({
                                if (isQuickAction)
                                    editingComponentInlineDialog = index
                                else
                                    editingComponent = index
                            }, Modifier.tooltip { Text(component = IGLang.Misc.edit) }) {
                                Icon(Icons.Edit)
                            }
                        }
                    }
                }
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
    )
}
