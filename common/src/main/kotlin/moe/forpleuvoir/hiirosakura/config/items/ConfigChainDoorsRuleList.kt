package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy
import moe.forpleuvoir.hiirosakura.ui.configwrapper.ListConfigWrapperDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.InlineEditField
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.*
import moe.forpleuvoir.ibukigourd.ui.editdialog.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configChainDoorsRuleList(name: String, defaultValue: List<ChainDoorsRule>) = configList(name, defaultValue, ChainDoorsRule)

//------------ UI Wrapper ------------\\

/**
 * 连锁开门规则卡片的排版常量。
 *
 * 卡片宽度不写死：浮层宽度只给下限（一行放得下 [MinColumns] 张）与上限（一行最多 [MaxColumns] 张），
 * 列数按实际可用宽度算，窗口更宽时卡片变宽而不是继续加列。
 */
private object ChainDoorsRuleCardDefaults {

    /** 一张卡片的最小宽度：标签列 + 匹配展示框 + 编辑按钮 + 卡片内边距；宁可卡片变宽也不压窄内容。 */
    val MinCardWidth: Dp = 560.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 2

    /** 一行的卡片数上限：要容下行首标签与完整展示框，一行最多两张。 */
    const val MaxColumns: Int = 2

    /** 卡片间距，取卡片列表的缺省值（列数按它反推浮层宽度）。 */
    val CardSpacing: Dp = EditDialogContentDefaults.cardSpacing

    /** 卡片列表右侧细滚动条的占位宽度，算列数时先扣掉。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 卡片内各行之间的纵向间距。 */
    val RowSpacing: Dp = 8.dp

    /** 标签列占行宽的权重（与 [EditorWeight] 配对比；四行比例一致 ⇒ 编辑器列等宽）。 */
    const val LabelWeight: Float = 0.32f

    /** 编辑器列占行宽的权重。 */
    const val EditorWeight: Float = 0.68f

    /** 标签列与编辑器之间的水平间距。 */
    val LabelGap: Dp = 8.dp

    /** 标签列左侧留白：标签不贴卡片边缘。 */
    val LabelStartPadding: Dp = 8.dp

    /** 开关行右侧留白：开关不贴卡片边缘。 */
    val SwitchEndPadding: Dp = 8.dp

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

@Composable
fun ChainDoorsRuleListConfigWrapper(
    config: ConfigList<ChainDoorsRule>,
    editorDialogTitle: @Composable (() -> Unit)? = { ConfigDialogTitle(config) },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = ChainDoorsRuleCardDefaults.DialogModifier,
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

        ListConfigWrapperDefaults.EditDialog(
            config = config,
            editingValue = editingValue,
            modifier = dialogModifier,
            title = editorDialogTitle,
            onDismissRequest = { showEditDialog = false },
            onConfirmRequest = {
                config.clear()
                it.entries.forEach { (_, value) -> config.add(value) }
                true
            }
        ) {
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
                            columns = ChainDoorsRuleCardDefaults.columnsFor(maxWidth),
                            removeButton = { index, _ ->
                                RemoveConfirmButton(
                                    message = "#${index + 1}",
                                    onConfirm = { editingValue.removeAt(index) },
                                    iconScale = LocalIconScale.current,
                                    contentPadding = EditDialogContentDefaults.iconPadding,
                                )
                            },
                        ) { _, value, onValueChange ->
                            ChainDoorsRuleForm(
                                value = value,
                                onValueChange = onValueChange,
                                modifier = Modifier.fillMaxWidth(),
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

            if (showAddDialog) {
                var rule by remember { mutableStateOf(ChainDoorsRule.MOB_INTERACTABLE_DOORS) }
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text(component = IGLang.Misc.add) },
                    text = {
                        ChainDoorsRuleForm(
                            value = rule,
                            onValueChange = { rule = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                editingValue.add(rule)
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
}

/**
 * 规则表单：两个方块匹配行、按键响应模式开关与连锁策略行；卡片体与新增浮层共用同一份。
 *
 * 每行都是「标签 + 编辑器」，四行共用同一宽度的标签列，编辑器因此左边缘与宽度都对齐。
 */
@Composable
private fun ChainDoorsRuleForm(
    value: ChainDoorsRule,
    onValueChange: (ChainDoorsRule) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ChainDoorsRuleCardDefaults.RowSpacing),
    ) {
        ChainDoorsLabeledRow(
            label = {
                Text(
                    component = HSLang.ChainDoors.originDoor,
                    modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.originDoorComment) },
                    maxLines = 1,
                )
            },
        ) {
            BlockInfoMatcherDisplayerInnerEditor(
                value = value.originDoor,
                onValueChange = { onValueChange(value.copy(originDoor = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        ChainDoorsLabeledRow(
            label = {
                Text(
                    component = HSLang.ChainDoors.chainDoor,
                    modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.chainDoorComment) },
                    maxLines = 1,
                )
            },
        ) {
            BlockInfoMatcherDisplayerInnerEditor(
                value = value.chainDoor,
                onValueChange = { onValueChange(value.copy(chainDoor = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        ChainDoorsLabeledRow(
            label = {
                Text(
                    component = HSLang.ChainDoors.keyToggleMode,
                    modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.keyToggleModeComment) },
                    maxLines = 1,
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = ChainDoorsRuleCardDefaults.SwitchEndPadding),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Switch(value.keyToggleMode, { onValueChange(value.copy(keyToggleMode = it)) })
            }
        }

        ChainDoorsLabeledRow(
            label = {
                Text(
                    component = HSLang.ChainDoors.strategy,
                    modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategyComment) },
                    maxLines = 1,
                )
            },
        ) {
            ChainStrategyDisplayerEditor(
                value = value.strategy,
                onValueChange = { onValueChange(value.copy(strategy = it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 一行「标签 + 编辑器」：两列按 [ChainDoorsRuleCardDefaults.LabelWeight] 与
 * [ChainDoorsRuleCardDefaults.EditorWeight] 的比例分配行宽 —— 四行行宽与比例一致，编辑器列因此等宽。
 *
 * 行高由两侧内容自然决定，不做统一设置。
 */
@Composable
private fun ChainDoorsLabeledRow(
    label: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(ChainDoorsRuleCardDefaults.LabelGap),
    verticalAlignment = Alignment.CenterVertically,
) {
    val rowScope = this
    val editorModifier = Modifier.weight(ChainDoorsRuleCardDefaults.EditorWeight)

    Box(
        modifier = Modifier
            .weight(ChainDoorsRuleCardDefaults.LabelWeight)
            .padding(start = ChainDoorsRuleCardDefaults.LabelStartPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        label()
    }
    Box(modifier = editorModifier) {
        rowScope.content()
    }
}

@Composable
fun ChainStrategyDisplayerEditor(
    value: ChainStrategy,
    onValueChange: (ChainStrategy) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    InlineEditField(
        onEdit = { showDialog = true },
        modifier = modifier,
        tooltip = { ChainStrategyTooltip(value) },
    ) {
        ChainStrategyInfo(value)
    }
    if (showDialog) {
        var editingValue by remember { mutableStateOf(value) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(component = HSLang.ChainDoors.strategy) },
            text = {

                ChainStrategyEditor(editingValue) { editingValue = it }

            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(editingValue)
                        showDialog = false
                    }
                ) {
                    Text(component = IGLang.Misc.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(component = IGLang.Misc.cancel)
                }
            },
        )
    }
}

/** 策略明细气泡：当前策略 + 半径 / 形状 / 同方块 / 同步状态 / 上限。 */
@Composable
private fun ChainStrategyTooltip(value: ChainStrategy) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.width(240.dp)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ChainStrategyInfo(value)
        }
        HorizontalDivider()
        if (value is ChainStrategy.Neighborhood) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(component = HSLang.ChainDoors.strategyRadius)
                Text("${value.radius}")
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(component = HSLang.ChainDoors.strategyShape)
                Text(value.shape.translateText)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(component = HSLang.ChainDoors.strategySameBlock)
            Text(component = IGLang.Misc.coloredSwitch(value.sameBlock))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(component = HSLang.ChainDoors.strategySyncState)
            Text(component = IGLang.Misc.coloredSwitch(value.syncState))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(component = HSLang.ChainDoors.strategyLimit)
            Text("${value.limit}")
        }
    }
}

@Composable
private fun ChainStrategyInfo(
    value: ChainStrategy
) {
    when (value) {
        is ChainStrategy.Neighborhood ->
            Text(ChainStrategy.Neighborhood.text)

        is ChainStrategy.Recursive    ->
            Text(ChainStrategy.Recursive.text)
    }
}

@Composable
private fun ChainStrategyEditor(
    value: ChainStrategy,
    onValueChange: (ChainStrategy) -> Unit,
) {
    var isNeighborhood by remember { mutableStateOf(value is ChainStrategy.Neighborhood) }


    var neighborhoodValue by remember { mutableStateOf(value as? ChainStrategy.Neighborhood ?: ChainStrategy.Neighborhood.DEFAULT) }

    var recursiveValue by remember { mutableStateOf(value as? ChainStrategy.Recursive ?: ChainStrategy.Recursive.DEFAULT) }

    LaunchedEffect(isNeighborhood, neighborhoodValue) {
        if (isNeighborhood) {
            onValueChange(neighborhoodValue)
        }
    }
    LaunchedEffect(isNeighborhood, recursiveValue) {
        if (!isNeighborhood) {
            onValueChange(recursiveValue)
        }
    }


    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(RadioButtonDefaults.spacing)) {
            RadioButton(
                selected = isNeighborhood,
                index = 0,
                count = 2,
                onSelect = { isNeighborhood = true },
                // tip 挂在按钮上：RadioButtonGroup 的 item DSL 给不了每段自己的 tip
                modifier = Modifier.tooltip { Text(ChainStrategy.Neighborhood.hoverText) },
            ) {
                Text(ChainStrategy.Neighborhood.text)
            }
            RadioButton(
                selected = !isNeighborhood,
                index = 1,
                count = 2,
                onSelect = { isNeighborhood = false },
                modifier = Modifier.tooltip { Text(ChainStrategy.Recursive.hoverText) },
            ) {
                Text(ChainStrategy.Recursive.text)
            }
        }
        Spacer(Modifier.height(16.dp))

        Column(Modifier.width(480.dp)) {
            val width = 160.dp
            AnimatedContent(
                targetState = isNeighborhood,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                    } else {
                        (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                    }.using(SizeTransform(clip = true))
                },
            ) { neighborhood ->
                if (neighborhood) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategyRadius,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategyRadiusComment) },
                            )
                            IntField(neighborhoodValue.radius, { neighborhoodValue = neighborhoodValue.copy(radius = it) }, modifier = Modifier.width(width))
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategyShape,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategyShapeComment) },
                            )
                            EnumSelector(
                                selected = neighborhoodValue.shape,
                                onSelect = { neighborhoodValue = neighborhoodValue.copy(shape = it) },
                                items = ChainStrategy.Neighborhood.Shape.entries,
                                modifier = Modifier.width(width),
                            )
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategySameBlock,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategySameBlockComment) },
                            )
                            Switch(neighborhoodValue.sameBlock, { neighborhoodValue = neighborhoodValue.copy(sameBlock = it) })
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategySyncState,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategySyncStateComment) },
                            )
                            Switch(neighborhoodValue.syncState, { neighborhoodValue = neighborhoodValue.copy(syncState = it) })
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategyLimit,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategyLimitComment) },
                            )
                            IntField(neighborhoodValue.limit, { neighborhoodValue = neighborhoodValue.copy(limit = it) }, modifier = Modifier.width(width))
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategySameBlock,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategySameBlockComment) },
                            )
                            Switch(recursiveValue.sameBlock, { recursiveValue = recursiveValue.copy(sameBlock = it) })
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategySyncState,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategySyncStateComment) },
                            )
                            Switch(recursiveValue.syncState, { recursiveValue = recursiveValue.copy(syncState = it) })
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                component = HSLang.ChainDoors.strategyLimit,
                                modifier = Modifier.tooltip { Text(component = HSLang.ChainDoors.strategyLimitComment) },
                            )
                            IntField(recursiveValue.limit, { recursiveValue = recursiveValue.copy(limit = it) }, modifier = Modifier.width(width))
                        }
                    }
                }
            }
        }
    }
}