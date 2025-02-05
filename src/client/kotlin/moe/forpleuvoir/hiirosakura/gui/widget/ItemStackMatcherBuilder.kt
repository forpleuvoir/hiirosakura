package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcher
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryCount
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryDataComponentType
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryItem
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryRarity
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryScript
import moe.forpleuvoir.hiirosakura.HSLang.itemStackMatcherEntryTag
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.onClose
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.create
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreen
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextAreaWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.textRenderer
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import net.minecraft.util.Rarity

private val map: Map<Text, (Modifier, Modifier, (ItemStackMatchEntry) -> Unit) -> IGScreenImpl> = mapOf(
    itemStackMatcherEntryItem to { modifier: Modifier, screenModifier: Modifier, itemConsumer: (ItemStackMatchEntry.Item) -> Unit ->
        ItemMatchEntryBuilder(modifier, screenModifier, itemConsumer = itemConsumer)
    },
    itemStackMatcherEntryScript to { modifier: Modifier, screenModifier: Modifier, scriptConsumer: (ItemStackMatchEntry.Script) -> Unit ->
        ScriptMatchEntryBuilder(modifier, screenModifier, scriptConsumer = scriptConsumer)
    },
    itemStackMatcherEntryCount to { modifier: Modifier, screenModifier: Modifier, countConsumer: (ItemStackMatchEntry.Count) -> Unit ->
        CountMatchEntryBuilder(modifier, screenModifier, countConsumer = countConsumer)
    },
    itemStackMatcherEntryRarity to { modifier: Modifier, screenModifier: Modifier, rarityConsumer: (ItemStackMatchEntry.Rarity) -> Unit ->
        RarityMatchEntryBuilder(modifier, screenModifier, rarityConsumer = rarityConsumer)
    },
    itemStackMatcherEntryTag to { modifier: Modifier, screenModifier: Modifier, tagConsumer: (ItemStackMatchEntry.Tag) -> Unit ->
        TagMatchEntryBuilder(modifier, screenModifier, tagConsumer = tagConsumer)
    },
    itemStackMatcherEntryDataComponentType to { modifier: Modifier, screenModifier: Modifier, componentTypeConsumer: (ItemStackMatchEntry.DataComponentType) -> Unit ->
        ComponentTypeMatchEntryBuilder(modifier, screenModifier, componentTypeConsumer = componentTypeConsumer)
    }
)

fun ItemStackMatcherBuilder(
    matcher: ItemStackMatcher,
    matcherConsumer: (ItemStackMatcher) -> Unit,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier
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
        val selectedMode: MutableState<MultiMatcher.MatchMode?> = mutableStateOf(matcher.mode)
        TextLabel(itemStackMatcher)
        Row(
            verticalArrangement = Arrangement.spacedBy(2f),
        ) {
            Column(
                Modifier.fill(),
                horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Left)
            ) {
                RadioButtons(
                    MultiMatcher.MatchMode.entries,
                    selectedMode,
                    { matcher.mode = it },
                    {
                        TextLabel(it.translateText)
                    },
                    {
                        Modifier.hoverText(it.translateComment)
                    }
                )
            }
            Column(
                horizontalArrangement = Arrangement.spacedBy(4f)
            ) {
                RowListWrapped(
                    modifier = Modifier.height(180f).weight(1),
                    listModifier = { Modifier.weight(1).fill() },
                ) {
                    if (matcher.entries.isEmpty()) {
                        TextLabel(IGLang.hasNothing)
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
                    onChanged = { screen()?.execute { this.recompose() } }
                }

                Row(
                    modifier = Modifier.height(180f),
                    verticalArrangement = Arrangement.spacedBy(2f),
                ) {
                    map.forEach { (key, builder) ->
                        Button(Modifier, Arrangement.spacedBy(6f, Alignment.CenterHorizontally)) {
                            TextLabel(key, modifier = Modifier.width(map.keys.maxWidth(textRenderer).toFloat()))
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

private fun WidgetContainerScope.EntryWrapper(
    entry: ItemStackMatchEntry,
    entryConsumer: (ItemStackMatchEntry) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    Modifier
        .padding(horizontal = 2f)
        .bgHoverHighlightBox()
        .then(modifier),
    horizontalArrangement = Arrangement.spacedBy(4f)
) {
    TextLabel(entry.translateText, modifier = Modifier.weight(1))
    when (entry) {
        is ItemStackMatchEntry.Item              -> Column(
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(entry.item, .6f)
            TextLabel(entry.item.name.copyToText())
        }

        is ItemStackMatchEntry.Script            -> Unit
        is ItemStackMatchEntry.Count             -> TextLabel(entry.count.toString())
        is ItemStackMatchEntry.Rarity            -> TextLabel(entry.rarity.name)
        is ItemStackMatchEntry.Tag               -> TextLabel(entry.tag)
        is ItemStackMatchEntry.DataComponentType -> TextLabel(entry.componentType.id.toString())
    }
    TextLabel(entry.mode.translateText)
    EditButton {
        when (entry) {
            is ItemStackMatchEntry.Item              -> ItemMatchEntryBuilder(item = entry, itemConsumer = entryConsumer)
            is ItemStackMatchEntry.Script            -> ScriptMatchEntryBuilder(script = entry, scriptConsumer = entryConsumer)
            is ItemStackMatchEntry.Count             -> CountMatchEntryBuilder(count = entry, countConsumer = entryConsumer)
            is ItemStackMatchEntry.Rarity            -> RarityMatchEntryBuilder(rarity = entry, rarityConsumer = entryConsumer)
            is ItemStackMatchEntry.Tag               -> TagMatchEntryBuilder(tag = entry, tagConsumer = entryConsumer)
            is ItemStackMatchEntry.DataComponentType -> ComponentTypeMatchEntryBuilder(componentType = entry, componentTypeConsumer = entryConsumer)
        }.open()
    }
    RemoveButton { removeAction() }
}

private fun <T : ItemStackMatchEntry> MatchEntryDialog(
    title: Text,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    parentScreen: IGScreen? = mc.currentScreen as IGScreen?,
    entrySupplier: (MatchEntry.MatchMode) -> T,
    entryConsumer: (T) -> Unit,
    content: RowScope.() -> Unit
): IGScreenImpl {
    return Dialog(
        modifier,
        screenModifier,
        parentScreen = parentScreen
    ) {
        TextLabel(title)
        content()
        Column(
            Modifier.matchSibling(),
            horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Right)
        ) {
            Button(
                Modifier.hoverText(MatchEntry.MatchMode.Include.translateComment)
            ) {
                TextLabel(MatchEntry.MatchMode.Include.translateText)
                click {
                    entryConsumer(entrySupplier(MatchEntry.MatchMode.Include))
                    mc.currentScreen?.close()
                }
            }
            Button(
                Modifier.hoverText(MatchEntry.MatchMode.Exclude.translateComment)
            ) {
                TextLabel(MatchEntry.MatchMode.Exclude.translateText)
                click {
                    entryConsumer(entrySupplier(MatchEntry.MatchMode.Exclude))
                    mc.currentScreen?.close()
                }
            }
        }
    }
}

private fun ItemMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    item: ItemStackMatchEntry.Item = ItemStackMatchEntry.Item(mc.player?.mainHandStack?.item ?: Items.MELON, MatchEntry.MatchMode.Include),
    itemConsumer: (ItemStackMatchEntry.Item) -> Unit
): IGScreenImpl {
    val item = mutableStateOf(item.item)
    return MatchEntryDialog(
        title = itemStackMatcherEntryItem,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Item(item.getValue(), mode) },
        entryConsumer = itemConsumer
    ) {
        ItemSelector(
            item,
            modifier = Modifier.width(120f),
            searchBarModifier = { Modifier.width(120f) },
            listModifier = { Modifier.width(120f) },
        )
    }
}

private fun ScriptMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    script: ItemStackMatchEntry.Script = ItemStackMatchEntry.Script("", MatchEntry.MatchMode.Include),
    scriptConsumer: (ItemStackMatchEntry.Script) -> Unit
): IGScreenImpl {
    var script = script.script
    return MatchEntryDialog(
        title = itemStackMatcherEntryScript,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Script(script, mode) },
        entryConsumer = scriptConsumer
    ) {
        TextAreaWrapped(
            modifier = Modifier.size(360f, 240f)
        ) {
            text = script
            textConsumer { script = it }
        }
    }
}

private fun CountMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    count: ItemStackMatchEntry.Count = ItemStackMatchEntry.Count(
        (mc.player?.mainHandStack?.count ?: 1)..(mc.player?.mainHandStack?.count ?: 64),
        MatchEntry.MatchMode.Include
    ),
    countConsumer: (ItemStackMatchEntry.Count) -> Unit
): IGScreenImpl {
    val start = mutableStateOf(count.count.first)
    val end = mutableStateOf(count.count.endInclusive)
    return MatchEntryDialog(
        title = itemStackMatcherEntryCount,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Count(start.getValue()..end.getValue(), mode) },
        entryConsumer = countConsumer
    ) {
        Column {
            IntEditor(start, 1..Int.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
            TextLabel("<= .. <=")
            IntEditor(end, 1..Int.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
        }
    }
}

private fun RarityMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    rarity: ItemStackMatchEntry.Rarity = ItemStackMatchEntry.Rarity(mc.player?.mainHandStack?.rarity ?: Rarity.COMMON, MatchEntry.MatchMode.Include),
    rarityConsumer: (ItemStackMatchEntry.Rarity) -> Unit
): IGScreenImpl {
    val rarity = mutableStateOf(rarity.rarity)
    return MatchEntryDialog(
        title = itemStackMatcherEntryRarity,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Rarity(rarity.getValue(), mode) },
        entryConsumer = rarityConsumer
    ) {
        Selector(
            options = Rarity.entries,
            selected = rarity,
            selectedWrapper = {
                TextLabel(
                    Literal(it.name),
                    modifier = Modifier.weight(1)
                )
            },
            optionWrapper = {
                TextLabel(
                    Literal(it.name),
                    modifier = Modifier
                        .width(
                            Rarity.entries
                                .map { rarity -> rarity.name }
                                .maxWidth(textRenderer).toFloat()
                                .coerceAtLeast(30f)
                        )
                )
            },
            modifier = Modifier.width(120f),
        )
    }
}

private fun TagMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    tag: ItemStackMatchEntry.Tag = ItemStackMatchEntry.Tag("", MatchEntry.MatchMode.Include),
    tagConsumer: (ItemStackMatchEntry.Tag) -> Unit
): IGScreenImpl {
    var tag = tag.tag
    return MatchEntryDialog(
        title = itemStackMatcherEntryTag,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.Tag(tag, mode) },
        entryConsumer = tagConsumer
    ) {
        TextEditor(modifier = Modifier.width(120f)) {
            text = tag
            textConsumer { tag = it }
        }
    }
}

private fun ComponentTypeMatchEntryBuilder(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    componentType: ItemStackMatchEntry.DataComponentType = ItemStackMatchEntry.DataComponentType(DataComponentTypes.FOOD, MatchEntry.MatchMode.Include),
    componentTypeConsumer: (ItemStackMatchEntry.DataComponentType) -> Unit
): IGScreenImpl {
    val componentType: MutableState<ComponentType<*>> = mutableStateOf(componentType.componentType)
    return MatchEntryDialog(
        title = itemStackMatcherEntryDataComponentType,
        modifier = modifier,
        screenModifier = screenModifier,
        entrySupplier = { mode -> ItemStackMatchEntry.DataComponentType(componentType.getValue(), mode) },
        entryConsumer = componentTypeConsumer
    ) {
        DataComponentTypeSelector(
            componentType,
            modifier = Modifier.width(120f),
            searchBarModifier = { Modifier.width(200f) },
            listModifier = { Modifier.width(200f) },
        )
    }

}

