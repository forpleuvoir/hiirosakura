package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.util.ENCHANTMENT_LIST
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.component.ComponentType
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry

fun WidgetContainerScope.ItemSelector(
    item: MutableState<Item>,
    items: List<Item> = Registries.ITEM.toList(),
    onSelected: (Item) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Item) -> IGWidget = {
        Column(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            ItemIcon(it, .6f)
            TextLabel(it.name.copyToText())
        }
    },
    optionWrapper: ButtonScope.(Item) -> IGWidget = {
        Column(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(it, .6f)
            TextLabel(it.name.copyToText())
        }
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
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
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)

fun WidgetContainerScope.DataComponentTypeSelector(
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
    searchBarModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
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
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)

fun WidgetContainerScope.EnchatmentSelector(
    enchantment: MutableState<RegistryEntry<Enchantment>>,
    enchantments: List<RegistryEntry<Enchantment>> = ENCHANTMENT_LIST,
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
    searchBarModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
    listModifier: RowScope.() -> Modifier = { Modifier.width(120f) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
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
    searchBarModifier = searchBarModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)