package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
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
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.item.EitherHolder

fun ContainerScope.DamageTypeComponentWrapper(
    key: ResourceLocation,
    component: EitherHolder<DamageType>,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (EitherHolder<DamageType>, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val left = damageTypes.get(component.key().get()).get()
    val valueState = EitherHolder(left).asMutableState
    valueState.subscribe { onValueChange(it, false) }
    DamageTypeSelector(valueState, modifier = Modifier.width(140f))
}


internal val damageTypes get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE)

fun ContainerScope.DamageTypeSelector(
    damageType: MutableState<EitherHolder<DamageType>>,
    damageTypes: Iterable<EitherHolder<DamageType>> = moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.damageTypes.asHolderIdMap().map {
        EitherHolder(it)
    },
    onSelected: (EitherHolder<DamageType>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(EitherHolder<DamageType>) -> GuiWidget = {
        Text(
            it.key().get().location().asTranslateText(),
            modifier = Modifier.weight(1)
                .hoverText(it.contents().left().get().value().translatableText)
        )
    },
    optionWrapper: ButtonScope.(EitherHolder<DamageType>) -> GuiWidget = {
        Text(
            it.key().get().location().asTranslateText(),
            modifier = Modifier.weight(1)
                .hoverText(it.contents().left().get().value().translatableText)
        )
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(damageTypes.map { it.key().get().location().asTranslateText() }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(damageTypes.map { it.key().get().location().asTranslateText() }.maxWidth + 12f).maxHeight(160f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = damageTypes,
    selected = damageType,
    predicate = { tag, str ->
        tag.key().get().location().asTranslateText().plainText.contains(str)
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
