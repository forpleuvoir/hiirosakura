package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableFloatEditor
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableNumberEditorType
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.margin
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.UseEffects

fun ContainerScope.UseEffectsComponentWrapper(
    key: Identifier,
    component: UseEffects,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (UseEffects, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    Button(
        Modifier.width(140f)
            .hoverTip {
                Row {
                    Column(Modifier.margin(right = 10f), horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
                        Text("can_sprint")
                        Text("interact_vibrations")
                        Text("speed_multiplier")
                    }
                    Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
                        Text(component.canSprint.toString())
                        Text(component.interactVibrations.toString())
                        Text(component.speedMultiplier.toString())
                    }
                }
            }
    ) {
        Text(IGLang.edit)
        click {
            UseEffectsEditor(key.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}


fun UseEffectsEditor(
    title: Text,
    useEffects: UseEffects,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (UseEffects, Boolean) -> Unit,
): IGScreenImpl {
    val canSprint = useEffects.canSprint.asMutableState
    val interactVibrations = useEffects.interactVibrations.asMutableState
    val speedMultiplier = useEffects.speedMultiplier.asMutableState

    return DataComponentEditor(
        title,
        { UseEffects(canSprint.getValue(), interactVibrations.getValue(), speedMultiplier.getValue()) to true },
        onValueChange,
        modifier,
        screenModifier
    ) {
        DialogContent {
            Column(Modifier.width(200f), verticalArrangement = Arrangement.spacedBy(5f)) {
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("can_sprint")
                    SwitchButton(canSprint)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("interact_vibrations")
                    SwitchButton(interactVibrations)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("speed_multiplier")
                    SwitchableFloatEditor(speedMultiplier, defaultEditor = SwitchableNumberEditorType.Slider, valueRange = 0f..1f, width = 60f)
                }
            }
        }
    }
}
