package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.hiirosakura.util.keyOrUnknown
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRender
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.tip.PopupTip
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.util.collection.notifiableList
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import kotlin.jvm.optionals.getOrNull

fun ContainerScope.ItemSelector(
    item: MutableState<Item>,
    items: List<Item> = BuiltInRegistries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Item) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            ItemIcon(it, .6f)
            Text(it.name.copyToText())
        }
    },
    optionWrapper: ButtonScope.(Item) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(it, .6f)
            Text(it.name.copyToText())
        }
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = items,
    selected = item,
    predicate = { item, str ->
        item.name.string.contains(str, ignoreCase = true) || BuiltInRegistries.ITEM.getKey(item).toString().contains(str)
    },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)

fun ContainerScope.ItemSelector(
    item: MutableState<Item>,
    items: List<Item> = BuiltInRegistries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    selectorBGColor: ARGBColor = Color.ofRGB(0xFFCCF0),
    optionsDirection: List<Direction> = listOf(Direction.Bottom, Direction.Right, Direction.Top, Direction.Left),
    amountStep: Float? = null,
    modifier: Modifier = Modifier
) = Button(
    modifier.attachLeft {
        padding(horizontal = 5f, vertical = 4f)
            .render { guiGraphics, _, _, _ ->
                guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
            }
    },
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ItemIcon(item, .6f)
    val text = mutableStateOf(item.getValue().name.copyToText())
    Text(text)
    click {
        PopupTip(
            modifier = Modifier.disableRender().margin(0f).padding(0f),
            optionalDirection = notifiableList(optionsDirection)
        ) {
            ItemSelector(items = items, bgColor = selectorBGColor, amountStep = amountStep, onSelected = {
                item.setValue(it)
                onSelected(it)
                text.setValue(it.name.copyToText())
                closeScreen()
            })
        }.open()
    }
}

fun ContainerScope.ItemSelector(
    items: List<Item> = BuiltInRegistries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    columnSize: Int = 9,
    searchBarHideLimit: Int = 9 * columnSize,
    selectedColor: ARGBColor = defaultSelectedColor,
    optionWrapper: ButtonScope.(Item) -> GuiWidget = {
        ItemIcon(it, 1f)
    },
    modifier: Modifier = Modifier,
    bgColor: ARGBColor = Color.ofRGB(0xFFCCF0),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.matchSibling() },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier.height(162.5f).width(162.5f) },
    amountStep: Float? = null
) = Column(
    modifier = modifier.attachLeft {
        padding(5)
            .renderBackground { guiGraphics, _, _, _ ->
                guiGraphics.pushWidgetTexture(transform, WidgetTextures.DIALOG_BG, bgColor)
            }
    }
) {
    val showList = notifiableList(items)
    if (items.size > searchBarHideLimit) {
        SearchBar(
            textConsumer = { str ->
                showList.disableNotify {
                    showList.clear()
                    showList.addAll(items.filter {
                        it.name.string.contains(str, ignoreCase = true) || BuiltInRegistries.ITEM.getKey(it).toString().contains(str)
                    })
                }
                showList.onChange(showList)
            },
            hintText = stateOf(IGLang.search.plainText),
            modifier = searchBarModifier().padding(0).disableRender(),
            textEditorModifier = { Modifier.weight(1) }
        )
    }
    ColumnListWrapped(
        modifier = listWrapperModifier().attachLeft {
            padding(3f).renderBackground { guiGraphics, _, _, _ ->
                guiGraphics {
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_OUTLINE)
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_INNER)
                }
            }
        },
        listModifier = listModifier
    ) {
        amountStep?.let(::amountStep)
        if (showList.isEmpty()) {
            Text(IGLang.hasNothing)
            return@ColumnListWrapped
        }
        showList.chunked(columnSize).forEach { column ->
            Row(
                modifier = Modifier.fill(),
                horizontalArrangement = Arrangement.Left
            ) {
                column.forEach { item ->
                    FlatButton(
                        modifier = Modifier.hoverText(item.name.copyToText().append(Literal("\n${item.key}").withColor(Color(10, 136, 226)))),
                        hoveredColor = selectedColor,
                        round = 1,
                    ) {
                        optionWrapper(item)
                        click { onSelected(item) }
                    }
                }
            }
        }
    }.apply {
        showList.subscribe {
            executeRecompose()
        }
    }
}

fun ContainerScope.DataComponentTypeSelector(
    componentType: MutableState<DataComponentType<*>>,
    componentTypes: List<DataComponentType<*>> = BuiltInRegistries.DATA_COMPONENT_TYPE.toList(),
    onSelected: (DataComponentType<*>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(DataComponentType<*>) -> GuiWidget = {
        Text(
            it.keyOrUnknown.toString(),
            modifier = Modifier.weight(1)
        )
    },
    optionWrapper: ButtonScope.(DataComponentType<*>) -> GuiWidget = {
        Text(
            it.keyOrUnknown.toString(),
            modifier = Modifier.weight(1)
        )
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width((componentTypes.map { it.keyOrUnknown.toString() }.maxWidth + 12f).coerceAtLeast(210f))
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width((componentTypes.map { it.keyOrUnknown.toString() }.maxWidth + 12f).coerceAtLeast(210f)).maxHeight(150f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = componentTypes,
    predicate = { type, str ->
        type.key.toString().contains(str)
    },
    selected = componentType,
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)

fun ContainerScope.EnchatmentSelector(
    enchantment: MutableState<Holder<Enchantment>>,
    enchantments: List<Holder<Enchantment>> = REGISTERED_ENCHANTMENT,
    onSelected: (Holder<Enchantment>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Holder<Enchantment>) -> GuiWidget = {
        Text(
            it.value().description.copyToText(),
            modifier = Modifier.weight(1)
        )
    },
    optionWrapper: ButtonScope.(Holder<Enchantment>) -> GuiWidget = {
        Text(
            it.value().description.copyToText(),
            modifier = Modifier.weight(1)
        )
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width((enchantments.map { it.value().description }.maxWidth + 12f).coerceAtLeast(80f))
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width((enchantments.map { it.value().description }.maxWidth + 12f).coerceAtLeast(80f)).maxHeight(150f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = enchantments,
    predicate = { type, str ->
        type.registeredName.contains(str) || type.value().description.string.contains(str)
    },
    selected = enchantment,
    checker = { a, b -> a.registeredName == b.registeredName },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    listWrapperModifier = listWrapperModifier,
    searchBarModifier = searchBarModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)

@JvmName("EnchantmentSelectorString")
fun ContainerScope.EnchatmentSelector(
    enchantment: MutableState<String>,
    enchantments: List<String> = REGISTERED_ENCHANTMENT_ID,
    onSelected: (String) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(String) -> GuiWidget = {
        Text(enchantmentDescription(it), modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(String) -> GuiWidget = {
        Text(enchantmentDescription(it), modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width((enchantments.map { enchantmentDescription(it) }.maxWidth + 12f).coerceAtLeast(80f))
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width((enchantments.map { enchantmentDescription(it) }.maxWidth + 12f).coerceAtLeast(80f)).maxHeight(150f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = enchantments,
    predicate = { type, str ->
        enchantmentDescription(type).string?.contains(str) == true
    },
    selected = enchantment,
    checker = { a, b -> a == b },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)

val REGISTERED_ENCHANTMENT: List<Holder<Enchantment>>
    get() = registryAccess?.lookupOrThrow(Registries.ENCHANTMENT)?.asHolderIdMap()?.toList() ?: emptyList()


val REGISTERED_ENCHANTMENT_ID get() = REGISTERED_ENCHANTMENT.map { it.registeredName }

fun enchantmentDescription(id: String): Text {
    return REGISTERED_ENCHANTMENT.find { it.registeredName == id }?.value()?.description?.copyToText() ?: Literal(id)
}

@Deprecated(
    "The type of enchantment registered by the client side, after a forced transfer, is not recommended",
    replaceWith = ReplaceWith("REGISTERED_ENCHANTMENT")
)
val CLIENT_REGISTERED_ENCHANTMENT: List<Holder.Reference<Enchantment>> by lazy {
    VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT).run {
        listElementIds().map { registryKey ->
            (this.get(registryKey).getOrNull() as Holder.Reference<Enchantment>)
        }.toList()
    }
}


val REGISTERED_ATTRIBUTE get() = registryAccess?.lookupOrThrow(Registries.ATTRIBUTE)?.asHolderIdMap()?.toList() ?: emptyList()

fun ContainerScope.EntiryAttributeSelector(
    entityAttribute: MutableState<Holder<Attribute>>,
    entityAttributes: List<Holder<Attribute>> = REGISTERED_ATTRIBUTE,
    onSelected: (Holder<Attribute>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Holder<Attribute>) -> GuiWidget = {
        Text(it.registeredName, modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(Holder<Attribute>) -> GuiWidget = {
        Text(it.registeredName, modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width((entityAttributes.map { it.registeredName }.maxWidth + 12f).coerceAtLeast(120f)) },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width((entityAttributes.map { it.registeredName }.maxWidth + 12f).coerceAtLeast(120f)).maxHeight(150f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = entityAttributes,
    predicate = { type, str -> type.registeredName.contains(str) },
    selected = entityAttribute,
    checker = { a, b -> a.registeredName == b.registeredName },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    listWrapperModifier = listWrapperModifier,
    searchBarModifier = searchBarModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep,
    scope = scope
)