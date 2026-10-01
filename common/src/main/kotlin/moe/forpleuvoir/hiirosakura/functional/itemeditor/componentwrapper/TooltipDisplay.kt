package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.DataComponentTypeSelector
import moe.forpleuvoir.hiirosakura.util.asText
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.HorizontalDivider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.TooltipDisplay

@Composable
fun TooltipDisplayComponentWrapper(
    key: Identifier,
    value: TooltipDisplay,
    onValueChange: (TooltipDisplay) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        tooltip = {
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
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.hiddenComponents.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        TooltipDisplayEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            title = { DataComponentDialogTitle(key) },
            onDismissRequest = { showDialog = false }
        )
    }
}

/**
 * 隐藏组件卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object TooltipDisplayCardDefaults {

    /** 一张卡片的最小宽度：组件类型选择器 + 卡片内边距。 */
    val MinCardWidth: Dp = 560.dp

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
    val DialogMaxHeight: Dp = 920.dp

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
 * 隐藏组件编辑浮层：表头是隐藏提示开关，下面是一条隐藏组件一张卡片的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供；候选组件排除已隐藏的条目，
 * 由 [DataComponentTypeSelector] 选定后经浮动添加按钮加入。
 *
 * @param key 语言键来源
 * @param value 待编辑的隐藏提示设置
 * @param onValueChange 确认时的写回
 * @param title 浮层标题
 * @param onDismissRequest 关闭请求
 */
@Composable
fun TooltipDisplayEditorDialog(
    key: Identifier,
    value: TooltipDisplay,
    onValueChange: (TooltipDisplay) -> Unit,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var enabled by remember { mutableStateOf(value.hideTooltip) }

    val list = rememberKeyedList(value.hiddenComponents.toList())

    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        modifier = TooltipDisplayCardDefaults.DialogModifier,
        title = title,
        onConfirmRequest = {
            onValueChange(TooltipDisplay(enabled, ReferenceLinkedOpenHashSet(list.entries.values())))
            true
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                val availableComponents = BuiltInRegistries.DATA_COMPONENT_TYPE.toList() - list.entries.values().toSet()
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(key, suffix = "hide_tooltip", fallback = "HideTooltip")
                            Switch(enabled, { enabled = it })
                        }
                    },
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = list,
                            lazyGridState = cardGridState,
                            columns = TooltipDisplayCardDefaults.columnsFor(maxWidth),
                            maxHeight = TooltipDisplayCardDefaults.DialogMaxHeight,
                            removeButton = { index, component ->
                                RemoveConfirmButton(
                                    message = component.keyOrUnknown(registryAccess!!).asText().plainText,
                                    onConfirm = { list.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, component, onEntryChange ->
                            DataComponentTypeSelector(
                                component,
                                { onEntryChange(it) },
                                items = listOf(component) + availableComponents,
                                modifier = Modifier.fillMaxWidth(),
                                searchFilter = { type, str ->
                                    str in type.keyOrUnknown(registryAccess!!).toString() || str in type.keyOrUnknown(registryAccess!!)
                                        .asTranslateText().plainText
                                }
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

                if (showAddDialog) {
                    var type by remember(availableComponents) { mutableStateOf(availableComponents.first()) }
                    SimpleAlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        onConfirmRequest = {
                            list.add(type)
                            true
                        },
                        title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                        content = {
                            DataComponentTypeSelector(
                                type,
                                {
                                    type = it
                                },
                                content = {
                                    Text(it.keyOrUnknown.toString(), overflow = TextOverflow.Ellipsis, maxLines = 1)
                                },
                                items = availableComponents,
                                modifier = Modifier.fillMaxWidth(),
                                searchFilter = { type, str ->
                                    str in type.keyOrUnknown(registryAccess!!).toString() || str in type.keyOrUnknown(registryAccess!!)
                                        .asTranslateText().plainText
                                }
                            )
                        }
                    )
                }
            }
        }
    )
}
