package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.HolderSoundEventSelector
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.Consumable

//region Wrapper
fun ContainerScope.ConsumableComponentWrapper(
    key: ResourceLocation,
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
            ConsumableEditor(key.asTranslateText(), component, onValueChange = onValueChange)
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
        Column {
            Row {
                Row {
                    Text("consume_seconds")
                    FloatEditor(consumeSeconds, 0f..Float.MAX_VALUE, editorModifier = { Modifier.width(60f) })
                }
                Row {
                    Text("animation")
                    EnumSelector(animation)
                }
            }
            Row {
                Row {
                    Text("sound")
                    HolderSoundEventSelector(sound)
                }
                Row {
                    Text("has_consume_particles")
                    SwitchButton(hasConsumeParticles)
                }
            }
        }
        DialogContent {

        }
    }
}
//endregion
