package moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.util.rememberClipboardWriter
import moe.forpleuvoir.hiirosakura.ui.widget.*
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.CompositeMatcherModeSelector
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.LocalMatcherDialogContentSize
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.MatchEntryRow
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.TestButton
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.*
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

//region Displayer

@Composable
fun ItemStackMatcherDisplayerEditor(
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
    modifier: Modifier = Modifier,
    displayerModifier: @Composable RowScope.() -> Modifier = { Modifier.weight(1f) },
) {
    var showDialog by remember { mutableStateOf(false) }
    ItemStackMatcherDisplayer(
        value = value,
        modifier = modifier,
        displayerModifier = displayerModifier,
        trailingIcon = {
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Edit)
            }
        }
    )
    if (showDialog) {
        ItemStackMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun ItemStackMatcherDisplayerInnerEditor(
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    InlineEditField(
        onEdit = { showDialog = true },
        modifier = modifier,
        tooltip = { ItemStackMatcherInfo(value) },
    ) {
        ItemStackMatcherSimpleInfo(value)
    }
    if (showDialog) {
        ItemStackMatcherEditorDialog(
            { showDialog = false },
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
fun ItemStackMatcherDisplayer(
    value: ItemStackMatcher,
    modifier: Modifier = Modifier,
    displayerModifier: @Composable RowScope.() -> Modifier = { Modifier.weight(1f) },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapper.spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = displayerModifier()
                .height(configControlHeight())
                .tooltip { ItemStackMatcherInfo(value) },
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.padding(TextFieldDefaults.contentPadding())) {
                ItemStackMatcherSimpleInfo(value)
            }
        }
        trailingIcon?.let {
            CompositionLocalProvider(LocalIconScale provides configIconScale()) {
                it()
            }
        }
    }
}

@Composable
fun ItemStackMatcherInfo(
    value: ItemStackMatcher,
    modifier: Modifier = Modifier.width(IntrinsicSize.Min),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) = Column(modifier, verticalArrangement, horizontalAlignment) {
    Text(
        value.mode.translateText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    HorizontalDivider()
    value.entries.take(10).forEach { entry ->
        ItemStackMatchEntryInfo(entry)
    }
}

@Composable
fun ItemStackMatcherSimpleInfo(
    value: ItemStackMatcher,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    val entries = value.entries
    when (entries.size) {
        0    -> Text(component = IGLang.Misc.hasNothing, maxLines = 1, overflow = TextOverflow.Ellipsis)
        1    -> ItemStackMatchEntryInfo(entries.first())
        else -> {
            if (ItemStackMatcher.isAnyMatcher(value)) {
                Text(CompositeMatcher.MatchMode.AnyMatch.translateText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else Text(
                value.mode.translateText.appendLiteral(":").append(HSLang.Matcher.entries(entries.size)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun ItemStackMatchEntryInfo(entry: MatchEntry<ItemStack>) {
    when (entry) {
        is ItemStackMatchEntry.Matcher           -> ItemStackMatchEntryMatcherInfo(entry)
        is ItemStackMatchEntry.Item              -> ItemStackMatchEntryItemInfo(entry)
        is ItemStackMatchEntry.Name              -> ItemStackMatchEntryNameInfo(entry)
        is ItemStackMatchEntry.Script            -> ItemStackMatchEntryScriptInfo(entry)
        is ItemStackMatchEntry.Count             -> ItemStackMatchEntryCountInfo(entry)
        is ItemStackMatchEntry.Enchantment       -> ItemStackMatchEntryEnchantmentInfo(entry)
        is ItemStackMatchEntry.Tag               -> ItemStackMatchEntryTagInfo(entry)
        is ItemStackMatchEntry.Rarity            -> ItemStackMatchEntryRarityInfo(entry)
        is ItemStackMatchEntry.DataComponentType -> ItemStackMatchEntryDataComponentTypeInfo(entry)
    }
}
//endregion

@Composable
fun BasicItemStackMatcherEditor(
    mode: CompositeMatcher.MatchMode,
    onModeChange: (CompositeMatcher.MatchMode) -> Unit,
    entries: KeyedListState<ItemStackMatchEntry>,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositeMatcherModeSelector(mode, onModeChange)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ItemStackMatcher.handheldItemStack?.let { itemStack ->
                    TestButton(
                        HSLang.ItemStackMatcher.testButton.plainText,
                        HSLang.ItemStackMatcher.testSuccess.plainText,
                        HSLang.ItemStackMatcher.testFailed.plainText
                    ) {
                        ItemStackMatcher(mode, entries.entries.values()).match(itemStack)
                    }
                }

                val copyToClipboard = rememberClipboardWriter()
                FormatExportButton(IGLang.Misc.copySuccess(HSLang.ItemStackMatcher.title).plainText) {
                    copyToClipboard(it.encode(ItemStackMatcher.serialization(ItemStackMatcher(mode, entries.entries.values()))))
                }
                FormatImportButton(HSLang.ItemStackMatcher.title.plainText, HSLang.Common.success.plainText) {
                    ItemStackMatcher.deserialization(it)
                        .getOrThrow()
                        .let { result ->
                            onModeChange(result.mode)
                            entries.clear()
                            result.entries.forEach { entries.add(it) }
                        }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        // 滚动显隐的嵌套滚动回调必须挂在滚动容器的祖先上:FAB 是滚动容器的兄弟节点,
        // 挂在 FAB 自身的 Modifier 上永远收不到位移(历史实现即因如此从未生效)。
        val lazyListState = rememberLazyListState()
        val fabVisibility = rememberFabScrollVisibility(lazyListState)
        Box(
            Modifier
                .fillMaxSize()
                .fabScrollVisibility(fabVisibility)
        ) {
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                entries.move(from.index, to.index)
            }
            // 列表与滚动条各占一列
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyListState
                ) {
                    itemsIndexed(entries.entries, key = { _, keyed -> keyed.key }) { index, (key, entry) ->
                        ReorderableItem(
                            reorderableLazyListState, key = key,
                            animateItemModifier = hsItemAnimation(),
                        ) { _ ->
                            ItemStackMatchEntryRow(
                                modifier = Modifier.fillMaxWidth(),
                                entry = entry,
                                onChange = { newEntry ->
                                    entries.setValue(index, newEntry)
                                },
                                onRemove = {
                                    entries.removeAt(index)
                                },
                                moveHandler = {
                                    DragHandle(modifier = Modifier.draggableHandle())
                                }
                            )
                        }

                    }
                }
                if (lazyListState.canScroll) {
                    Spacer(Modifier.width(8.dp))
                }
                VerticalFlatScroller(
                    adapter = rememberScrollerAdapter(lazyListState)
                )
            }

            FloatingAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                fabVisibilityState = fabVisibility,
                addMenuOptions = if (isNested) nestedAddMenuOptions else addMenuOptions
            ) { newEntry ->
                // key 必须单调递增且不复用:`entries.size` 在「删过元素再加」(以及导入后
                // 与 0..n-1 重合)时会撞 key —— Lazy 直接抛 "Key was already used" 崩屏,
                // 且 key 已存在时 animateItem 不会触发新条目动画。
                entries.add(newEntry)
            }
        }
    }
}

@Composable
fun ItemStackMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: ItemStackMatcher,
    onValueChange: (ItemStackMatcher) -> Unit,
) {
    var editingMode by remember(value) { mutableStateOf(value.mode) }

    val editingEntries = rememberKeyedList(value.entries)
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = HSLang.ItemStackMatcher.title) },
        content = {
            BasicItemStackMatcherEditor(
                editingMode,
                { editingMode = it },
                editingEntries,
                modifier = Modifier.size(LocalMatcherDialogContentSize.current)
            )
        },
        onConfirmRequest = {
            onValueChange(ItemStackMatcher(editingMode, editingEntries.entries.values()))
            true
        }
    )
}

//region EntryRow

@Composable
private fun ItemStackMatchEntryRow(
    entry: ItemStackMatchEntry,
    onChange: (ItemStackMatchEntry) -> Unit,
    onRemove: () -> Unit,
    moveHandler: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) = MatchEntryRow(
    title = entry.translateText,
    entry = entry,
    entryCopyWithMode = { e, m -> e.copyWithMode(m) },
    moveHandler = moveHandler,
    onChange = onChange,
    onRemove = onRemove,
    modifier = modifier
) {
    when (entry) {
        is ItemStackMatchEntry.Matcher           -> ItemStackMatchEntryMatcherRow(entry, onChange)
        is ItemStackMatchEntry.Item              -> ItemStackMatchEntryItemRow(entry, onChange)
        is ItemStackMatchEntry.Name              -> ItemStackMatchEntryNameRow(entry, onChange)
        is ItemStackMatchEntry.Script            -> ItemStackMatchEntryScriptRow(entry, onChange)
        is ItemStackMatchEntry.Count             -> ItemStackMatchEntryCountRow(entry, onChange)
        is ItemStackMatchEntry.Rarity            -> ItemStackMatchEntryRarityRow(entry, onChange)
        is ItemStackMatchEntry.Enchantment       -> ItemStackMatchEntryEnchantmentRow(entry, onChange)
        is ItemStackMatchEntry.Tag               -> ItemStackMatchEntryTagRow(entry, onChange)
        is ItemStackMatchEntry.DataComponentType -> ItemStackMatchEntryDataComponentTypeRow(entry, onChange)
    }
}

//endregion

//region Adder

private val nestedAddMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> = listOf(
    AddMenuOption({ Text(ItemStackMatchEntry.Item.title) }, { ItemStackMatchEntry.Item.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryItemEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Item, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Name.title) }, { ItemStackMatchEntry.Name.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryNameEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Name, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Script.title) }, { ItemStackMatchEntry.Script.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryScriptEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Script, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Count.title) }, { ItemStackMatchEntry.Count.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryCountEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Count, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Rarity.title) }, { ItemStackMatchEntry.Rarity.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryRarityEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Rarity, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Enchantment.title) }, { ItemStackMatchEntry.Enchantment.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryEnchantmentEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Enchantment, addAction)
    },
    AddMenuOption({ Text(ItemStackMatchEntry.Tag.title) }, { ItemStackMatchEntry.Tag.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryTagEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Tag, addAction)
    },
    AddMenuOption(
        { Text(ItemStackMatchEntry.DataComponentType.title) },
        { ItemStackMatchEntry.DataComponentType.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryDataComponentTypeEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.DataComponentType, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<ItemStackMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(
        { Text(ItemStackMatchEntry.Matcher.title) },
        { ItemStackMatchEntry.Matcher.default }) { addAction, defaultValue, onDismiss ->
        ItemStackMatchEntryMatcherEditorDialog(onDismiss, defaultValue() as ItemStackMatchEntry.Matcher, addAction)
    }
//endregion
