package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy
import moe.forpleuvoir.hiirosakura.ui.icon.default.Link2
import moe.forpleuvoir.hiirosakura.ui.icon.default.Radar
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ListConfigWrapperDefaults
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configChainDoorsRuleList(name: String, defaultValue: List<ChainDoorsRule>) = configList(name, defaultValue, ChainDoorsRule)

//------------ UI Wrapper ------------\\

@Composable
fun ChainDoorsRuleListConfigWrapper(
    config: ConfigList<ChainDoorsRule>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(InlineStyleText(config.translateText.plainText))
    },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(40.dp).size(1200.dp, 800.dp),
) = ListConfigWrapperDefaults.run {
    CompositionLocalProvider(
        ItemBrowserDefaults.LocalItemIconSize provides 32.dp
    ) {
        var showEditDialog by remember { mutableStateOf(false) }
        RowWrapper(
            config = config,
            modifier = modifier
        ) { showEditDialog = true }
        if (showEditDialog) {
            val editingValue = remember {
                config.mapIndexed { index, value -> index.toLong() to value }.toMutableStateList()
            }
            var nextKey by remember { mutableLongStateOf(editingValue.size.toLong()) }
            EditDialog(
                config = config,
                editingValue = editingValue,
                modifier = dialogModifier,
                title = editorDialogTitle,
                onDismissRequest = { showEditDialog = false },
                onConfirmRequest = {
                    config.clear()
                    it.forEach { (_, value) -> config.add(value) }
                    true
                }
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        EditDialogContentHeader(
                            contentHeader = {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current)) {
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(HSLang.ChainDoors.originDoor)
                                    }
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(HSLang.ChainDoors.chainDoor)
                                    }
                                    Box(Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                                        Text(HSLang.ChainDoors.keyToggleMode)
                                    }
                                    Box(Modifier.width(180.dp), contentAlignment = Alignment.Center) {
                                        Text(HSLang.ChainDoors.strategy)
                                    }
                                }
                            }
                        )
                    },
                    addDialog = { onDismissRequest ->
                        var rule by remember { mutableStateOf(ChainDoorsRule.MOB_INTERACTABLE_DOORS) }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(IGLang.Misc.add) },
                            text = {
                                IGCompositionLocalProvider {
                                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        BlockInfoMatcherDisplayerInnerEditor(
                                            rule.originDoor,
                                            { rule = rule.copy(originDoor = it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Text(HSLang.ChainDoors.originDoor)
                                            }
                                        )
                                        BlockInfoMatcherDisplayerInnerEditor(
                                            rule.chainDoor,
                                            { rule = rule.copy(chainDoor = it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Text(HSLang.ChainDoors.chainDoor)
                                            }
                                        )
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(HSLang.ChainDoors.keyToggleMode)
                                            Switch(rule.keyToggleMode, { rule = rule.copy(keyToggleMode = it) })
                                        }
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(HSLang.ChainDoors.keyToggleMode)
                                            ChainStrategyDisplayerEditor(
                                                rule.strategy, { rule = rule.copy(strategy = it) }, modifier = Modifier.width(250.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        editingValue.add(nextKey++ to rule)
                                        onDismissRequest()
                                    }
                                ) {
                                    Text(IGLang.Misc.confirm)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = onDismissRequest) {
                                    Text(IGLang.Misc.cancel)
                                }
                            },
                        )
                    }
                ) { lazyListState ->
                    EditDialogContentList(
                        data = editingValue,
                        key = { it.first },
                        modifier = Modifier,
                        lazyListState = lazyListState
                    ) { (key, value), onValueChange ->
                        Row(
                            Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //origin
                            BlockInfoMatcherDisplayerInnerEditor(
                                value = value.originDoor,
                                onValueChange = { onValueChange(key to value.copy(originDoor = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //chain
                            BlockInfoMatcherDisplayerInnerEditor(
                                value = value.chainDoor,
                                onValueChange = { onValueChange(key to value.copy(chainDoor = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //keyToggleMode
                            Box(Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                                Switch(
                                    checked = value.keyToggleMode,
                                    onCheckedChange = { onValueChange(key to value.copy(keyToggleMode = it)) },
                                )
                            }
                            //strategy
                            ChainStrategyDisplayerEditor(
                                value.strategy,
                                { onValueChange(key to value.copy(strategy = it)) },
                                modifier = Modifier.width(180.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChainStrategyDisplayerEditor(
    value: ChainStrategy,
    onValueChange: (ChainStrategy) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    AssistChip(
        {},
        modifier = modifier.plainTooltip {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.width(240.dp)) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ChainStrategyInfo(value)
                }
                HorizontalDivider()
                if (value is ChainStrategy.Neighborhood) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(HSLang.ChainDoors.strategyRadius)
                        Text("${value.radius}")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(HSLang.ChainDoors.strategyShape)
                        Text(value.shape.translateText)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(HSLang.ChainDoors.strategySameBlock)
                    Text(IGLang.Misc.coloredSwitch(value.sameBlock))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(HSLang.ChainDoors.strategySyncState)
                    Text(IGLang.Misc.coloredSwitch(value.syncState))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(HSLang.ChainDoors.strategyLimit)
                    Text("${value.limit}")
                }
            }
        },
        label = {
            ChainStrategyInfo(value)
        },
        leadingIcon = {
            when (value) {
                is ChainStrategy.Neighborhood -> Icon(Icons.Radar, null)
                is ChainStrategy.Recursive    -> Icon(Icons.Link2, null)
            }
        },
        trailingIcon = {
            IconButton({
                showDialog = true
            }) {
                Icon(Icons.EditNote, null)
            }
        }
    )
    if (showDialog) {
        var editingValue by remember { mutableStateOf(value) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(HSLang.ChainDoors.strategy) },
            text = {
                IGCompositionLocalProvider(
                    LocalNumberFieldStyle provides NumberFieldStyle.Outlined,
                ) {
                    ChainStrategyEditor(editingValue) { editingValue = it }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(editingValue)
                        showDialog = false
                    }
                ) {
                    Text(IGLang.Misc.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(IGLang.Misc.cancel)
                }
            },
        )
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
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = isNeighborhood,
                onClick = { isNeighborhood = true },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(ChainStrategy.Neighborhood.text, Modifier.plainTooltip { Text(ChainStrategy.Neighborhood.hoverText) })
            }
            SegmentedButton(
                selected = !isNeighborhood,
                onClick = { isNeighborhood = false },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(ChainStrategy.Recursive.text, Modifier.plainTooltip { Text(ChainStrategy.Recursive.hoverText) })
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
                    }.using(SizeTransform(clip = false))
                },
            ) { neighborhood ->
                if (neighborhood) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategyRadius)
                            IntField(neighborhoodValue.radius, { neighborhoodValue = neighborhoodValue.copy(radius = it) }, modifier = Modifier.width(width))
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategyShape)
                            EnumSelector(neighborhoodValue.shape, { neighborhoodValue = neighborhoodValue.copy(shape = it) }, modifier = Modifier.width(width))
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategySameBlock)
                            Switch(neighborhoodValue.sameBlock, { neighborhoodValue = neighborhoodValue.copy(sameBlock = it) })
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategySyncState)
                            Switch(neighborhoodValue.syncState, { neighborhoodValue = neighborhoodValue.copy(syncState = it) })
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategyLimit)
                            IntField(neighborhoodValue.limit, { neighborhoodValue = neighborhoodValue.copy(limit = it) }, modifier = Modifier.width(width))
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategySameBlock)
                            Switch(recursiveValue.sameBlock, { recursiveValue = recursiveValue.copy(sameBlock = it) })
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategySyncState)
                            Switch(recursiveValue.syncState, { recursiveValue = recursiveValue.copy(syncState = it) })
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(HSLang.ChainDoors.strategyLimit)
                            IntField(recursiveValue.limit, { recursiveValue = recursiveValue.copy(limit = it) }, modifier = Modifier.width(width))
                        }
                    }
                }
            }
        }
    }
}