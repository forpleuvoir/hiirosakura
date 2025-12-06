@file:Suppress("FunctionName")

package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcher
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryCount
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryDataComponentType
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryEnchantment
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryItem
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryMatcher
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryName
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryRarity
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryScript
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryTag
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.CompositeMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.TableLayoutColumnScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreen
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.ItemIcon
import moe.forpleuvoir.ibukigourd.gui.widget.Rect
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.*
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import kotlin.jvm.optionals.getOrNull

private val map: Map<Text, (Modifier, Modifier, (ItemStackMatchEntry) -> Unit) -> IGScreenImpl> = mapOf(
    itemStackMatcherEntryMatcher to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Matcher) -> Unit ->
        MatcherMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    }, itemStackMatcherEntryItem to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Item) -> Unit ->
        ItemMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryName to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Name) -> Unit ->
        NameMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryScript to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Script) -> Unit ->
        ScriptMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryCount to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Count) -> Unit ->
        CountMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryRarity to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Rarity) -> Unit ->
        RarityMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryEnchantment to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Enchantment) -> Unit ->
        EnchantmentMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryTag to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.Tag) -> Unit ->
        TagMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    },
    itemStackMatcherEntryDataComponentType to { modifier: Modifier, screenModifier: Modifier, entryConsumer: (ItemStackMatchEntry.DataComponentType) -> Unit ->
        ComponentTypeMatchEntryBuilder(modifier, screenModifier, entryConsumer = entryConsumer)
    }
)

fun ItemStackMatcherBuilder(
    matcher: ItemStackMatcher,
    matcherConsumer: (ItemStackMatcher) -> Unit,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    title: Text = itemStackMatcher
): IGScreenImpl {
    val matcher = matcher.clone()
    return Dialog(
        screenModifier = Modifier
            .onClose {
                matcherConsumer(matcher)
            }.then(screenModifier),
        modifier = Modifier.width(420f).then(modifier),
    ) {
        var onChanged = {}
        val selectedMode: MutableState<CompositeMatcher.MatchMode?> = mutableStateOf(matcher.mode)
        Text(title)
        Column(
            verticalArrangement = Arrangement.spacedBy(2f),
        ) {
            Row(
                Modifier.fill(),
                horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
            ) {
                RadioButtons(
                    CompositeMatcher.MatchMode.entries,
                    selectedMode,
                    { matcher.mode = it },
                    {
                        Text(it.translateText)
                    },
                    {
                        Modifier.hoverText(it.translateComment)
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4f)
            ) {
                ColumnListWrapped(
                    modifier = Modifier.matchSibling().weight(1),
                    listModifier = { Modifier.weight(1).fill() },
                ) {
                    if (matcher.entries.isEmpty()) {
                        Text(IGLang.hasNothing)
                    }
                    matcher.entries.forEachIndexed { index, entry ->
                        EntryWrapper(
                            entry,
                            {
                                matcher.setEntry(index, it)
                                onChanged()
                            },
                            {
                                matcher.removeEntry(index)
                                onChanged()
                            },
                            Modifier.fill()
                        )
                    }
                }.apply {
                    onChanged = { this.executeRecompose() }
                }

                Column(
//                    modifier = Modifier.height(180f),
                    verticalArrangement = Arrangement.spacedBy(2f),
                ) {
                    map.forEach { (key, builder) ->
                        Button(Modifier, Arrangement.spacedBy(6f, Alignment.CenterHorizontally)) {
                            Text(key, modifier = Modifier.width(map.keys.maxWidth))
                            Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(8f, 8f))
                            click {
                                builder.invoke(Modifier, Modifier) {
                                    matcher.addEntry(it)
                                    onChanged()
                                }.open()
                            }
                        }
                    }
                }

            }

        }
    }
}

private fun ContainerScope.EntryWrapper(
    entry: ItemStackMatchEntry,
    entryConsumer: (ItemStackMatchEntry) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    Modifier
        .padding(horizontal = 2f)
        .bgHoverHighlightBox()
        .then(modifier),
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    Text(entry.translateText, modifier = Modifier.weight(1))
    when (entry) {
        is ItemStackMatchEntry.Matcher -> ItemStackMatcherSimpleInfo(entry.matcher, Modifier.hoverTip { ItemStackMatcherInfo(entry.matcher) })
        is ItemStackMatchEntry.Item    -> Row(
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(entry.item, .6f)
            Text(entry.asText)
        }

        is ItemStackMatchEntry.Script  -> Text(entry.asText, Modifier.hoverText(entry.script))
        else                           -> Text(entry.asText)
    }
    FlatButton(
        idleColor = Colors.BLACK.alpha(0f), round = 0
    ) {
        Text(
            entry.mode.translateText,
            setting = TextSetting().copy(backgroundColor = if (entry.mode.toBoolean()) Colors.LIME.alpha(.25f) else Colors.RED.alpha(0.25f))
        )
        click {
            val mode = MatchEntry.MatchMode.fromBoolean(!entry.mode.toBoolean())
            val newValue = when (entry) {
                is ItemStackMatchEntry.Matcher           -> ItemStackMatchEntry.Matcher(entry.matcher, mode)
                is ItemStackMatchEntry.Item              -> ItemStackMatchEntry.Item(entry.item, mode)
                is ItemStackMatchEntry.Name              -> ItemStackMatchEntry.Name(entry.name, mode)
                is ItemStackMatchEntry.Script            -> ItemStackMatchEntry.Script(entry.script, mode)
                is ItemStackMatchEntry.Count             -> ItemStackMatchEntry.Count(entry.count, mode)
                is ItemStackMatchEntry.Rarity            -> ItemStackMatchEntry.Rarity(entry.rarity, mode)
                is ItemStackMatchEntry.Enchantment       -> ItemStackMatchEntry.Enchantment(entry.enchantment, entry.level, mode)
                is ItemStackMatchEntry.Tag               -> ItemStackMatchEntry.Tag(entry.tag, mode)
                is ItemStackMatchEntry.DataComponentType -> ItemStackMatchEntry.DataComponentType(entry.componentType, mode)
            }
            entryConsumer(newValue)
        }
    }
    EditButton {
        when (entry) {
            is ItemStackMatchEntry.Matcher           -> MatcherMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Item              -> ItemMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Name              -> NameMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Script            -> ScriptMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Count             -> CountMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Rarity            -> RarityMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Enchantment       -> EnchantmentMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.Tag               -> TagMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
            is ItemStackMatchEntry.DataComponentType -> ComponentTypeMatchEntryBuilder(entry = entry, entryConsumer = entryConsumer)
        }.open()
    }
    RemoveButton { removeAction() }
}

internal fun <T : MatchEntry<*>> MatchEntryDialog(
    title: Text,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    parentScreen: IGScreen? = mc.screen as IGScreen?,
    entrySupplier: (MatchEntry.MatchMode) -> T,
    entryConsumer: (T) -> Unit,
    content: ColumnScope.() -> Unit
): IGScreenImpl {
    return Dialog(
        modifier,
        screenModifier,
        parentScreen = parentScreen
    ) {
        Text(title, modifier = Modifier.margin(bottom = 5f))
        content()
        Row(
            Modifier.matchSibling().margin(top = 5f),
            horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Right)
        ) {
            Button(
                Modifier.hoverText(MatchEntry.MatchMode.Include.translateComment)
            ) {
                Text(MatchEntry.MatchMode.Include.translateText)
                click {
                    entryConsumer(entrySupplier(MatchEntry.MatchMode.Include))
                    closeScreen()
                }
            }
            Button(
                Modifier.hoverText(MatchEntry.MatchMode.Exclude.translateComment)
            ) {
                Text(MatchEntry.MatchMode.Exclude.translateText)
                click {
                    entryConsumer(entrySupplier(MatchEntry.MatchMode.Exclude))
                    closeScreen()
                }
            }
        }
    }
}

private fun MatcherMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Matcher = ItemStackMatchEntry.Matcher(ItemStackMatcher.handheldItemMatcher, MatchEntry.MatchMode.Include),
    entryConsumer: (ItemStackMatchEntry.Matcher) -> Unit
): IGScreenImpl = ItemStackMatcherBuilder(
    entry.matcher,
    {
        entryConsumer(ItemStackMatchEntry.Matcher(it, entry.mode))
    },
    modifier,
    screenModifier,
    itemStackMatcherEntryMatcher
)


private fun ItemMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Item = ItemStackMatchEntry.Item(ItemStackMatcher.handheldItemStack?.item ?: Items.MELON, MatchEntry.MatchMode.Include),
    entryConsumer: (ItemStackMatchEntry.Item) -> Unit
): IGScreenImpl {
    val item = mutableStateOf(entry.item)
    return MatchEntryDialog(
        title = itemStackMatcherEntryItem,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Item(item.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        ItemSelector(
            item,
            modifier = Modifier.width(120f),
        )
    }
}

private fun NameMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Name = ItemStackMatchEntry.Name(
        ItemStackMatcher.handheldItemStack?.hoverName?.string ?: Items.MELON.name.string,
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (ItemStackMatchEntry.Name) -> Unit
): IGScreenImpl {
    var name = entry.name
    return MatchEntryDialog(
        title = itemStackMatcherEntryName,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Name(name, mode) },
        entryConsumer = entryConsumer
    ) {
        TextEditor(Modifier.width(200f)) {
            text = name
            textConsumer { name = it }
        }
    }
}

private fun ScriptMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Script = ItemStackMatchEntry.Script(mode = MatchEntry.MatchMode.Include),
    entryConsumer: (ItemStackMatchEntry.Script) -> Unit
): IGScreenImpl {
    var script = entry.script
    return MatchEntryDialog(
        title = itemStackMatcherEntryScript,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Script(script, mode) },
        entryConsumer = entryConsumer
    ) {
        TextAreaWrapped(
            modifier = Modifier.size(440f, 200f)
        ) {
            text = script
            textConsumer { script = it }
        }
    }
}

private fun CountMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Count = ItemStackMatchEntry.Count(
        (ItemStackMatcher.handheldItemStack?.count ?: 1)..(ItemStackMatcher.handheldItemStack?.count ?: 64),
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (ItemStackMatchEntry.Count) -> Unit
): IGScreenImpl {
    val start = mutableStateOf(entry.count.first)
    val end = mutableStateOf(entry.count.endInclusive)
    return MatchEntryDialog(
        title = itemStackMatcherEntryCount,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Count(start.getValue()..end.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            IntEditor(start, 1..Int.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
            Text("<= .. <=")
            IntEditor(end, 1..Int.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
        }
    }
}

private fun RarityMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Rarity = ItemStackMatchEntry.Rarity(ItemStackMatcher.handheldItemStack?.rarity ?: Rarity.COMMON, MatchEntry.MatchMode.Include),
    entryConsumer: (ItemStackMatchEntry.Rarity) -> Unit
): IGScreenImpl {
    val rarity = mutableStateOf(entry.rarity)
    return MatchEntryDialog(
        title = itemStackMatcherEntryRarity,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Rarity(rarity.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        Selector(
            options = Rarity.entries,
            selected = rarity,
            selectedWrapper = {
                Text(
                    Literal(it.name),
                    modifier = Modifier.weight(1)
                )
            },
            optionWrapper = {
                Text(
                    Literal(it.name),
                    modifier = Modifier
                        .width(
                            Rarity.entries
                                .map { rarity -> rarity.name }
                                .maxWidth.coerceAtLeast(30f)
                        )
                )
            },
            modifier = Modifier.width(120f),
        )
    }
}

private fun EnchantmentMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Enchantment = ItemStackMatchEntry.Enchantment(
        ItemStackMatcher.handheldItemStack?.enchantments?.entrySet()?.firstOrNull()?.key?.registeredName
            ?: "minecraft:aqua_affinity",
        1..255,
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (ItemStackMatchEntry.Enchantment) -> Unit
): IGScreenImpl {
    val enchantment = mutableStateOf(entry.enchantment)

    val level = mutableStateOf(entry.level.first)
    val levelEnd = mutableStateOf(entry.level.endInclusive)

    val enchantments = ItemStackMatcher.handheldItemStack
        ?.enchantments
        ?.entrySet()
        ?.run {
            buildMap {
                this@run.forEach { (enchantment, level) ->
                    this[enchantment] = level
                }
            }
        }

    return MatchEntryDialog(
        title = itemStackMatcherEntryEnchantment,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Enchantment(enchantment.getValue(), level.getValue()..levelEnd.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {

                if (!enchantments.isNullOrEmpty()) {
                    Text(HSLang.getFromHandItem, modifier = Modifier.height(20f))
                }

                if (REGISTERED_ENCHANTMENT_ID.isNotEmpty()) {
                    Text(HSLang.getFromRegistry, modifier = Modifier.height(20f))
                }

                Text(HSLang.itemStackMatcherEntryEnchantmentID, modifier = Modifier.height(20f))
                Text(HSLang.itemStackMatcherEntryEnchantmentLevelRange, modifier = Modifier.height(20f))
            }

            Column(horizontalAlignment = Alignment.Right, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!enchantments.isNullOrEmpty()) {
                    EnchatmentSelector(
                        mutableStateOf(enchantments.entries.first().key),
                        enchantments = enchantments.keys.toList(),
                        onSelected = {
                            REGISTERED_ENCHANTMENT
                                .find { e -> e.registeredName == it.registeredName }
                                ?.let { e ->
                                    enchantment.setValue(e.registeredName)
                                }
                            enchantments[it]?.let { lv ->
                                level.setValue(lv)
                                levelEnd.setValue(lv)
                            }
                        },
                        modifier = Modifier.matchSibling()
                    )
                }

                if (REGISTERED_ENCHANTMENT_ID.isNotEmpty()) {
                    val selectedEnchantment = enchantment.getValue().asMutableState
                    selectedEnchantment.subscribe {
                        enchantment.setValue(it)
                    }
                    EnchatmentSelector(selectedEnchantment, modifier = Modifier.matchSibling())
                }

                TextEditor(Modifier.matchSibling().hoverTip {
                    Text(enchantmentDescription(enchantment.getValue()))
                }) { bindState(enchantment) }

                Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                    IntEditor(level, 1..65535, modifier = Modifier.width(65f), editorModifier = { Modifier.weight(1) })
                    Text("<= .. <=")
                    IntEditor(levelEnd, 1..65535, modifier = Modifier.width(65f), editorModifier = { Modifier.weight(1) })
                }

            }
        }


    }
}

private fun TagMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.Tag = ItemStackMatchEntry.Tag(
        ItemStackMatcher.handheldItemStack?.tags?.findFirst()?.getOrNull()?.location?.toString() ?: "",
        MatchEntry.MatchMode.Include
    ),
    entryConsumer: (ItemStackMatchEntry.Tag) -> Unit
): IGScreenImpl {
    var tag = entry.tag
    val tags = ItemStackMatcher.handheldItemStack
        ?.tags
        ?.toList()
        ?.map { tag -> tag.location.toString() }
    return MatchEntryDialog(
        title = itemStackMatcherEntryTag,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Tag(tag, mode) },
        entryConsumer = entryConsumer
    ) {
        var editor by lateInitValueOf<TextEditorWidget>()
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!tags.isNullOrEmpty()) {
                    Text(HSLang.getFromHandItem, modifier = Modifier.height(20f))
                }
                Text(HSLang.tag, modifier = Modifier.height(20f))
            }
            Column(horizontalAlignment = Alignment.Right, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!tags.isNullOrEmpty()) {
                    Selector(
                        tags,
                        modifier = Modifier.width(200f),
                        onSelected = {
                            editor.text = it
                            tag = it
                        },
                        selectedWrapper = {
                            Text(it, modifier = Modifier.weight(1))
                        },
                        optionWrapper = {
                            Text(it, modifier = Modifier.width(tags.maxWidth + 1))
                        }
                    )
                }
                TextEditor(modifier = Modifier.width(200f)) {
                    text = tag
                    textConsumer { tag = it }
                    editor = owner()
                }
            }
        }
    }
}

private fun ComponentTypeMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    entry: ItemStackMatchEntry.DataComponentType = ItemStackMatchEntry.DataComponentType(
        ItemStackMatcher.handheldItemStack?.components?.keySet()?.firstOrNull() ?: DataComponents.FOOD, MatchEntry.MatchMode.Include
    ),
    entryConsumer: (ItemStackMatchEntry.DataComponentType) -> Unit
): IGScreenImpl {
    val componentType: MutableState<DataComponentType<*>> = mutableStateOf(entry.componentType)
    val components = ItemStackMatcher.handheldItemStack
        ?.components?.keySet()
    return MatchEntryDialog(
        title = itemStackMatcherEntryDataComponentType,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.DataComponentType(componentType.getValue(), mode) },
        entryConsumer = entryConsumer
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!components.isNullOrEmpty()) {
                    Text(HSLang.getFromHandItem, modifier = Modifier.height(20f))
                }
                Text(HSLang.itemComponent, modifier = Modifier.height(20f))
            }
            Column(horizontalAlignment = Alignment.Right, verticalArrangement = Arrangement.spacedBy(5f)) {
                if (!components.isNullOrEmpty()) {
                    DataComponentTypeSelector(
                        componentType,
                        components.toList(),
                        modifier = Modifier.width(200f),
                        searchBarModifier = { Modifier.width(200f) },
                        listModifier = { Modifier.width(200f) },
                    )
                }
                DataComponentTypeSelector(
                    componentType,
                    modifier = Modifier.width(200f),
                    searchBarModifier = { Modifier.width(200f) },
                    listModifier = { Modifier.width(200f) },
                )
            }
        }
    }
}

//------------ ItemStackMatcherTableColumn ------------\\

fun <T> TableScope<T>.ItemStackMatcherTableColumn(
    consumer: (Int, T, ItemStackMatcher) -> Unit,
    matcherExtractor: (T) -> ItemStackMatcher,
    buttonModifier: TableLayoutColumnScope.() -> Modifier = { Modifier },
    weight: Int = 0,
    header: TableLayoutColumnScope.() -> GuiWidget,
) = Header(weight, header).Column { index, entry ->
    val matcher = matcherExtractor(entry)
    Button(
        modifier = Modifier.hoverTip {
            ItemStackMatcherInfo(matcher)
        }.then(buttonModifier()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ItemStackMatcherSimpleInfo(matcher)
        click {
            ItemStackMatcherBuilder(matcher, {
                consumer(index, entry, it)
            }).open()
        }
    }
}

//------------ ItemStackMatcherInfo ------------\\

fun ContainerScope.ItemStackMatcherSimpleInfo(
    itemStackMatcher: CompositeMatcher<ItemStack>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = Row(
    modifier,
    horizontalArrangement,
    verticalAlignment
) {
    val entries = itemStackMatcher.entries
    when (entries.size) {
        0    -> Text(IGLang.hasNothing)
        1    -> ItemStackEntryInfo(entries.first())
        else -> {
            if (ItemStackMatcher.isAnyMatcher(itemStackMatcher)) {
                Text(CompositeMatcher.MatchMode.AnyMatch.translateText)
            } else Text(itemStackMatcher.mode.translateText.appendLiteral(":").append(IGLang.listConfigWrapperText(entries.size)))
        }
    }
}

fun ContainerScope.ItemStackMatcherInfo(
    itemStackMatcher: CompositeMatcher<ItemStack>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(1f),
    horizontalAlignment: Alignment.Horizontal = Alignment.Left,
) = Column(
    modifier,
    verticalArrangement,
    horizontalAlignment
) {
    //mode
    Text(
        itemStackMatcher.mode.translateText,
        Modifier.matchSibling(),
        setting = TextWidget.Setting().copy(horizontalAlignment = Alignment.CenterHorizontally)
    )
    Rect(Colors.DARK_BLUE_GREY.alpha(0.5f), Modifier.height(1f).matchSibling())
    //entries
    itemStackMatcher.entries.forEachWithLimit(10) {
        ItemStackEntryInfo(it)
    }
}

//------------ ItemStackMatchEntryInfo ------------\\

@Suppress("UNCHECKED_CAST")
fun ContainerScope.ItemStackEntryInfo(entry: MatchEntry<ItemStack>) {
    when (entry) {
        is ItemStackMatchEntry.Matcher           -> ItemStackEntryMatcherInfo(entry)
        is ItemStackMatchEntry.Item              -> ItemStackEntryItemInfo(entry)
        is ItemStackMatchEntry.Name              -> ItemStackEntryNameInfo(entry)
        is ItemStackMatchEntry.Script            -> ItemStackEntryScriptInfo(entry)
        is ItemStackMatchEntry.Count             -> ItemStackEntryCountInfo(entry)
        is ItemStackMatchEntry.Enchantment       -> ItemStackEntryEnchantmentInfo(entry)
        is ItemStackMatchEntry.Tag               -> ItemStackEntryTagInfo(entry)
        is ItemStackMatchEntry.Rarity            -> ItemStackEntryRarityInfo(entry)
        is ItemStackMatchEntry.DataComponentType -> ItemStackEntryDataComponentTypeInfo(entry)
    }
}

fun RowScope.ModeRect(entry: MatchEntry<*>) {
    Rect(if (entry.mode.toBoolean()) Colors.GREEN.alpha(.5F) else Colors.RED.alpha(0.5f), Modifier.width(1f).matchSibling())
}

fun ContainerScope.ItemStackEntryMatcherInfo(
    entry: ItemStackMatchEntry.Matcher,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(itemStackMatcherEntryMatcher)
    ItemStackMatcherSimpleInfo(entry.matcher)
}

fun ContainerScope.ItemStackEntryItemInfo(
    entry: ItemStackMatchEntry.Item,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(itemStackMatcherEntryItem)
    ItemIcon(entry.item, .6f)
    Text(entry.asText)
}

fun ContainerScope.ItemStackEntryNameInfo(
    entry: ItemStackMatchEntry.Name,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(
        mutableStateBy { itemStackMatcherEntryScript.append(entry.asText) },
        modifier = Modifier.maxWidth(180f)
    )
}

fun ContainerScope.ItemStackEntryScriptInfo(
    entry: ItemStackMatchEntry.Script,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(
        mutableStateBy { itemStackMatcherEntryScript.append(entry.asText) },
        modifier = Modifier.maxWidth(180f)
    )
}

fun ContainerScope.ItemStackEntryCountInfo(
    entry: ItemStackMatchEntry.Count,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(mutableStateBy { itemStackMatcherEntryCount.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.ItemStackEntryRarityInfo(
    entry: ItemStackMatchEntry.Rarity,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(mutableStateBy { itemStackMatcherEntryRarity.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.ItemStackEntryEnchantmentInfo(
    entry: ItemStackMatchEntry.Enchantment,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(mutableStateBy { itemStackMatcherEntryEnchantment.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.ItemStackEntryTagInfo(
    entry: ItemStackMatchEntry.Tag,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(mutableStateBy { itemStackMatcherEntryTag.append(entry.asText) }, modifier = Modifier.maxWidth(180f))
}

fun ContainerScope.ItemStackEntryDataComponentTypeInfo(
    entry: ItemStackMatchEntry.DataComponentType,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ModeRect(entry)
    Text(
        mutableStateBy { itemStackMatcherEntryDataComponentType.append(entry.asText) },
        modifier = Modifier.maxWidth(180f)
    )
}