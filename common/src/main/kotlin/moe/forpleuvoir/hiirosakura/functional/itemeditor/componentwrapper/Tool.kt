package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.CommentAppendMode
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetBlockEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Add
import moe.forpleuvoir.ibukigourd.ui.icon.default.Delete
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.fabVisibilityAnimation
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberFabVisibilityByScroll
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.ibukigourd.util.moveElement
import net.minecraft.core.HolderSet
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.Tool
import net.minecraft.world.level.block.Block
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Composable
fun ToolComponentWrapper(
    key: Identifier,
    value: Tool,
    onValueChange: (Tool) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height)) {
        var showDialog by remember { mutableStateOf(false) }
        IconButton(onClick = {
            showDialog = true
        }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(Icons.EditNote, null)
        }
        if (showDialog) {
            ToolEditorDialog(
                key = key,
                value = value,
                onValueChange = onValueChange,
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

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

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            onValueChange(Tool(rules.values().toMutableList(), defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative))
            true
        },
        modifier = Modifier
            .padding(24.dp),
        content = {
            BoxWithConstraints {
                Column(
                    modifier = Modifier
                        .size(if (maxWidth > 1160.dp) 1160.dp else 800.dp, 920.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FloatField(
                            defaultMiningSpeed,
                            { defaultMiningSpeed = it },
                            modifier = Modifier.weight(1f, false),
                            label = { Text(key, suffix = "default_mining_speed", fallback = "Default Mining Speed") }
                        )
                        IntField(
                            damagePerBlock,
                            { damagePerBlock = it },
                            modifier = Modifier.weight(1f, false),
                            label = { Text(key, suffix = "damage_per_block", fallback = "Damage Per Block") }
                        )
                        Box(
                            modifier = Modifier.height(68.dp).padding(top = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Switch(
                                canDestroyBlocksInCreative,
                                { canDestroyBlocksInCreative = it },
                                modifier = Modifier.plainTooltip {
                                    Text(
                                        key,
                                        suffix = "can_destroy_blocks_in_creative",
                                        fallback = "Can Destroy Blocks In Creative",
                                        commentAppendMode = CommentAppendMode.Append(true)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedLabelBox(
                        label = {
                            Text(key, suffix = "rules", fallback = "Rules")
                        },
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            var nextKey by remember { mutableLongStateOf(rules.size.toLong()) }
                            val lazyGridState = rememberLazyGridState()
                            if (rules.isEmpty()) {
                                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                val hapticFeedback = LocalHapticFeedback.current
                                val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
                                    rules.moveElement(from.index, to.index)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                }
                                LazyVerticalGrid(
                                    state = lazyGridState,
                                    columns = GridCells.Adaptive(360.dp),
                                    modifier = Modifier.padding(end = if (lazyGridState.canScroll) 12.dp else 0.dp).fillMaxHeight(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    itemsIndexed(rules, key = { _, v -> v.key }) { index, rule ->
                                        ReorderableItem(reorderableLazyGridState, rule.key) { isDragging ->
                                            val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                                            val handleInteraction = remember { MutableInteractionSource() }
                                            val handleHovered by handleInteraction.collectIsHoveredAsState()
                                            RuleCard(
                                                rule.value,
                                                {
                                                    rules[index] = rule.copyValue(it)
                                                },
                                                modifier = Modifier.scale(scale).width(360.dp),
                                                onRemove = {
                                                    rules.removeAt(index)
                                                },
                                                key = key,
                                                hapticFeedback = hapticFeedback,
                                                handleInteraction = handleInteraction,
                                                handleHovered = handleHovered,
                                                isDragging = isDragging,
                                            )
                                        }
                                    }
                                }

                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(lazyGridState),
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                            var showAddDialog by remember { mutableStateOf(false) }
                            FloatingActionButton(
                                onClick = {
                                    showAddDialog = true
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .size(40.dp)
                                    .fabVisibilityAnimation(rememberFabVisibilityByScroll(lazyGridState))
                            ) {
                                Icon(Icons.Add, IGLang.Misc.add.plainText)
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
                                        rules.addLast(Keyed(nextKey++, addingEntry))
                                        true
                                    },
                                    title = { Text(IGLang.Misc.add) },
                                    content = {
                                        RuleContent(addingEntry, { addingEntry = it }, key)
                                    }
                                )
                            }
                        }

                    }

                }
            }
        }
    )
}

@Composable
private fun ReorderableCollectionItemScope.RuleCard(
    value: Tool.Rule,
    onValueChange: (Tool.Rule) -> Unit,
    onRemove: () -> Unit,
    key: Identifier,
    hapticFeedback: HapticFeedback,
    handleInteraction: MutableInteractionSource,
    handleHovered: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                DragHandle(
                    hapticFeedback,
                    handleInteraction,
                    handleHovered,
                    isDragging
                )
                RemoveConfirmButton(
                    key.asTranslateText(suffix = "rule", fallback = "Rule").plainText,
                    onRemove
                )
            }
            RuleContent(value, onValueChange, key)
        }
    }
}

@Composable
private fun RuleContent(
    value: Tool.Rule,
    onValueChange: (Tool.Rule) -> Unit,
    key: Identifier
) {
    Column {
        //blocks
        val count = value.blocks.count()
        var showDialog by remember { mutableStateOf(false) }
        val tip = if (count > 0) {
            Modifier.plainTooltip {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    value.blocks.take(20).forEach { item ->
                        ItemBrowserDefaults.ItemWrapper(
                            item.value(),
                            border = false,
                            scaleOnHover = 1f,
                            showTooltip = false,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        } else Modifier
        OutlinedLabelBox(
            modifier = Modifier.height(64.dp)
                .fillMaxWidth()
                .then(tip),
            contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 4.dp, bottom = 4.dp),
            label = {
                Text(key, suffix = "blocks", fallback = "Blocks")
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    value.blocks.take(6).forEach { item ->
                        ItemBrowserDefaults.ItemWrapper(
                            item.value(),
                            border = false,
                            scaleOnHover = 1f,
                            showTooltip = false,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    if (count > 6)
                        Text("...", overflow = TextOverflow.Ellipsis, maxLines = 1)
                    if (count == 0)
                        Text(IGLang.Misc.hasNothing, overflow = TextOverflow.Ellipsis, maxLines = 1)


                }
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }

            if (showDialog) {
                HolderSetBlockEditorDialog(
                    key = key,
                    value = value.blocks,
                    onValueChange = { onValueChange(value.copy(blocks = it)) },
                    onDismissRequest = { showDialog = false },
                    title = { Text(key) }
                )
            }
        }
        //speed
        Spacer(Modifier.height(12.dp))
        FloatField(
            value = value.speed.getOrNull() ?: 0f,
            onValueChange = { onValueChange(value.copy(speed = Optional.of(it))) },
            range = 0f..Float.MAX_VALUE,
            outputTransformation = if (value.speed.isEmpty) {
                OutputTransformation {
                    replace(start = 0, end = length, text = HSLang.Common.unset.plainText)
                }
            } else null,
            labelPosition = TextFieldLabelPosition.Attached(true),
            label = { Text(key, suffix = "speed", fallback = "Speed") },
            trailingIcon = {
                if (value.speed.isPresent) {
                    IconButton(onClick = { onValueChange(value.copy(speed = Optional.ofNullable(null))) }) {
                        Icon(Icons.Delete, null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        //correctForDrops
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp),
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