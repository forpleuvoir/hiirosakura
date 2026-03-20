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
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.component.PiercingWeapon
import java.util.*
import kotlin.jvm.optionals.getOrNull

//region Wrapper
fun ContainerScope.PiercingWeaponComponentWrapper(
    key: Identifier,
    component: PiercingWeapon,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (PiercingWeapon, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit).width(140f),
    ) {
        Text(IGLang.edit)
        click {
            PiercingWeaponEditor(
                key.asTranslateText(),
                component,
                modifier = Modifier.width(220f),
                screenModifier = Modifier.padding(20f),
                onValueChange = onValueChange
            ).open()
        }
    }
}
//endregion


//region Editor
fun PiercingWeaponEditor(
    title: Text,
    component: PiercingWeapon,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (PiercingWeapon, Boolean) -> Unit,
): IGScreenImpl {

    val dealsKnockback = component.dealsKnockback.asMutableState
    val dismounts = component.dismounts.asMutableState
    var sound = component.sound.getOrNull()
    var hitSound = component.hitSound.getOrNull()

    return DataComponentEditor(
        title,
        {
            PiercingWeapon(
                dealsKnockback.getValue(),
                dismounts.getValue(),
                Optional.ofNullable(sound),
                Optional.ofNullable(hitSound),
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        //普通属性
        val width = 120f
        ColumnListWrapped(Modifier, spacing = 2f) {
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("deals_knockback", modifier = Modifier.priority(-1))
                SwitchButton(dealsKnockback)
            }
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("dismounts", modifier = Modifier.priority(-1))
                SwitchButton(dismounts)
            }

            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
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
            Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
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
}
//endregion