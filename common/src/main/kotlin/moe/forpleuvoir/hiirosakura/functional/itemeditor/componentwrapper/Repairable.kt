package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.util.rememberSegmentedButtonWidth
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.selector.Selector
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import androidx.compose.ui.graphics.RectangleShape
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import moe.forpleuvoir.hiirosakura.ui.widget.SegmentedButton
import moe.forpleuvoir.hiirosakura.ui.widget.SegmentedButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.util.rememberAdaptiveGridSpan
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.foundation.layout.Row
import moe.forpleuvoir.hiirosakura.ui.compat.OutlinedCard
import moe.forpleuvoir.hiirosakura.ui.configwrapper.rememberKeyedStateList
import moe.forpleuvoir.hiirosakura.ui.compat.RemoveButton

@Composable
fun RepairableComponentWrapper(
    key: Identifier,
    value: Repairable,
    onValueChange: (Repairable) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        val count = value.items.count()
        var showDialog by remember { mutableStateOf(false) }

        val tip = if (count > 0) {
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
        } else Modifier
        FlatButton(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .then(tip)
                .width(DataComponentEditorDefaults.entrySize.width),
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
IconButton(onClick = {
    showDialog = true
}) {
    Icon(Icons.Edit)
}
        }
if (showDialog) {
            HolderSetItemEditorDialog(
                value = value.items,
                onValueChange = { onValueChange(Repairable(it)) },
                onDismissRequest = { showDialog = false },
                title = { Text(key) }
            )
        }
    }
}

private val itemTags
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.map { it.key() }

private val TagKey<Item>.items
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).getTagOrEmpty(this)

private val TagKey<Item>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.ITEM).tags.filter {
        it.key().location == this.location
    }.findFirst().get()


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

    val items = rememberKeyedStateList(value.map { it.value() }.toList())
    var nextKey by remember { mutableLongStateOf(items.size.toLong()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val list = HolderSet.direct(items.values().map { BuiltInRegistries.ITEM.wrapAsHolder(it) })
            onValueChange(
                if (mode) list
                else tag?.asHolderSet ?: list
            )
            true
        },
        content = {
            Column {
                firstTag?.let {
                    Row {
                        val buttonTexts = listOf(
                            HSLang.ItemEditor.fromRegistry,
                            HSLang.ItemEditor.fromTag,
                        )
                        val width = rememberSegmentedButtonWidth(
                            items = buttonTexts,
                            textStyle = SokitsuTheme.typography.button,
                        ) { it }
                        SegmentedButton(
                            selected = mode,
                            onClick = { mode = true },
                            
                            modifier = Modifier.width(width),
                            label = { Text(component = HSLang.ItemEditor.fromRegistry) }
                        )
                        SegmentedButton(
                            selected = !mode,
                            onClick = { mode = false },
                            
                            modifier = Modifier.width(width),
                            label = { Text(component = HSLang.ItemEditor.fromTag) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        if (targetState) {
                            // false -> true：新内容从左侧进入
                            slideInHorizontally { -it } togetherWith
                                    slideOutHorizontally { it }
                        } else {
                            // true -> false：新内容从右侧进入
                            slideInHorizontally { it } togetherWith
                                    slideOutHorizontally { -it }
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
                                                        val item = selected.asItem()
                                                        if (!items.any { keyed -> keyed.value == item }) {
                                                            items.add(Keyed(nextKey++, item))
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
                                    ItemTagSelector(
                                        it,
                                        { tag ->
                                            tag.items.forEach { item ->
                                                if (!items.any { it.value == item.value() }) {
                                                    items.add(Keyed(nextKey++, item.value()))
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
                            OutlinedLabelBox(
                                { Text(component = HSLang.ItemEditor.items) },
                            ) {
                                Box(Modifier.fillMaxWidth().height(460.dp)) {
                                    val lazyGridState = rememberLazyGridState()
                                    LazyVerticalGrid(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        columns = GridCells.Fixed(9),
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        contentPadding = PaddingValues(if (lazyGridState.canScroll) 12.dp else 0.dp),
                                        state = lazyGridState
                                    ) {
                                        itemsIndexed(items, key = { _, keyed -> keyed.key }) { index, (_, item) ->
                                            val interactionSource = remember { MutableInteractionSource() }

                                            val isHovered by interactionSource.collectIsHoveredAsState()
                                            OutlinedCard(
                                                Modifier
                                                    .size(48.dp)
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
                                                                items.removeAt(index)
                                                            }
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

                                    VerticalScroller(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        adapter = rememberScrollerAdapter(lazyGridState, 9)
                                    )
                                }
                            }
                        }

                    } else {
                        tag?.let { ItemTagSelector(it, { tag = it }) }
                            ?: Text(component = IGLang.Misc.hasNothing)
                    }
                }
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
    label: @Composable (() -> Unit)? = null,
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
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) {
        OutlinedLabelBox(label = label, modifier = modifier, contentPadding = contentPadding) {
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
