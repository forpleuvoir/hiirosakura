package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.util.rememberSegmentedButtonWidth
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.common.util.requireType
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

internal val blockTags
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).tags.map { it.key() }

internal val TagKey<Block>.blocks
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).getTagOrEmpty(this)

internal val TagKey<Block>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).tags.filter {
        it.key().location == this.location
    }.findFirst().get()


@Composable
fun HolderSetBlockEditorDialog(
    key: Identifier,
    value: HolderSet<Block>,
    onValueChange: (HolderSet<Block>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    var tag by remember {
        mutableStateOf(
            if (value is HolderSet.Named) value.key()
            else blockTags.findFirst().get()
        )
    }

    var mode by remember { mutableStateOf(value !is HolderSet.Named) }
    val blocks = rememberKeyedList(value.map { it.value() }.toList())
    var nextKey by remember { mutableLongStateOf(blocks.size.toLong()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val result = if (mode) {
                HolderSet.direct(blocks.values().map { BuiltInRegistries.BLOCK.wrapAsHolder(it) })
            } else {
                tag.asHolderSet
            }
            onValueChange(result)
            true
        },
        content = {
            Column {
                SingleChoiceSegmentedButtonRow {
                    val buttonTexts = listOf(
                        key.asTranslateText(suffix = "from_blocks", fallback = "From Blocks"),
                        key.asTranslateText(suffix = "from_tag", fallback = "From Tag"),
                    )
                    val width = rememberSegmentedButtonWidth(
                        items = buttonTexts,
                        textStyle = MaterialTheme.typography.labelLarge,
                    ) { it.toAnnotatedString() }
                    SegmentedButton(
                        selected = mode,
                        onClick = { mode = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.width(width),
                        label = { Text(key, suffix = "from_blocks", fallback = "From Blocks") }
                    )
                    SegmentedButton(
                        selected = !mode,
                        onClick = { mode = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.width(width),
                        label = { Text(key, suffix = "from_tag", fallback = "From Tag") }
                    )
                }
                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        if (targetState) {
                            // false -> true：新内容从左侧进入
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        } else {
                            // true -> false：新内容从右侧进入
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        }
                    },
                    label = "ModeSlide",
                ) { currentMode ->
                    if (currentMode) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                var showItemSelector by remember { mutableStateOf(false) }
                                OutlinedButton(
                                    { showItemSelector = true },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    shape = MaterialTheme.shapes.extraSmall,
                                ) {
                                    Text(key, suffix = "add_block", fallback = "Add Block")
                                }
                                if (showItemSelector) {
                                    FlexibleDialog(
                                        onDismissRequest = { showItemSelector = false },
                                        onConfirmRequest = { true },
                                        content = {
                                            ItemBrowser(
                                                itemDisplay = {
                                                    ItemBrowserDefaults.ItemWrapper(it) { selected ->
                                                        val block = if (selected is BlockItem) {
                                                            selected.block
                                                        } else selected.requireType<Block>()
                                                        if (!blocks.any { keyed -> keyed.value == block }) {
                                                            blocks.add(Keyed(nextKey++, block))
                                                        }
                                                        showItemSelector = false
                                                    }
                                                },
                                                modifier = Modifier.size(680.dp, 520.dp)
                                            )
                                        },
                                        confirmButton = {},
                                        dismissButton = {}
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                BlockTagSelector(
                                    blockTags.findFirst().get(),
                                    { tag ->
                                        tag.blocks.forEach { item ->
                                            if (!blocks.any { it.value == item.value() }) {
                                                blocks.add(Keyed(nextKey++, item.value()))
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
                                    content = {
                                        Text(key, suffix = "add_from_tag", fallback = "Add From Tag")
                                    }
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedLabelBox(
                                { Text(key, suffix = "items", fallback = "Items") },
                            ) {
                                Box(Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyListState = rememberLazyGridState()
                                    LazyVerticalGrid(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        columns = GridCells.Fixed(9),
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        contentPadding = PaddingValues(if (lazyListState.canScroll) 12.dp else 0.dp),
                                        state = lazyListState
                                    ) {
                                        itemsIndexed(blocks, key = { _, keyed -> keyed.key }) { index, (_, block) ->
                                            val interactionSource = remember { MutableInteractionSource() }
                                            val isHovered by interactionSource.collectIsHoveredAsState()
                                            OutlinedCard(
                                                Modifier
                                                    .size(48.dp)
                                                    .hoverable(interactionSource)
                                                    .plainTooltip(interactionSource) {
                                                        Column {
                                                            Text(block.name)
                                                            Spacer(Modifier.height(4.dp))
                                                            Text(block.key.toString(), color = MaterialTheme.colorScheme.primaryContainer)
                                                        }
                                                    },
                                            ) {
                                                Box(Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                                                    AnimatedContent(
                                                        targetState = isHovered,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                        transitionSpec = {
                                                            if (targetState) {
                                                                // 删除按钮从右边进入，物品图标向左离开
                                                                slideIntoContainer(
                                                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                                                    animationSpec = tween(180),
                                                                ) togetherWith slideOutOfContainer(
                                                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                                                    animationSpec = tween(180),
                                                                )
                                                            } else {
                                                                // 物品图标从左边返回，删除按钮向右离开
                                                                slideIntoContainer(
                                                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                                                    animationSpec = tween(180),
                                                                ) togetherWith slideOutOfContainer(
                                                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                                                    animationSpec = tween(180),
                                                                )
                                                            }
                                                        },
                                                        contentAlignment = Alignment.Center,
                                                        label = "ItemRemoveButton",
                                                    ) { hovered ->
                                                        if (hovered) {
                                                            RemoveButton {
                                                                blocks.removeAt(index)
                                                            }
                                                        } else {
                                                            ItemIcon(
                                                                ItemStack(block),
                                                                scaleOnHover = 1f,
                                                                showTooltip = false,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        adapter = rememberScrollbarAdapter(lazyListState)
                                    )
                                }
                            }
                        }

                    } else {
                        BlockTagSelector(tag, { tag = it })
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockTagSelector(
    selected: TagKey<Block>,
    onSelect: (TagKey<Block>) -> Unit,
    items: List<TagKey<Block>> = blockTags.toList(),
    itemEquals: (TagKey<Block>, TagKey<Block>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<Block>) -> Unit = {
        Row(Modifier.plainTooltip {
            val types = it.blocks
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    types.take(20).forEach { item ->
                        ItemBrowserDefaults.ItemWrapper(item.value(), scaleOnHover = 1f, showTooltip = false, border = false, modifier = Modifier.size(36.dp))
                    }
                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${it.location}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<Block>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.plainTooltip {
            val types = item.blocks
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    types.take(20).forEach { item ->
                        ItemBrowserDefaults.ItemWrapper(item.value(), scaleOnHover = 1f, showTooltip = false, border = false, modifier = Modifier.size(36.dp))
                    }
                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${item.location}")
        }
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<Block>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.blocks.any { str in it.value().name.plainText }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<Block>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<Block>) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = Selector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding
)
