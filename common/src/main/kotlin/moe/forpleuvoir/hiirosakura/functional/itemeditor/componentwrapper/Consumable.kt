package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.IdentifierText
import moe.forpleuvoir.hiirosakura.gui.widget.*
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnWidget
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.component.Consumable
import net.minecraft.world.item.consume_effects.*

//region Wrapper
fun ContainerScope.ConsumableComponentWrapper(
    key: Identifier,
    component: Consumable,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Consumable, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit)
    ) {
        Icon(IconTextures.EDIT)
        click {
            ConsumableEditor(
                key.asTranslateText(),
                component,
                modifier = Modifier.width(450f).fill(),
                screenModifier = Modifier.padding(20f),
                onValueChange = onValueChange
            ).open()
        }
    }

}
//endregion

//region Editor
fun ConsumableEditor(
    title: Text,
    component: Consumable,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Consumable, Boolean) -> Unit,
): IGScreenImpl {
    //region 声明变量
    val consumeSeconds = component.consumeSeconds.asMutableState
    val animation = component.animation.asMutableState
    val sound = component.sound.asMutableState
    val hasConsumeParticles = component.hasConsumeParticles.asMutableState
    val onConsumeEffects = ArrayList(component.onConsumeEffects)
    //endregion

    var listRecompose by lateInitValueOf<() -> Unit>()

    return DataComponentEditor(
        title,
        {
            Consumable(
                consumeSeconds.getValue(),
                animation.getValue(),
                sound.getValue(),
                hasConsumeParticles.getValue(),
                onConsumeEffects
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        Column(Modifier.fill()) {
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("consume_seconds")
                    FloatEditor(consumeSeconds, 0f..Float.MAX_VALUE, editorModifier = { Modifier.width(60f) })
                }
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("animation")
                    EnumSelector(animation, ItemUseAnimation.entries, modifier = Modifier.width(100f))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("sound")
                    HolderSoundEventSelector(sound, modifier = Modifier.width(73f))
                }
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("has_consume_particles")
                    SwitchButton(hasConsumeParticles)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("on_consume_effects")
                    Selector(
                        options = ConsumeEffectTypes.entries,
                        selected = ConsumeEffectTypes.entries.first().asMutableState,
                        modifier = Modifier.width(73f),
                        selectedWrapper = {
                            Text(IGLang.add)
                        },
                        optionWrapper = {
                            Text(it.key)
                        },
                        onSelected = {
                            onConsumeEffects.add(it.value)
                            listRecompose()
                        }
                    )
                }
                Box(Modifier.weight(1)) {  }
            }
        }
        ColumnListWrapped(Modifier.height(250f).fill(), spacing = 2f, listModifier = { Modifier.weight(1).fill() }) {
            listRecompose = { this.executeRecompose() }
            onConsumeEffects.forEachIndexed { index, effect ->
                ConsumeEffectWrapper(
                    effect,
                    Modifier.fill().bgHoverHighlightBox(),
                    listRecompose,
                    {
                        onConsumeEffects.removeAt(index)
                    },
                    { effect, shouldRecompose ->
                        onConsumeEffects[index] = effect
                        if (shouldRecompose) listRecompose()
                    }
                )
            }
        }
    }
}

private val ConsumeEffectTypes = mapOf<String, ConsumeEffect>(
    "apply_effects" to ApplyStatusEffectsConsumeEffect(MobEffectInstance(MobEffects.LUCK)),
    "remove_effects" to RemoveStatusEffectsConsumeEffect(MobEffects.LUCK),
    "clear_all_effects" to ClearAllStatusEffectsConsumeEffect.INSTANCE,
    "teleport_randomly" to TeleportRandomlyConsumeEffect(),
    "play_sound" to PlaySoundConsumeEffect(SoundEvents.GENERIC_EAT),
)
//endregion

//region Wrapper
fun ContainerScope.ConsumeEffectWrapper(
    effect: ConsumeEffect,
    modifier: Modifier = Modifier,
    recompose: () -> Unit,
    deleteAction: () -> Unit,
    onValueChange: (ConsumeEffect, Boolean) -> Unit
) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        val id = BuiltInRegistries.CONSUME_EFFECT_TYPE.getKey(effect.type) ?: Identifier.parse("minecraft:unknown")
        IdentifierText(id, modifier = Modifier.margin(left = 2f))
        Row {
            when (effect) {
                is ApplyStatusEffectsConsumeEffect    -> ApplyStatusEffectsWrapper(effect, Modifier.width(120f), onValueChange = onValueChange)
                is RemoveStatusEffectsConsumeEffect   -> RemoveStatusEffectsWrapper(effect, Modifier.width(120f), onValueChange = onValueChange)
                is TeleportRandomlyConsumeEffect      -> TeleportRandomlyWrapper(effect, Modifier.width(120f), onValueChange = onValueChange)
                is PlaySoundConsumeEffect             -> PlaySoundWrapper(effect, Modifier.width(120f), onValueChange = onValueChange)
                is ClearAllStatusEffectsConsumeEffect -> Text("Unit", modifier = Modifier.height(18f))
            }
            DeleteButton(
                modifier = Modifier.margin(left = 3f),
                confirmMessage = { IGLang.removeConfirm("") },
                recompose = recompose,
                deleteAction = deleteAction
            )
        }
    }
}


//endregion

//region ApplyStatusEffects

fun ContainerScope.ApplyStatusEffectsWrapper(
    effect: ApplyStatusEffectsConsumeEffect,
    modifier: Modifier = Modifier,
    onValueChange: (ConsumeEffect, Boolean) -> Unit
) {
    var wrapperRecompose by lateInitValueOf<() -> Unit>()
    var effect = effect
    Row(modifier) {
        Button(
            Modifier.weight(1)
                .hoverTip {
                    if (effect.effects.isEmpty()) {
                        Text(IGLang.hasNothing)
                    } else {
                        Column {
                            effect.effects.forEachWithLimit(10) {
                                Text(it.effect.value().displayName)
                            }
                        }
                    }
                }
        ) {
            Text(IGLang.listConfigWrapperText(effect.effects.size))
            click {
                //region ApplyStatusEffectsEditor
                ConfirmDialog(
                    title = Identifier.parse("minecraft:apply_effects").asTranslateText(),
                    screenModifier = Modifier.padding(20f)
                ) {
                    val effects = mutableListOf<MobEffectInstance>().apply {
                        effect.effects.forEach {
                            add(it)
                        }
                    }
                    val probability = effect.probability.asMutableState
                    onConfirm = {
                        effect = ApplyStatusEffectsConsumeEffect(effects, probability.getValue())
                        onValueChange(effect, true)
                        closeScreen()
                        wrapperRecompose()
                    }
                    var listRecompose by lateInitValueOf<() -> Unit>()
                    Row(
                        Modifier.width(320f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4f)) {
                            Text("probability")
                            SwitchableFloatEditor(probability, 0f..1f, 60f, defaultEditor = SwitchableNumberEditorType.Slider)
                        }
                        HolderMobEffectSelector(
                            MobEffects.LUCK.asMutableState,
                            modifier = Modifier.width(125F),
                            selectedWrapper = {
                                Text("Add Form MobEffect", modifier = Modifier.width(100f))
                            },
                            onSelected = {
                                closeScreen()
                                MobEffectInstanceEditor(MobEffectInstance(it)) {
                                    effects.add(it)
                                    listRecompose()
                                }.open()
                            }
                        )
                    }
                    ColumnListWrapped(Modifier.width(320f).weight(1), listModifier = { Modifier.fill() }) {
                        listRecompose = { this.executeRecompose() }
                        effects.forEachIndexed { index, instance ->
                            MobEffectInstanceEntryWrapper(instance, index, {
                                effects.removeAt(index)
                                effect
                                listRecompose()
                            }) {
                                effects[index] = instance
                            }
                        }
                    }
                }.open()
                //endregion
            }
        }
    }.apply {
        wrapperRecompose = { this.executeRecompose() }
    }
}

//region MobEffectInstanceEntryWrapper
fun ContainerScope.MobEffectInstanceEntryWrapper(
    mobEffectInstance: MobEffectInstance,
    index: Int,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onValueChange: (MobEffectInstance) -> Unit
): ColumnWidget = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    val icon = mutableStateOf(WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
    val expanded = mutableStateOf(false)
    expanded.subscribe {
        icon.setValue(it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN))
    }
    var childrenBox by lateInitValueOf { Box.Unspecified }

    var mobEffectInstance = mobEffectInstance

    //region 属性声明
    val effect = mobEffectInstance.effect.asMutableState
    effect.subscribe {
        mobEffectInstance = MobEffectInstance(
            it,
            mobEffectInstance.duration,
            mobEffectInstance.amplifier,
            mobEffectInstance.isAmbient,
            mobEffectInstance.isVisible,
            mobEffectInstance.showIcon(),
            mobEffectInstance.hiddenEffect
        )
        onValueChange(mobEffectInstance)
    }
    val duration = mobEffectInstance.duration.asMutableState
    duration.subscribe {
        mobEffectInstance.duration = it
        onValueChange(mobEffectInstance)
    }
    val amplifier = mobEffectInstance.amplifier.asMutableState
    amplifier.subscribe {
        mobEffectInstance.amplifier = it.coerceIn(MobEffectInstance.MIN_AMPLIFIER, MobEffectInstance.MAX_AMPLIFIER)
        onValueChange(mobEffectInstance)
    }
    val ambient = mobEffectInstance.isAmbient.asMutableState
    ambient.subscribe {
        mobEffectInstance.ambient = it
        onValueChange(mobEffectInstance)
    }

    val visible = mobEffectInstance.visible.asMutableState
    visible.subscribe {
        mobEffectInstance.visible = it
        onValueChange(mobEffectInstance)
    }
    val showIcon = mobEffectInstance.showIcon.asMutableState
    showIcon.subscribe {
        mobEffectInstance.showIcon = it
        onValueChange(mobEffectInstance)
    }
    //endregion
    val width = 314f
    Button(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.bgHoverHighlightBox()
            .width(320f)
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
            .padding(0)
    ) {
//        Text(title)
        //effect
        Row(
            modifier = Modifier.padding(2f).weight(1).bgHoverHighlightBox(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("effect:$index")
            HolderMobEffectSelector(effect, modifier = Modifier.width(100f))
        }
        Icon(icon, modifier = Modifier.padding(vertical = 2.5f, horizontal = 2f))
        DeleteButton(
            confirmMessage = { IGLang.removeConfirm("$index : ${effect.getValue().value().displayName.string}") },
            recompose = {}
        ) {
            onRemove()
        }
        click { expanded.switch() }
    }
    SwitchableProxy(
        {
            Row {
                Widget(
                    Modifier.width(1.5f)
                        .matchSibling()
                        .padding(0, 0, 2, 2)
                        .margin(4, 0.5, 0, 0)
                        .render { guiGraphics, _, _, _ ->
                            guiGraphics.pushBox(
                                transform.asWorldCoordinateBox.trimEdges(padding.top, padding.bottom, padding.left, padding.right),
                                configContainerWrapperGuidelinesColor
                            )
                        }
                )
                Column {
                    //duration
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("duration")
                        IntEditor(duration, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
                    }
                    //amplifier
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("amplifier")
                        IntEditor(amplifier, range = 0..255, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
                    }
                    //ambient
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ambient")
                        SwitchButton(ambient)
                    }
                    //visible
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("visible")
                        SwitchButton(visible)
                    }
                    //showIcon
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("show_icon")
                        SwitchButton(showIcon)
                    }
                    //hiddenEffect
                    Row(
                        modifier = Modifier.padding(2f).width(width).bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) hiddenEffect@{
                        Text("hidden_effect")
                        mobEffectInstance.hiddenEffect?.let { hiddenEffect ->
                            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                                Text(hiddenEffect.effect.value().displayName)
                                EditButton {
                                    MobEffectInstanceEditor(hiddenEffect) {
                                        mobEffectInstance.hiddenEffect = it
                                        onValueChange(mobEffectInstance)
                                        this@hiddenEffect.executeRecompose()
                                    }.open()
                                }
                                DeleteButton(
                                    confirmMessage = { IGLang.removeConfirm("hidden_effect") },
                                    recompose = { this.executeRecompose() }
                                ) {
                                    mobEffectInstance.hiddenEffect = null
                                }
                            }
                        } ?: run {
                            AddButton {
                                MobEffectInstanceEditor(MobEffectInstance(MobEffects.LUCK)) {
                                    mobEffectInstance.hiddenEffect = it
                                    onValueChange(mobEffectInstance)
                                    this@hiddenEffect.executeRecompose()
                                }.open()
                            }
                        }
                    }
                }
            }.apply {
                childrenBox = { this.transform.asWorldCoordinateBox }
            }
        },
        {
            childrenBox = { Box.Unspecified }
            Widget(Modifier)
        },
        expanded
    )

}
//endregion


//endregion

//region RemoveStatusEffects
fun ContainerScope.RemoveStatusEffectsWrapper(
    effect: RemoveStatusEffectsConsumeEffect,
    modifier: Modifier = Modifier,
    onValueChange: (ConsumeEffect, Boolean) -> Unit
) {
    val effects = effect.effects.asMutableState
    effects.onSetValue = {
        onValueChange(RemoveStatusEffectsConsumeEffect(it), false)
        it
    }
    Button(
        modifier.attachLeft {
            hoverTip {
                val list = effects.getValue().toList()
                if (list.isEmpty()) {
                    Text(IGLang.hasNothing)
                } else {
                    Column {
                        list.forEachWithLimit(10) {
                            Text(it.value().displayName)
                        }
                    }
                }
            }
        }
    ) {
        Text(IGLang.listConfigWrapperText(effects.getValue().size()))
        click {
            MobEffectEditor(effects.getValue()) {
                effects.setValue(it)
                this.executeRecompose()
            }.open()
        }
    }
}
//endregion

//region TeleportRandomly
fun ContainerScope.TeleportRandomlyWrapper(
    effect: TeleportRandomlyConsumeEffect,
    modifier: Modifier = Modifier,
    onValueChange: (ConsumeEffect, Boolean) -> Unit
) {
    val diameter = effect.diameter.asMutableState
    diameter.onSetValue = {
        onValueChange(TeleportRandomlyConsumeEffect(it), false)
        it
    }
    FloatEditor(diameter, 0f..Float.MAX_VALUE, modifier = modifier, editorModifier = { Modifier.weight(1) })
}
//endregion

//region PlaySound
fun ContainerScope.PlaySoundWrapper(
    effect: PlaySoundConsumeEffect,
    modifier: Modifier = Modifier,
    onValueChange: (ConsumeEffect, Boolean) -> Unit
) {
    val sound = effect.sound.asMutableState
    sound.onSetValue = {
        onValueChange(PlaySoundConsumeEffect(it), false)
        it
    }
    HolderSoundEventSelector(sound, modifier = modifier)
}
//endregion