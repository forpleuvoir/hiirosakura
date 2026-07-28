package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.util.rememberSegmentedButtonWidth
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowser
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.name
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.debug
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
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
        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .plainTooltip {
                    FlowRow(
                        modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        value.items.forEach { item ->
                            ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                        }
                    }
                }
                .width(DataComponentEditorDefaults.entrySize.width),
            label = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    value.items.take(6).forEach { item ->
                        ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                    }
                    if (count > 6)
                        Text("...", overflow = TextOverflow.Ellipsis, maxLines = 1)
                    if (count == 0)
                        Text(IGLang.Misc.hasNothing, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            RepairableEditor(
                key = key,
                value = value,
                onValueChange = onValueChange,
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
fun RepairableEditor(
    key: Identifier,
    value: Repairable,
    onValueChange: (Repairable) -> Unit,
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit
) {
    val set = value.items()
    var tag by remember {
        mutableStateOf(
            if (set is HolderSet.Named) set.key()
            else itemTags.findFirst().get()
        )
    }

    var mode by remember { mutableStateOf(set !is HolderSet.Named) }
    val items = rememberKeyedList(set.map { it.value() }.toList())
    var nextKey by remember { mutableLongStateOf(items.size.toLong()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        onConfirmRequest = {
            val result = if (mode) {
                Repairable(HolderSet.direct(items.values().map { BuiltInRegistries.ITEM.wrapAsHolder(it) }))
            } else {
                Repairable(tag.asHolderSet)
            }
            onValueChange(result)
            true
        },
        content = {
            Column {
                SingleChoiceSegmentedButtonRow {
                    val buttonTexts = listOf(
                        key.asTranslateText(suffix = "from_items", fallback = "From Items"),
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
                        label = { Text(key, suffix = "from_items", fallback = "From Items") }
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
                                OutlinedButton(
                                    { showItemSelector = true },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    border = BorderStroke(1.dp,MaterialTheme.colorScheme.outline),
                                    shape = MaterialTheme.shapes.extraSmall,
                                ) {
                                    Text(key, suffix = "add_item", fallback = "Add Item")
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
                                Spacer(Modifier.width(12.dp))
                                ItemTagSelector(
                                    itemTags.findFirst().get(),
                                    { tag ->
                                        tag.items.forEach { item ->
                                            if (!items.any { it.value == item.value() }) {
                                                items.add(Keyed(nextKey++, item.value()))
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
                                    val scrollState = rememberScrollState()
                                    FlowRow(
                                        Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(scrollState),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        items.forEachIndexed { index, (_, item) ->
                                            val interactionSource = remember { MutableInteractionSource() }
                                            val isHovered by interactionSource.collectIsHoveredAsState()
                                            OutlinedCard(
                                                Modifier
                                                    .size(48.dp)
                                                    .hoverable(interactionSource)
                                                    .plainTooltip(interactionSource) {
                                                        Column {
                                                            Text(item.asItem().name)
                                                            Spacer(Modifier.height(4.dp))
                                                            Text(item.asItem().key.toString(), color = MaterialTheme.colorScheme.primaryContainer)
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

                                    VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        adapter = rememberScrollbarAdapter(scrollState)
                                    )
                                }
                            }
                        }


                    } else {
                        ItemTagSelector(tag, { tag = it })
                    }
                }
            }
        }
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTagSelector(
    selected: TagKey<Item>,
    onSelect: (TagKey<Item>) -> Unit,
    items: List<TagKey<Item>> = itemTags.toList(),
    itemEquals: (TagKey<Item>, TagKey<Item>) -> Boolean = { a, b -> a == b },
    content: @Composable (TagKey<Item>) -> Unit = {
        Row(Modifier.plainTooltip {
            val types = it.items
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    types.forEach { item ->
                        ItemIcon(ItemStack(item), scaleOnHover = 1f, showTooltip = false)
                    }
                }
            }
        }) {
            Text("#${it.location}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (TagKey<Item>, Boolean) -> Unit = { item, _ ->
        Row(Modifier.plainTooltip {
            val types = item.items
            if (types.count() == 0) {
                Text(IGLang.Misc.hasNothing)
                return@plainTooltip
            } else {
                FlowRow(
                    modifier = Modifier.widthIn(max = 320.dp).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    types.forEach { item ->
                        ItemIcon(ItemStack(item), scaleOnHover = 1f)
                    }
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
