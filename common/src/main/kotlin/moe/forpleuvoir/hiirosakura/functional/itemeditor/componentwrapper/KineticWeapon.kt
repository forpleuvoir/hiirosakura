package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.AddButton
import moe.forpleuvoir.hiirosakura.gui.widget.HolderSoundEventSelector
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
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
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.hoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnWidget
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.configContainerWrapperGuidelinesColor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.component.KineticWeapon
import java.util.*
import kotlin.jvm.optionals.getOrNull

//region Wrapper
fun ContainerScope.KineticWeaponComponentWrapper(
    key: Identifier,
    component: KineticWeapon,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (KineticWeapon, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit).width(140f),
    ) {
        Text(IGLang.edit)
        click {
            KineticWeaponEditor(
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
fun KineticWeaponEditor(
    title: Text,
    component: KineticWeapon,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (KineticWeapon, Boolean) -> Unit,
): IGScreenImpl {

    val contactCooldownTicks = component.contactCooldownTicks.asMutableState
    val delayTicks = component.delayTicks.asMutableState
    val dismountConditions = component.dismountConditions.getOrNull().asMutableState
    val knockbackConditions = component.knockbackConditions.getOrNull().asMutableState
    val damageConditions = component.damageConditions.getOrNull().asMutableState
    val forwardMovement = component.forwardMovement.asMutableState
    val damageMultiplier = component.damageMultiplier.asMutableState
    var sound = component.sound.getOrNull()
    var hitSound = component.hitSound.getOrNull()

    return DataComponentEditor(
        title,
        {
            KineticWeapon(
                contactCooldownTicks.getValue(),
                delayTicks.getValue(),
                Optional.ofNullable(dismountConditions.getValue()),
                Optional.ofNullable(knockbackConditions.getValue()),
                Optional.ofNullable(damageConditions.getValue()),
                forwardMovement.getValue(),
                damageMultiplier.getValue(),
                Optional.ofNullable(sound),
                Optional.ofNullable(hitSound),
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        //普通属性
        val width = 90f
        Column(Modifier.fill(), verticalArrangement = Arrangement.spacedBy(2f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("contact_cooldown_ticks", modifier = Modifier.priority(-1))
                    IntEditor(contactCooldownTicks, 0..Int.MAX_VALUE, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
                }
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("delay_ticks", modifier = Modifier.priority(-1))
                    IntEditor(delayTicks, 0..Int.MAX_VALUE, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("forward_movement", modifier = Modifier.priority(-1))
                    FloatEditor(forwardMovement, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
                }
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("damage_multiplier", modifier = Modifier.priority(-1))
                    FloatEditor(damageMultiplier, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("sound", modifier = Modifier.priority(-1))
                    Row(Modifier.width(width), horizontalArrangement = Arrangement.spacedBy(2f)) r@{
                        sound?.let { hs ->
                            val state = hs.asMutableState
                            state.onSetValue = {
                                sound = it
                                this@r.executeRecompose()
                                it
                            }
                            HolderSoundEventSelector(state, modifier = Modifier.weight(1).priority(-1))
                        } ?: run {
                            Widget(Modifier.weight(1).height(18f))
                            AddButton {
                                sound = SoundEvents.SPEAR_USE
                                this@r.executeRecompose()
                            }
                        }
                        RemoveButton {
                            sound = null
                            this@r.executeRecompose()
                        }
                    }
                }
                Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("hit_sound", modifier = Modifier.priority(-1))
                    Row(Modifier.width(width), horizontalArrangement = Arrangement.spacedBy(2f)) r@{
                        hitSound?.let { hs ->
                            val state = hs.asMutableState
                            state.onSetValue = {
                                hitSound = it
                                this@r.executeRecompose()
                                it
                            }
                            HolderSoundEventSelector(state, modifier = Modifier.weight(1).priority(-1))
                        } ?: run {
                            Widget(Modifier.weight(1).height(18f))
                            AddButton {
                                hitSound = SoundEvents.SPEAR_USE
                                this@r.executeRecompose()
                            }
                        }
                        RemoveButton {
                            hitSound = null
                            this@r.executeRecompose()
                        }
                    }
                }
            }
        }
        ColumnListWrapped(Modifier.height(250f).fill(), spacing = 2f, listModifier = { Modifier.weight(1).fill() }) {
            KineticWeaponConditionWrapper(dismountConditions, Literal("dismount_conditions"), Modifier.fill())
            KineticWeaponConditionWrapper(knockbackConditions, Literal("knockback_conditions"), Modifier.fill())
            KineticWeaponConditionWrapper(damageConditions, Literal("damage_conditions"), Modifier.fill())
        }
    }
}
//endregion

//region KineticWeaponConditionWrapper
fun ContainerScope.KineticWeaponConditionWrapper(
    condition: MutableState<KineticWeapon.Condition?>,
    title: Text,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
): ColumnWidget = Column(modifier.attachLeft { padding(2f) }, verticalArrangement, horizontalAlignment) {
    //KineticWeapon.Condition是否为空
    var hasCondition = condition.getValue() != null
    val expanded = mutableStateOf(hasCondition)
    var icon = expanded.getValue().either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
    expanded.subscribe {
        icon = it.either(WidgetTextures.DROP_DOWN_MENU_ARROW_UP, WidgetTextures.DROP_DOWN_MENU_ARROW_DOWN)
    }
    var childrenBox by lateInitValueOf { Box.Unspecified }

    Button(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.bgHoverHighlightBox()
            .fill()
            .padding(2f)
            .hoverHighlightBox(
                boxSupplier = { childrenBox() },
                colorRange = Colors.CYAN.alpha(0f) to Colors.CYAN.alpha(0.15f),
            )
    ) {
        Text(title)
        Row(horizontalArrangement = Arrangement.spacedBy(2f)) {
            if (hasCondition) {
                Icon(icon, modifier = Modifier.padding(vertical = 2.5f, horizontal = 2f))
            } else {
                AddButton {
                    condition.setValue(KineticWeapon.Condition(0, 0f, 0f))
                    hasCondition = true
                    this@Button.executeRecompose()
                    expanded.setValue(true)
                }
            }
            DeleteButton(
                confirmMessage = { IGLang.removeConfirm(title) },
                recompose = { this@Button.executeRecompose() }
            ) {
                condition.setValue(null)
                hasCondition = false
                expanded.setValue(false)
            }
        }
        click {
            if (hasCondition) {
                expanded.switch()
                this@Button.executeRecompose()
            }
        }
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
                    //region 属性声明
                    val maxDurationTicks = (condition.getValue()?.maxDurationTicks ?: 0).asMutableState
                    maxDurationTicks.subscribe {
                        condition.setValue(
                            KineticWeapon.Condition(
                                it,
                                condition.getValue()?.minSpeed ?: 0f,
                                condition.getValue()?.minRelativeSpeed ?: 0f,
                            )
                        )
                    }
                    val minSpeed = (condition.getValue()?.minSpeed ?: 0f).asMutableState
                    minSpeed.subscribe {
                        condition.setValue(
                            KineticWeapon.Condition(
                                condition.getValue()?.maxDurationTicks ?: 0,
                                it,
                                condition.getValue()?.minRelativeSpeed ?: 0f,
                            )
                        )
                    }
                    val minRelativeSpeed = (condition.getValue()?.minRelativeSpeed ?: 0f).asMutableState
                    minRelativeSpeed.subscribe {
                        condition.setValue(
                            KineticWeapon.Condition(
                                condition.getValue()?.maxDurationTicks ?: 0,
                                condition.getValue()?.minSpeed ?: 0f,
                                it,
                            )
                        )
                    }
                    //endregion
                    //maxDurationTicks
                    Row(
                        modifier = Modifier.padding(2f).fill().bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("max_duration_ticks", modifier = Modifier.priority(-1))
                        IntEditor(maxDurationTicks, range = 0..Int.MAX_VALUE, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
                    }
                    //minSpeed
                    Row(
                        modifier = Modifier.padding(2f).fill().bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("min_speed", modifier = Modifier.priority(-1))
                        FloatEditor(minSpeed, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
                    }
                    Row(
                        modifier = Modifier.padding(2f).fill().bgHoverHighlightBox(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("min_relative_speed", modifier = Modifier.priority(-1))
                        FloatEditor(minRelativeSpeed, modifier = Modifier.width(160f), editorModifier = { Modifier.weight(1) })
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