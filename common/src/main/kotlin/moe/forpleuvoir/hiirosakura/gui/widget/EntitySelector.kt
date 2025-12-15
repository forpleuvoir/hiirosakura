package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxHeight
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType

fun ContainerScope.EntityTypeSelector(
    entityType: MutableState<EntityType<*>>,
    entityTypes: Iterable<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE,
    onSelected: (EntityType<*>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(EntityType<*>) -> GuiWidget = {
        Text(it.description, modifier = Modifier.weight(1))
    },
    optionWrapper: ButtonScope.(EntityType<*>) -> GuiWidget = {
        Text(it.description, modifier = Modifier.weight(1))
    },
    modifier: Modifier = Modifier.width(120f),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.width(entityTypes.map { it.description }.maxWidth + 14f) },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier.width(entityTypes.map { it.description }.maxWidth + 14f).maxHeight(160f) },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = entityTypes,
    selected = entityType,
    predicate = { type, str ->
        type.descriptionId.contains(str, ignoreCase = true)
                || type.description.string.contains(str, ignoreCase = true)
                || BuiltInRegistries.ENTITY_TYPE.getKey(type).toString().contains(str, ignoreCase = true)
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
    amountStep = 15f,
    scope = scope
)