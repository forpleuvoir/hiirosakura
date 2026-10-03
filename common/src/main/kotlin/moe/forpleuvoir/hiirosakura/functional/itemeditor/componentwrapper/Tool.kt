package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.*
import moe.forpleuvoir.hiirosakura.ui.util.NullableInputTransformation
import moe.forpleuvoir.hiirosakura.ui.util.NullableTrailingIcon
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetBlockEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.ItemIconButton
import moe.forpleuvoir.hiirosakura.ui.widget.ToggleButton
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalColorScheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.HolderSet
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.Tool
import net.minecraft.world.level.block.Block
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Composable
fun ToolComponentWrapper(
    key: Identifier,
    value: Tool,
    onValueChange: (Tool) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }
    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        Text(component = IGLang.ConfigWrapper.listConfigWrapperText(value.rules.size), overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        ToolEditorDialog(
            key = key,
            value = value,
            onValueChange = onValueChange,
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/**
 * 挖掘规则卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算。
 */
private object ToolCardDefaults {

    /** 一张卡片的最小宽度：方块列表 + 速度输入框 + 卡片内边距。 */
    val MinCardWidth: Dp = 360.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 2

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 3

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
 * 工具组件编辑浮层：表头一行三项固定属性，下面是「一条挖掘规则一张卡片」的卡片网格。
 *
 * 卡片头部的拖拽手柄与删除按钮由 `EditDialogContentCards` 提供，卡片体是 [RuleContent]；
 * 规则增删、排序与编辑都在副本上做，确认时才写回。
 *
 * @param key 语言键来源
 * @param value 待编辑的工具组件
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
@Composable
fun ToolEditorDialog(
    key: Identifier,
    value: Tool,
    onValueChange: (Tool) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
) {
    val rules = rememberKeyedList(value.rules)

    var defaultMiningSpeed by remember { mutableFloatStateOf(value.defaultMiningSpeed) }
    var damagePerBlock by remember { mutableIntStateOf(value.damagePerBlock) }
    var canDestroyBlocksInCreative by remember { mutableStateOf(value.canDestroyBlocksInCreative) }

    var showAddDialog by remember { mutableStateOf(false) }
    // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
    val cardGridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            onValueChange(Tool(rules.entries.values().toMutableList(), defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative))
            true
        },
        modifier = ToolCardDefaults.DialogModifier,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            DataComponentSection(
                                key,
                                suffix = "default_mining_speed",
                                fallback = "Default Mining Speed",
                                modifier = Modifier.weight(1f),
                            ) {
                                FloatField(
                                    defaultMiningSpeed,
                                    { defaultMiningSpeed = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            DataComponentSection(
                                key,
                                suffix = "damage_per_block",
                                fallback = "Damage Per Block",
                                modifier = Modifier.weight(1f),
                            ) {
                                IntField(
                                    damagePerBlock,
                                    { damagePerBlock = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            DataComponentSection(
                                key,
                                suffix = "can_destroy_blocks_in_creative",
                                modifier = Modifier.weight(1f),
                            ) {
                                ToggleButton(
                                    canDestroyBlocksInCreative,
                                    { canDestroyBlocksInCreative = it },
                                    modifier = Modifier.fillMaxWidth().height(configControlHeight()),
                                )
                            }
                        }
                    },
                ) { _ ->
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        EditDialogContentCards(
                            state = rules,
                            lazyGridState = cardGridState,
                            columns = ToolCardDefaults.columnsFor(maxWidth),
                            maxHeight = ToolCardDefaults.DialogMaxHeight,
                            removeButton = { index, _ ->
                                RemoveConfirmButton(
                                    message = key.asTranslateText(suffix = "rule", fallback = "Rule").plainText,
                                    onConfirm = { rules.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, entry, onEntryChange ->
                            RuleContent(entry, onEntryChange, key)
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

            if (showAddDialog) {
                var addingEntry by remember {
                    mutableStateOf(
                        Tool.Rule(
                            HolderSet.empty(),
                            Optional.ofNullable(null),
                            Optional.ofNullable(null)
                        )
                    )
                }
                SimpleAlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    onConfirmRequest = {
                        rules.add(addingEntry)
                        true
                    },
                    title = { DataComponentDialogTitle(component = IGLang.Misc.add) },
                    content = {
                        RuleContent(addingEntry, { addingEntry = it }, key)
                    }
                )
            }
        }
    )
}

/** 一条挖掘规则的卡片体：方块列表、速度与掉落判定；切换开关的候选由调用方按整卡宽排列。 */
@Composable
private fun RuleContent(
    value: Tool.Rule,
    onValueChange: (Tool.Rule) -> Unit,
    key: Identifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        //blocks
        val count = value.blocks.count()
        var showDialog by remember { mutableStateOf(false) }
        DataComponentField(key, suffix = "blocks", fallback = "Blocks", modifier = Modifier.fillMaxWidth()) {
            DataComponentDisplay(
                modifier = Modifier
                    .thenIf(count > 0) {
                        Modifier.tooltip {
                            FlowRow(
                                modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                value.blocks.take(20).forEach { item ->
                                    ItemIconButton(
                                        item.value(),
                                        hoverHighlight = false,
                                        scaleOnHover = 1f,
                                        showTooltip = false,
                                        itemIconSize = DpSize(36.dp, 36.dp),
                                        contentPadding = PaddingValues(0.dp),
                                    )
                                }
                            }
                        }
                    },
                onEdit = { showDialog = true },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    value.blocks.take(6).forEach { item ->
                        ItemIconButton(
                            item.value(),
                            hoverHighlight = false,
                            scaleOnHover = 1f,
                            showTooltip = false,
                            itemIconSize = DpSize(36.dp, 36.dp),
                            contentPadding = PaddingValues(0.dp),
                        )
                    }
                    if (count > 6)
                        Text("...", overflow = TextOverflow.Ellipsis, maxLines = 1)
                    if (count == 0)
                        Text(IGLang.Misc.hasNothing, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
        }

        if (showDialog) {
            HolderSetBlockEditorDialog(
                value = value.blocks,
                onValueChange = { onValueChange(value.copy(blocks = it)) },
                onDismissRequest = { showDialog = false },
                title = { DataComponentDialogTitle(key) }
            )
        }
        //speed
        Spacer(Modifier.height(12.dp))
        DataComponentField(key, suffix = "speed", fallback = "Speed") {
            FloatField(
                value = value.speed.getOrNull() ?: 0f,
                onValueChange = { onValueChange(value.copy(speed = Optional.of(it))) },
                valueRange = 0f..Float.MAX_VALUE,
                inputTransformation = NullableInputTransformation(value.speed.isEmpty),
                valueToText = { if (value.speed.isEmpty) HSLang.Common.unset.plainText else it.toString() },
                trailingIcon = {
                    NullableTrailingIcon(value.speed.isPresent) {
                        onValueChange(value.copy(speed = Optional.empty()))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        //correctForDrops
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(key, suffix = "correct_for_drops", fallback = "Correct For Drops", maxLines = 1, overflow = TextOverflow.Ellipsis)
            Switch(
                value.correctForDrops.getOrNull() ?: false,
                { onValueChange(value.copy(correctForDrops = Optional.ofNullable(it))) }
            )
        }
    }
}


fun Tool.Rule.copy(
    blocks: HolderSet<Block> = this.blocks,
    speed: Optional<Float> = this.speed,
    correctForDrops: Optional<Boolean> = this.correctForDrops,
): Tool.Rule = Tool.Rule(blocks, speed, correctForDrops)
