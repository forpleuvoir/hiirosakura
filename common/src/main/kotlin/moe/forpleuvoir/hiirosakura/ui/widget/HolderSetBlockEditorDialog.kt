package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.common.util.requireType
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonGroup
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.hiirosakura.ui.compat.OutlinedCard
import moe.forpleuvoir.hiirosakura.ui.compat.RemoveButton

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
    value: HolderSet<Block>,
    onValueChange: (HolderSet<Block>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    val firstTag = blockTags.findFirst().getOrNull()
    var tag by remember {
        mutableStateOf(
            if (value is HolderSet.Named) value.key() else firstTag
        )
    }


    var mode by remember { mutableStateOf(value !is HolderSet.Named) }
    LaunchedEffect(tag) {
        if (tag == null) mode = true
    }

    val blocks = rememberKeyedList(value.map { it.value() }.toList())

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val list = HolderSet.direct(blocks.entries.values().map { BuiltInRegistries.BLOCK.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet ?: list
            )
            true
        },
        content = {
            Column {
                firstTag?.let {
                    RadioButtonGroup(
                        selected = if (mode) 0 else 1,
                        onSelect = { mode = it == 0 },
                    ) {
                        item { Text(component = HSLang.ItemEditor.fromRegistry) }
                        item { Text(component = HSLang.ItemEditor.fromTag) }
                    }
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
                                Button(
                                    onClick = { showItemSelector = true },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                ) {
                                    Text(component = HSLang.ItemEditor.addFromRegistry)
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
                                                        if (!blocks.entries.any { keyed -> keyed.value == block }) {
                                                            blocks.add(block)
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
                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    BlockTagSelector(
                                        it,
                                        { tag ->
                                            tag.blocks.forEach { item ->
                                                if (!blocks.entries.any { it.value == item.value() }) {
                                                    blocks.add(item.value())
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        contentPadding = LabeledFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
                                        content = {
                                            Text(component = HSLang.ItemEditor.addFromTag)
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            LabelBox(
                                { Text(component = HSLang.ItemEditor.items) },
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
                                        itemsIndexed(blocks.entries, key = { _, keyed -> keyed.key }) { index, (_, block) ->
                                            val interactionSource = remember { MutableInteractionSource() }

                                            val isHovered by interactionSource.collectIsHoveredAsState()
                                            OutlinedCard(
                                                Modifier
                                                    .size(48.dp)
                                                    .hoverable(interactionSource)
                                                    .tooltip(interactionSource) {
                                                        Column {
                                                            Text(block.name)
                                                            Spacer(Modifier.height(4.dp))
                                                            Text(block.key.toString(), color = SokitsuTheme.colorScheme.primaryContainer)
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

                                    VerticalScroller(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        adapter = rememberScrollerAdapter(lazyListState, 9)
                                    )
                                }
                            }
                        }

                    } else {
                        tag?.let { BlockTagSelector(it, { tag = it }) }
                            ?: Text(component = IGLang.Misc.hasNothing)
                    }
                }
            }
        }
    )
}


@Composable
fun BlockTagSelector(
    selected: TagKey<Block>,
    onSelect: (TagKey<Block>) -> Unit,
    items: List<TagKey<Block>> = blockTags.toList(),
    itemEquals: (TagKey<Block>, TagKey<Block>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<Block>) -> Unit = {
        Row(Modifier.tooltip {
            val types = it.blocks
            if (types.count() == 0) {
                Text(component = IGLang.Misc.hasNothing)
                return@tooltip
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
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<Block>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.tooltip {
            val types = item.blocks
            if (types.count() == 0) {
                Text(component = IGLang.Misc.hasNothing)
                return@tooltip
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
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) {
        LabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
                Selector(
                selected = selected,
                onSelect = onSelect,
                items = items,
                itemEquals = itemEquals,
                content = content,
                itemContent = itemContent,
                enabled = enabled,
                itemLeadingIcon = itemLeadingIcon,
                itemTrailingIcon = itemTrailingIcon,
                searchFilter = searchFilter?.let { filter -> { item, query -> filter(query, item) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
