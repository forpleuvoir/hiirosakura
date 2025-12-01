package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.registryManager
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
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
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
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
import net.minecraft.component.ComponentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.item.Item
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import kotlin.jvm.optionals.getOrNull

fun ContainerScope.ItemSelector(
    item: MutableState<Item>,
    items: List<Item> = Registries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Item) -> IGWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            ItemIcon(it, .6f)
            TextLabel(it.name.copyToText())
        }
    },
    optionWrapper: ButtonScope.(Item) -> IGWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(it, .6f)
            TextLabel(it.name.copyToText())
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
        item.name.string.contains(str, ignoreCase = true) || Registries.ITEM.getKey(item).toString().contains(str)
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
    items: List<Item> = Registries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    selectorBGColor: ARGBColor = Color(0xffffccf0),
    optionsDirection: List<Direction> = listOf(Direction.Bottom, Direction.Right, Direction.Top, Direction.Left),
    amountStep: Float? = null,
    modifier: Modifier = Modifier
) = Button(
    modifier.attachLeft {
        padding(horizontal = 5f, vertical = 4f)
            .render { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                }
            }
    },
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ItemIcon(item, .6f)
    val text = mutableStateOf(item.getValue().name.copyToText())
    TextLabel(text)
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
    items: List<Item> = Registries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    columnSize: Int = 9,
    searchBarHideLimit: Int = 9 * columnSize,
    selectedColor: ARGBColor = defaultSelectedColor,
    optionWrapper: ButtonScope.(Item) -> IGWidget = {
        ItemIcon(it, .9f)
    },
    modifier: Modifier = Modifier,
    bgColor: ARGBColor = Color(0xffffccf0),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.matchSibling() },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier.height(147f).width(147f) },
    amountStep: Float? = null
) = Column(
    modifier = modifier.attachLeft {
        padding(5)
            .renderBackground { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_BG, bgColor)
                }
            }
    }
) {
    val showList = notifiableList(items)
    if (items.size > searchBarHideLimit) {
        SearchBar(
            textConsumer = { str ->
                showList.disableNotify {
                    showList.clear()
                    showList.addAll(items.filter { it.name.string.contains(str, ignoreCase = true) || Registries.ITEM.getKey(it).toString().contains(str) })
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
            padding(3f).renderBackground { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_OUTLINE)
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_INNER)
                }
            }
        },
        listModifier = listModifier
    ) {
        amountStep?.let(::amountStep)
        if (showList.isEmpty()) {
            TextLabel(IGLang.hasNothing)
            return@ColumnListWrapped
        }
        showList.chunked(columnSize).forEach { column ->
            Row(
                modifier = Modifier.fill(),
                horizontalArrangement = Arrangement.Left
            ) {
                column.forEach { item ->
                    FlatButton(
                        modifier = Modifier.hoverText(item.name.copyToText().append(Literal("\n${item.id}").withColor(Color(10, 136, 226)))),
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
    componentType: MutableState<ComponentType<*>>,
    componentTypes: List<ComponentType<*>> = Registries.DATA_COMPONENT_TYPE.toList(),
    onSelected: (ComponentType<*>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(ComponentType<*>) -> IGWidget = {
        TextLabel(
            it.id.toString(),
            modifier = Modifier.weight(1)
        )
    },
    optionWrapper: ButtonScope.(ComponentType<*>) -> IGWidget = {
        TextLabel(
            it.id.toString(),
            modifier = Modifier.weight(1)
        )
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = componentTypes,
    predicate = { type, str ->
        type.id.toString().contains(str)
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
    enchantment: MutableState<RegistryEntry<Enchantment>>,
    enchantments: List<RegistryEntry<Enchantment>> = REGISTERED_ENCHANTMENT,
    onSelected: (RegistryEntry<Enchantment>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(RegistryEntry<Enchantment>) -> IGWidget = {
        TextLabel(
            it.value().description.copyToText(),
            modifier = Modifier.weight(1)
        )
    },
    optionWrapper: ButtonScope.(RegistryEntry<Enchantment>) -> IGWidget = {
        TextLabel(
            it.value().description.copyToText(),
            modifier = Modifier.weight(1)
        )
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = enchantments,
    predicate = { type, str ->
        type.idAsString.contains(str) || type.value().description.string.contains(str)
    },
    selected = enchantment,
    checker = { a, b -> a.idAsString == b.idAsString },
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
    selectedWrapper: DropDownMenuScope.(String) -> IGWidget = {
        TextLabel(enchantmentDescription(it), modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(String) -> IGWidget = {
        TextLabel(enchantmentDescription(it), modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(120f) },
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

val REGISTERED_ENCHANTMENT: List<RegistryEntry<Enchantment>>
    get() = registryManager?.getOrThrow(RegistryKeys.ENCHANTMENT)?.indexedEntries?.toList() ?: emptyList()


val REGISTERED_ENCHANTMENT_ID get() = REGISTERED_ENCHANTMENT.map { it.idAsString }

fun enchantmentDescription(id: String): Text {
    return REGISTERED_ENCHANTMENT.find { it.idAsString == id }?.value()?.description?.copyToText() ?: Literal(id)
}

@Deprecated("The type of enchantment registered by the client side, after a forced transfer, is not recommended", replaceWith = ReplaceWith("REGISTERED_ENCHANTMENT"))
val CLIENT_REGISTERED_ENCHANTMENT: List<RegistryEntry.Reference<Enchantment>> by lazy {
    BuiltinRegistries.createWrapperLookup().getOrThrow(RegistryKeys.ENCHANTMENT).run {
        streamKeys().map { registryKey ->
            (this.getOptional(registryKey).getOrNull() as RegistryEntry.Reference<Enchantment>)
        }.toList()
    }
}


val REGISTERED_ATTRIBUTE get() = registryManager?.getOrThrow(RegistryKeys.ATTRIBUTE)?.indexedEntries?.toList() ?: emptyList()

fun ContainerScope.EntiryAttributeSelector(
    entityAttribute: MutableState<RegistryEntry<EntityAttribute>>,
    entityAttributes: List<RegistryEntry<EntityAttribute>> = REGISTERED_ATTRIBUTE,
    onSelected: (RegistryEntry<EntityAttribute>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(RegistryEntry<EntityAttribute>) -> IGWidget = {
        TextLabel(it.idAsString, modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(RegistryEntry<EntityAttribute>) -> IGWidget = {
        TextLabel(it.idAsString, modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width((entityAttributes.map { it.idAsString }.maxWidth + 12f).coerceAtLeast(120f)) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width((entityAttributes.map { it.idAsString }.maxWidth + 12f).coerceAtLeast(120f)) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = 15f,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = entityAttributes,
    predicate = { type, str -> type.idAsString.contains(str) },
    selected = entityAttribute,
    checker = { a, b -> a.idAsString == b.idAsString },
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