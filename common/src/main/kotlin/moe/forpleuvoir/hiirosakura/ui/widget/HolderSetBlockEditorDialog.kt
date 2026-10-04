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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.LocalRegistryAccess
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.util.ItemRegistryHelper
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.selector.SelectorTrigger
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import kotlin.jvm.optionals.getOrNull

internal val blockTags
    @Composable get() = LocalRegistryAccess.current.lookupOrThrow(Registries.BLOCK).tags.map { it.key() }

internal fun TagKey<Block>.blocks(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.BLOCK).getTagOrEmpty(this)

internal fun TagKey<Block>.asHolderSet(registryAccess: RegistryAccess) =
    registryAccess.lookupOrThrow(Registries.BLOCK).tags.filter {
        it.key().location == this.location
    }.findFirst().get()


/**
 * 方块集合编辑浮层：表头是来源模式开关与新增控件，下面是 9 列方块图标网格。
 *
 * 方块不是可编辑字段，网格保持图标格结构：悬停时格子换成带二次确认的删除按钮，
 * 新增走「从注册表添加」的选择器（[ItemBrowser]）；增删与模式切换都在副本上做，确认时才写回。
 *
 * @param value 待编辑的方块集合
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
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
    val registryAccess = LocalRegistryAccess.current

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val list = HolderSet.direct(blocks.entries.values().map { BuiltInRegistries.BLOCK.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet(registryAccess) ?: list
            )
            true
        },
        maxWidth = AlertDialogDefaults.maxWidth + 120.dp,
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
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        } else {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        }
                    },
                    label = "ModeSlide",
                ) { currentMode ->
                    if (currentMode) {
                        Column {
                            Row {
                                BlockSelector(
                                    { block ->
                                        if (!blocks.entries.any { keyed -> keyed.value == block }) {
                                            blocks.add(block)
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(configControlHeight())
                                )
                                firstTag?.let {
                                    Spacer(Modifier.width(12.dp))
                                    BlockTagSelector(
                                        it,
                                        { tag ->
                                            tag.blocks(registryAccess).forEach { item ->
                                                if (!blocks.entries.any { keyed -> keyed.value == item.value() }) {
                                                    blocks.add(item.value())
                                                }
                                            }
                                        },
                                        content = {
                                            Text(component = HSLang.ItemEditor.addFromTag)
                                        },
                                        modifier = Modifier.weight(1f).height(configControlHeight())
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            DataComponentSection(title = {
                                Text(component = HSLang.ItemEditor.blocks, fontSize = SokitsuTheme.typography.body.fontSize)
                            }) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().height(460.dp),
                                    sprite = SurfaceDefaults.embeddedPanel,
                                ) {
                                    val gridState = rememberLazyGridState()
                                    Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                        LazyVerticalGrid(
                                            modifier = Modifier.weight(1f).fillMaxHeight(),
                                            columns = GridCells.Fixed(ItemGridDefaults.Columns),
                                            verticalArrangement = Arrangement.spacedBy(ItemGridDefaults.CellSpacing),
                                            horizontalArrangement = Arrangement.spacedBy(ItemGridDefaults.CellSpacing),
                                            state = gridState
                                        ) {
                                            itemsIndexed(blocks.entries, key = { _, keyed -> keyed.key }) { index, (_, block) ->
                                                val interactionSource = remember { MutableInteractionSource() }
                                                val isHovered by interactionSource.collectIsHoveredAsState()
                                                Surface(
                                                    Modifier
                                                        .size(ItemGridDefaults.CellSize)
                                                        .hoverable(interactionSource)
                                                        .tooltip(interactionSource) {
                                                            Column {
                                                                Text(block.name)
                                                                Spacer(Modifier.height(4.dp))
                                                                Text(block.key.toString(), color = SokitsuTheme.colorScheme.onSurfaceVariant)
                                                            }
                                                        },
                                                ) {
                                                    Box(Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                                                        var showDialog by remember { mutableStateOf(false) }
                                                        AnimatedContent(
                                                            targetState = isHovered,
                                                            modifier = Modifier
                                                                .fillMaxSize(),
                                                            transitionSpec = {
                                                                if (targetState) {
                                                                    slideIntoContainer(
                                                                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                                                        animationSpec = tween(180),
                                                                    ) togetherWith slideOutOfContainer(
                                                                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                                                        animationSpec = tween(180),
                                                                    )
                                                                } else {
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
                                                                IconButton(
                                                                    onClick = { if (isQuickAction) blocks.removeAt(index) else showDialog = true },
                                                                ) {
                                                                    Icon(Icons.Delete)
                                                                }
                                                            } else {
                                                                ItemIcon(
                                                                    ItemStack(block),
                                                                    scaleOnHover = 1f,
                                                                    showTooltip = false,
                                                                )
                                                            }
                                                        }

                                                        if (showDialog) {
                                                            SimpleAlertDialog(
                                                                onDismissRequest = { showDialog = false },
                                                                onConfirmRequest = {
                                                                    blocks.removeAt(index)
                                                                    true
                                                                },
                                                                title = {
                                                                    Text(IGLang.Misc.removeConfirm(block.name.plainText))
                                                                },
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (gridState.canScroll) {
                                            Spacer(Modifier.width(8.dp))
                                            VerticalFlatScroller(
                                                modifier = Modifier.fillMaxHeight(),
                                                adapter = rememberScrollerAdapter(gridState, ItemGridDefaults.Columns)
                                            )
                                        }
                                    }
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
private fun BlockSelector(
    onSelect: (Block) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    SelectorTrigger(
        content = {
            Text(component = HSLang.ItemEditor.addFromRegistry, overflow = TextOverflow.Ellipsis, maxLines = 1)
        },
        modifier = modifier,
        expanded = showDialog,
        onClick = {
            showDialog = true
        },
    )
    if (showDialog) {
        FlexibleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmRequest = { true },
            content = {
                ItemBrowser(
                    itemDisplay = {
                        ItemIconButton(it) { selected ->
                            when (selected) {
                                is BlockItem -> onSelect(selected.block)
                                is Block     -> onSelect(selected)
                            }
                            showDialog = false
                        }
                    },
                    filter = { it is BlockItem || it is Block },
                    searchItems = ItemRegistryHelper.allBlock.toList(),
                    modifier = Modifier.size(680.dp, 520.dp)
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun BlockTagSelector(
    selected: TagKey<Block>,
    onSelect: (TagKey<Block>) -> Unit,
    items: List<TagKey<Block>> = blockTags.toList(),
    itemEquals: (TagKey<Block>, TagKey<Block>) -> Boolean = { a, b -> a == b },
    registryAccess: RegistryAccess = LocalRegistryAccess.current,
    content: @Composable (TagKey<Block>) -> Unit = {
        Row(Modifier.tooltip {
            val types = it.blocks(registryAccess)
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
                        ItemIconButton(
                            item.value(),
                            scaleOnHover = 1f,
                            showTooltip = false,
                            hoverHighlight = false,
                            itemIconSize = DpSize(36.dp, 36.dp),
                            contentPadding = PaddingValues(0.dp)
                        )
                    }

                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${it.location}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    },
    itemContent: @Composable (TagKey<Block>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.tooltip {
            val types = item.blocks(registryAccess)
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
                        ItemIconButton(
                            item.value(),
                            scaleOnHover = 1f,
                            showTooltip = false,
                            hoverHighlight = false,
                            itemIconSize = DpSize(36.dp, 36.dp),
                            contentPadding = PaddingValues(0.dp)
                        )
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
        str in tag.location.toString() || tag.blocks(registryAccess).any { str in it.value().name.plainText }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<Block>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<Block>) -> Unit)?)? = null,
) {
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
        modifier = modifier,
    )
}
