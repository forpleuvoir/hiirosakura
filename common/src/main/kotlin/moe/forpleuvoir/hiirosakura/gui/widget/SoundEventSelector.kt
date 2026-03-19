package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.input.MouseCursor
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent

fun ContainerScope.SoundPlayButton(
    soundSupplier: () -> SimpleSoundInstance,
    modifier: Modifier = Modifier,
) = Box(
    modifier.attachLeft {
        bgHoverHighlightBox(colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f))
            .padding(horizontal = 3f, vertical = 0.5f)
    }.mousePress {
        it.tryUse { wasMouseOver }.onSuccess {
            mc.soundManager.play(soundSupplier())
        }
    }.mouseOverCursor(MouseCursor.POINTING_HAND_CURSOR)
) {
    Icon(IconTextures.RIGHT, Color.ofRGB(0x008000))
}

fun ContainerScope.SoundPlayButton(
    soundEvent: SoundEvent,
    modifier: Modifier = Modifier,
) = SoundPlayButton({
    SimpleSoundInstance.forUI(soundEvent, 1f, 1.35f)
}, modifier)

fun ContainerScope.SoundPlayButton(
    soundEvent: Holder<SoundEvent>,
    modifier: Modifier = Modifier,
) = SoundPlayButton({
    SimpleSoundInstance.forUI(soundEvent.value(), 1f, 1.35f)
}, modifier)

fun ContainerScope.SoundEventSelector(
    soundEvent: MutableState<SoundEvent>,
    soundEvents: List<SoundEvent> = BuiltInRegistries.SOUND_EVENT.toList(),
    onSelected: (SoundEvent) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(SoundEvent) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            SoundPlayButton(it)
            Text(Translatable("subtitles.${it.location.path}", it.location.toString()))
        }
    },
    optionWrapper: ButtonScope.(SoundEvent) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            SoundPlayButton(it)
            Text(Translatable("subtitles.${it.location.path}", it.location.toString()))
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(soundEvents.map { Translatable("subtitles.${it.location.path}", it.location.toString()) }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(soundEvents.map { Translatable("subtitles.${it.location.path}", it.location.toString()) }.maxWidth + 12f).maxHeight(180f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = soundEvents,
    selected = soundEvent,
    predicate = { soundEvent, str ->
        Translatable("subtitles.${soundEvent.location.path}", soundEvent.location.toString()).plainText.contains(
            str,
            ignoreCase = true
        ) || BuiltInRegistries.SOUND_EVENT.getKey(
            soundEvent
        ).toString().contains(str)
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

fun ContainerScope.HolderSoundEventSelector(
    soundEvent: MutableState<Holder<SoundEvent>>,
    soundEvents: List<Holder<SoundEvent>> = BuiltInRegistries.SOUND_EVENT.asHolderIdMap().toList(),
    onSelected: (Holder<SoundEvent>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Holder<SoundEvent>) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            SoundPlayButton(it)
            Text(Translatable("subtitles.${it.value().location.path}", it.registeredName))
        }
    },
    optionWrapper: ButtonScope.(Holder<SoundEvent>) -> GuiWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            SoundPlayButton(it)
            Text(Translatable("subtitles.${it.value().location.path}", it.registeredName))
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(soundEvents.map { Translatable("subtitles.${it.value().location.path}", it.registeredName) }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(soundEvents.map { Translatable("subtitles.${it.value().location.path}", it.registeredName) }.maxWidth + 12f).maxHeight(160f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = soundEvents,
    selected = soundEvent,
    predicate = { soundEvent, str ->
        val soundEvent = soundEvent.value()
        Translatable("subtitles.${soundEvent.location.path}", soundEvent.location.toString()).plainText.contains(
            str,
            ignoreCase = true
        ) || BuiltInRegistries.SOUND_EVENT.getKey(
            soundEvent
        ).toString().contains(str)
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