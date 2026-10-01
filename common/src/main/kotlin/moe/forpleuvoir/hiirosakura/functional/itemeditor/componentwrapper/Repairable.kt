package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.ItemIconButton
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Repairable
import kotlin.jvm.optionals.getOrNull
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.RadioButtonGroup
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SurfaceDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility

@Composable
fun RepairableComponentWrapper(
    key: Identifier,
    value: Repairable,
    onValueChange: (Repairable) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val count = value.items.count()
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.thenIf(count > 0) {
            Modifier.tooltip {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    value.items.take(20).forEach { item ->
                        ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                    }
                }
            }
        },
        onEdit = { showDialog = true },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            value.items.take(6).forEach { item ->
                ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
            }

            if (count > 6)
                Text("...", overflow = TextOverflow.Ellipsis, maxLines = 1)
            if (count == 0)
                Text(component = IGLang.Misc.hasNothing, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
    }

    if (showDialog) {
        HolderSetItemEditorDialog(
            value = value.items,
            onValueChange = { onValueChange(Repairable(it)) },
            onDismissRequest = { showDialog = false },
            title = { DataComponentDialogTitle(key) }
        )
    }
}

/** 物品图标网格的尺寸约束。 */
private object ItemGridDefaults {

    /** 物品格的边长。 */
    val CellSize: Dp = 48.dp

    /** 物品格之间的间距。 */
    val CellSpacing: Dp = 3.dp

    /** 一行放得下的物品格数。 */
    const val Columns: Int = 9

    /** 物品网格右侧细滚动条的占位宽度。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 浮层宽度下限：一行放得下 [Columns] 个物品格。 */
    val DialogMinWidth: Dp
        get() = CellSize * Columns + CellSpacing * (Columns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层宽度上限。 */
    val DialogMaxWidth: Dp
        get() = DialogMinWidth + 160.dp

    /** 浮层高度上限。 */
    val DialogMaxHeight: Dp = 900.dp

    /** 浮层尺寸约束：宽度只夹上下限，具体宽度由窗口决定。 */
    val DialogModifier: Modifier
        get() = Modifier
            .widthIn(min = DialogMinWidth, max = DialogMaxWidth)
            .heightIn(max = DialogMaxHeight)
}

private val itemTags
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.map { it.key() }

private val TagKey<Item>.items
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).getTagOrEmpty(this)

private val TagKey<Item>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.filter {
        it.key().location == this.location
    }.findFirst().get()


/**
 * 物品集合编辑浮层：表头是来源模式开关与新增控件，下面是 9 列物品图标网格。
 *
 * 物品不是可编辑字段，网格保持图标格结构：悬停时格子换成带二次确认的删除按钮，
 * 新增走右下角的浮动按钮（[ItemBrowser]）；增删与模式切换都在副本上做，确认时才写回。
 *
 * @param value 待编辑的物品集合
 * @param onValueChange 确认时的写回
 * @param onDismissRequest 关闭请求
 * @param title 浮层标题
 */
@Composable
fun HolderSetItemEditorDialog(
    value: HolderSet<Item>,
    onValueChange: (HolderSet<Item>) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    val firstTag = itemTags.findFirst().getOrNull()
    var tag by remember {
        mutableStateOf(
            if (value is HolderSet.Named) value.key() else firstTag
        )
    }


    var mode by remember { mutableStateOf(value !is HolderSet.Named) }
    LaunchedEffect(tag) {
        if (tag == null) mode = true
    }

    val items = rememberKeyedList(value.map { it.value() }.toList())
    var showItemSelector by remember { mutableStateOf(false) }
    // 物品网格的滚动状态：浮动添加按钮的显隐以它为准
    val gridState = rememberLazyGridState()
    val addButtonVisibility = rememberFabScrollVisibility(gridState)

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val list = HolderSet.direct(items.entries.values().map { BuiltInRegistries.ITEM.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet ?: list
            )
            true
        },
        modifier = ItemGridDefaults.DialogModifier,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fabScrollVisibility(addButtonVisibility),
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
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
                        }
                    },
                ) { _ ->
                    AnimatedContent(
                        targetState = mode,
                        transitionSpec = {
                            if (targetState) {
                                slideInHorizontally { -it } togetherWith
                                        slideOutHorizontally { it }
                            } else {
                                slideInHorizontally { it } togetherWith
                                        slideOutHorizontally { -it }
                            }
                        },
                        label = "ModeSlide",
                    ) { currentMode ->
                        if (currentMode) {
                            Column {
                                firstTag?.let {
                                    ItemTagSelector(
                                        it,
                                        { tag ->
                                            tag.items.forEach { item ->
                                                if (!items.entries.any { it.value == item.value() }) {
                                                    items.add(item.value())
                                                }
                                            }
                                        },
                                        content = {
                                            Text(component = HSLang.ItemEditor.addFromTag)
                                        }
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                DataComponentSection(title = {
                                    Text(component = HSLang.ItemEditor.items, fontSize = SokitsuTheme.typography.body.fontSize)
                                }) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().height(460.dp),
                                        sprite = SurfaceDefaults.embeddedPanel,
                                    ) {
                                        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                            LazyVerticalGrid(
                                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                                columns = GridCells.Fixed(ItemGridDefaults.Columns),
                                                verticalArrangement = Arrangement.spacedBy(ItemGridDefaults.CellSpacing),
                                                horizontalArrangement = Arrangement.spacedBy(ItemGridDefaults.CellSpacing),
                                                state = gridState
                                            ) {
                                                itemsIndexed(items.entries, key = { _, keyed -> keyed.key }) { index, (_, item) ->
                                                    val interactionSource = remember { MutableInteractionSource() }

                                                    val isHovered by interactionSource.collectIsHoveredAsState()
                                                    Surface(
                                                        Modifier
                                                            .size(ItemGridDefaults.CellSize)
                                                            .hoverable(interactionSource)
                                                            .tooltip(interactionSource) {
                                                                Column {
                                                                    Text(item.asItem().name)
                                                                    Spacer(Modifier.height(4.dp))
                                                                    Text(item.asItem().key.toString(), color = SokitsuTheme.colorScheme.primaryContainer)
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
                                                                    RemoveConfirmButton(
                                                                        message = item.asItem().name.plainText,
                                                                        onConfirm = { items.removeAt(index) },
                                                                        iconScale = LocalIconScale.current,
                                                                        contentPadding = EditDialogContentDefaults.iconPadding,
                                                                    )
                                                                } else {
                                                                    ItemIcon(
                                                                        ItemStack(item),
                                                                        scaleOnHover = 1f,
                                                                        showTooltip = false,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(Modifier.width(8.dp))
                                            VerticalFlatScroller(
                                                modifier = Modifier.fillMaxHeight(),
                                                adapter = rememberScrollerAdapter(gridState, ItemGridDefaults.Columns)
                                            )
                                        }
                                    }
                                }
                            }

                        } else {
                            tag?.let { ItemTagSelector(it, { tag = it }) }
                                ?: Text(component = IGLang.Misc.hasNothing)
                        }
                    }
                }

                EditDialogAddButton(
                    visibility = addButtonVisibility,
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Button(
                        onClick = { showItemSelector = true },
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    ) {
                        Icon(Icons.Add, scale = configIconScale())
                    }
                }
            }

            if (showItemSelector) {
                FlexibleDialog(
                    onDismissRequest = { showItemSelector = false },
                    onConfirmRequest = { true },
                    content = {
                        ItemBrowser(
                            itemDisplay = {
                                ItemIconButton(it) { selected ->
                                    val item = selected.asItem()
                                    if (!items.entries.any { keyed -> keyed.value == item }) {
                                        items.add(item)
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
        }
    )
}

@Composable
fun ItemTagSelector(
    selected: TagKey<Item>,
    onSelect: (TagKey<Item>) -> Unit,
    items: List<TagKey<Item>> = itemTags.toList(),
    itemEquals: (TagKey<Item>, TagKey<Item>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<Item>) -> Unit = {
        Row(Modifier.tooltip {
            val types = it.items
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
                        ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                    }

                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${it.location}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    },
    itemContent: @Composable (TagKey<Item>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.tooltip {
            val types = item.items
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
                        ItemIcon(ItemStack(item), scaleOnHover = 1f)
                    }

                    if (types.count() > 20) Text("...")
                }
            }
        }) {
            Text("#${item.location}")
        }
    },
    enabled: Boolean = true,
    searchFilter: ((String, TagKey<Item>) -> Boolean)? = { str, tag ->
        str in tag.location.toString() || tag.items.any { str in it.value().name.plainText }
    },
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (TagKey<Item>) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (TagKey<Item>) -> Unit)?)? = null,
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
