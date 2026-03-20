package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.Widget
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.FireworkExplosion

//region Wrapper
fun ContainerScope.FireworkExplosionComponentWrapper(
    key: Identifier,
    component: FireworkExplosion,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (FireworkExplosion, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.hoverText(IGLang.edit).width(140f),
    ) {
        Text(IGLang.edit)
        click {
            FireworksEditor(
                key.asTranslateText(),
                component,
                modifier = Modifier.width(320f),
                screenModifier = Modifier.padding(20f),
                onValueChange = onValueChange
            ).open()
        }
    }
}
//endregion

//region Editor
fun FireworksEditor(
    title: Text,
    component: FireworkExplosion,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (FireworkExplosion, Boolean) -> Unit,
): IGScreenImpl {

    val shape = component.shape.asMutableState
    var colors = component.colors
    var fadeColors = component.fadeColors
    val hasTrail = component.hasTrail.asMutableState
    val hasTwinkle = component.hasTwinkle.asMutableState

    return DataComponentEditor(
        title,
        {
            FireworkExplosion(
                shape.getValue(),
                colors,
                fadeColors,
                hasTrail.getValue(),
                hasTwinkle.getValue()
            ) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        //普通属性
        Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
            Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("shape", modifier = Modifier.priority(-1))
                FireworkExplosionShapeSelector(shape)
            }
            Widget(Modifier.weight(1))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10f)) {
            Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("has_trail", modifier = Modifier.priority(-1))
                SwitchButton(hasTrail)
            }
            Row(Modifier.weight(1), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("has_twinkle", modifier = Modifier.priority(-1))
                SwitchButton(hasTwinkle)
            }
        }
        ColumnListWrapped(Modifier.fill().weight(1), spacing = 4f, listModifier = { Modifier.weight(1).fill() }) {
            IntColorListEntryWrapper(ArrayList(colors), Literal("colors"), modifier = Modifier.unlockConstraint()) {
                colors = it
            }
            IntColorListEntryWrapper(ArrayList(fadeColors), Literal("fade_colors"),modifier= Modifier.unlockConstraint()) {
                fadeColors = it
            }
        }
    }
}
//endregion