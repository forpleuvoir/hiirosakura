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
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentHelper
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentHelper.enchantmentDescription
import moe.forpleuvoir.hiirosakura.ui.widget.EnchatmentSelector
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.appendTranslate
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

@Composable
fun ItemEnchantmentsComponentWrapper(
    key: Identifier,
    value: ItemEnchantments,
    onValueChange: (ItemEnchantments) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(value.size() > 0) {
            Modifier.tooltip {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    value.entrySet().take(20).forEach { (holder, i) ->
                        Text(Literal("").append(enchantmentDescription(holder)).append(" ").appendTranslate("enchantment.level.$i", i.toString()))
                    }

                    if (value.size() > 20) {
                        Text("...")
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.enchantments.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        ItemEnchantmentsComponentEditDialog(
            value,
            onValueChange,
            key,
            { DataComponentDialogTitle(key) },
            { showDialog = false }
        )
    }
}

/**
 * 附魔条目卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object ItemEnchantmentsCardDefaults {

    /** 一张卡片的最小宽度：附魔选择器 + 等级输入框 + 卡片内边距。 */
    val MinCardWidth: Dp = 520.dp

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

    /** 浮层高度上限：可视区放得下两行卡片并有富余。 */
    val DialogMaxHeight: Dp = 1100.dp

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
 * 附魔编辑浮层：一条附魔一张卡片。
 *
 * 卡片头部两端是拖拽手柄与删除按钮（由 `EditDialogContentCards` 提供），卡片体是
 * [EnchantmentEntryContent]；条目增删、排序与编辑都在副本上做，确认时才写回。
 *
 * @param value 待编辑的附魔组件
 * @param onValueChange 确认时的写回
 * @param key 语言键来源
 * @param title 浮层标题
 * @param onDismissRequest 关闭请求
 */
@Composable
fun ItemEnchantmentsComponentEditDialog(
    value: ItemEnchantments,
    onValueChange: (ItemEnchantments) -> Unit,
    key: Identifier,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val editingEnchantments = rememberKeyedList(value.enchantments.map { it.key to it.value }, key = value)

    /** 可选附魔：当前条目自身的附魔保留，其余排除已被其它条目占用的。 */
    fun selectableEnchantments(current: Holder<Enchantment>?): List<Holder<Enchantment>> {
        val used = editingEnchantments.entries.mapTo(mutableSetOf()) { it.value.first }
        return EnchatmentHelper.REGISTERED_ENCHANTMENT
            .filter { it == current || it !in used }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = ItemEnchantmentsCardDefaults.DialogModifier,
        onConfirmRequest = {
            onValueChange(ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>>().apply {
                editingEnchantments.entries.forEach { (_, entry) ->
                    set(entry.first, entry.second)
                }
            }))
            true
        },
        content = {
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
                            state = editingEnchantments,
                            lazyGridState = cardGridState,
                            columns = ItemEnchantmentsCardDefaults.columnsFor(maxWidth),
                            maxHeight = ItemEnchantmentsCardDefaults.DialogMaxHeight,
                            removeButton = { index, entry ->
                                RemoveConfirmButton(
                                    message = entry.first.value().description.plainText,
                                    onConfirm = { editingEnchantments.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, entry, onEntryChange ->
                            EnchantmentEntryContent(entry, onEntryChange, selectableEnchantments(entry.first))
                        }
                    }
                }

                EditDialogAddButton(
                    visibility = addButtonVisibility,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Button(
                        onClick = {
                            selectableEnchantments(null).firstOrNull()?.let { showAddDialog = true }
                        },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                }
            }

            if (showAddDialog) {
                var level by remember { mutableStateOf(1) }

                var enchantment by remember { mutableStateOf(selectableEnchantments(null).first()) }
                SimpleAlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    onConfirmRequest = {
                        editingEnchantments.add(enchantment to level)
                        true
                    },
                    title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                    content = {
                        Row {
                            EnchatmentSelector(
                                enchantment,
                                onSelect = { enchantment = it },
                                items = selectableEnchantments(enchantment),
                                modifier = Modifier.weight(1f),
                                searchFilter = { entry, str ->
                                    entry.registeredName.contains(str) || entry.value().description.plainText.contains(str)
                                },
                                //TODO 鎹㈠埆鐨勬柟寮?
//                                    label = {
//                                        Text(key, suffix = "enchantment", fallback = "enchantment")
//                                    }
                            )
                            Spacer(Modifier.width(8.dp))
                            IntField(
                                level,
                                { level = it },
                                valueRange = 1..255,
                                modifier = Modifier.width(120.dp),
                            )
                        }
                    }
                )
            }
        }
    )
}

/** 一条附魔的卡片体：附魔选择器 + 等级输入框；选择器候选由调用方按其余条目算好后传入。 */
@Composable
private fun EnchantmentEntryContent(
    entry: Pair<Holder<Enchantment>, Int>,
    onValueChange: (Pair<Holder<Enchantment>, Int>) -> Unit,
    items: List<Holder<Enchantment>>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EnchatmentSelector(
            entry.first,
            onSelect = { onValueChange(entry.copy(first = it)) },
            items = items,
            modifier = Modifier.weight(1f),
            searchFilter = { enchantment, str ->
                enchantment.registeredName.contains(str) || enchantmentDescription(enchantment).plainText.contains(str)
            },
            //TODO 鎹㈠埆鐨勬柟寮?
//                                    label = {
//                                        Text(key, suffix = "enchantment", fallback = "enchantment")
//                                    }
        )
        IntField(
            entry.second,
            { onValueChange(entry.copy(second = it)) },
            valueRange = 1..255,
            modifier = Modifier.width(120.dp),
        )
    }
}