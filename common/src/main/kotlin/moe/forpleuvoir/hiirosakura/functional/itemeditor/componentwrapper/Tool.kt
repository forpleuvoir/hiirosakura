package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.NullableInputTransformation
import moe.forpleuvoir.hiirosakura.ui.util.NullableOutputTransformation
import moe.forpleuvoir.hiirosakura.ui.util.NullableTrailingIcon
import moe.forpleuvoir.hiirosakura.ui.widget.HolderSetBlockEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.ReorderableEditorVerticalGrid
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                        verticalAlignment = Alignment.Bottom,
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
                        OutlinedToggleButton(
                            canDestroyBlocksInCreative,
                            { canDestroyBlocksInCreative = it },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(key, suffix = "can_destroy_blocks_in_creative")
                            Spacer(Modifier.width(8.dp))
                            Text(IGLang.Misc.coloredSwitch(canDestroyBlocksInCreative))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedLabelBox(
                        label = {
                            Text(key, suffix = "rules", fallback = "Rules")
                        },
                    ) {
                        var nextKey by remember { mutableLongStateOf(rules.size.toLong()) }
                        ReorderableEditorVerticalGrid(
                            rules,
                            key = { it.key },
                            onMove = rules::moveElement,
                            columns = GridCells.Adaptive(360.dp),
                            floatingActionButton = { lazyGridState ->
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
                        ) { index, rule, isDragging, hapticFeedback ->
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
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .then(tip),
            contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
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
            inputTransformation = NullableInputTransformation(value.speed.isEmpty),
            outputTransformation = NullableOutputTransformation(value.speed.isEmpty),
            labelPosition = TextFieldLabelPosition.Attached(true),
            label = { Text(key, suffix = "speed", fallback = "Speed") },
            trailingIcon = {
                NullableTrailingIcon(value.speed.isPresent){
                    onValueChange(value.copy(speed = Optional.empty()))
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
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