package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxHeight
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN
import net.minecraft.world.item.component.DamageResistant

fun ContainerScope.DamageResistantComponentWrapper(
    key: ResourceLocation,
    component: DamageResistant,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (DamageResistant, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val valueState = component.types().asMutableState
    valueState.subscribe { onValueChange(DamageResistant(it), false) }
    DamageTypeTagSelector(valueState, modifier = Modifier.width(140f))
}

internal val damageTypeTags
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).tags.map { it.key() }

internal val TagKey<DamageType>.damageTypes
    get() = registryAccess!!.lookupOrThrow(Registries.DAMAGE_TYPE).getTagOrEmpty(this)

internal val DamageType.translatableText
    get() = when (this.deathMessageType()) {
        INTENTIONAL_GAME_DESIGN -> Text.translatable(
            "death.attack.${this.msgId}.message",
            "xx",
            ComponentUtils.wrapInSquareBrackets(Text.translatable("death.attack.${this.msgId}.link"))
        )

        else                    -> Text.translatable("death.attack.${this.msgId}", "xx", "oo")
    }

fun ContainerScope.DamageTypeTagSelector(
    damageTypeTag: MutableState<TagKey<DamageType>>,
    damageTypeTags: Iterable<TagKey<DamageType>> = moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.damageTypeTags.toList(),
    onSelected: (TagKey<DamageType>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(TagKey<DamageType>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),
            modifier = Modifier.weight(1)
                .hoverTip {
                    val types = it.damageTypes
                    if (types.count() == 0) {
                        Text(IGLang.hasNothing)
                        return@hoverTip
                    } else {
                        Column(horizontalAlignment = Alignment.Left) {
                            types.forEachWithLimit(20) { type ->
                                Text(type.value().translatableText)
                            }
                            if (types.count() > 20) Text("......")
                        }
                    }
                }
        ) {
            Text("#${it.location}")
        }
    },
    optionWrapper: ButtonScope.(TagKey<DamageType>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),

            modifier = Modifier.weight(1).hoverTip {
                val types = it.damageTypes
                if (types.count() == 0) {
                    Text(IGLang.hasNothing)
                    return@hoverTip
                } else {
                    Column(horizontalAlignment = Alignment.Left) {
                        types.forEachWithLimit(20) { type ->
                            Text(type.value().translatableText)
                        }
                        if (types.count() > 20) Text("......")
                    }
                }
            }
        ) {
            Text("#${it.location}")
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(damageTypeTags.map { "#${it.location}" }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(damageTypeTags.map { "#${it.location}" }.maxWidth + 12f).maxHeight(160f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = damageTypeTags,
    selected = damageTypeTag,
    predicate = { tag, str ->
        "#${tag.location}".contains(str)
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
