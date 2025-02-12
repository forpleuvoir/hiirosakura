package moe.forpleuvoir.hiirosakura.gui.widget

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
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvent

fun WidgetContainerScope.SoundEventSelector(
    soundEvent: MutableState<SoundEvent>,
    soundEvents: List<SoundEvent> = Registries.SOUND_EVENT.toList(),
    onSelected: (SoundEvent) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(SoundEvent) -> IGWidget = {
        Column(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            TextLabel(Translatable("subtitles.${it.id.path}", it.id.toString()))
        }
    },
    optionWrapper: ButtonScope.(SoundEvent) -> IGWidget = {
        Column(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            TextLabel(Translatable("subtitles.${it.id.path}", it.id.toString()))
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: RowScope.() -> Modifier = { Modifier.width(280f) },
    listModifier: RowScope.() -> Modifier = { Modifier.width(280f) },
    optionsDirection: List<Direction> = Direction.rightLeftBottomTop,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = soundEvents,
    selected = soundEvent,
    predicate = { soundEvent, str ->
        Translatable("subtitles.${soundEvent.id.path}", soundEvent.id.toString()).plainText.contains(str, ignoreCase = true) || Registries.SOUND_EVENT.getKey(soundEvent).toString().contains(str)
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