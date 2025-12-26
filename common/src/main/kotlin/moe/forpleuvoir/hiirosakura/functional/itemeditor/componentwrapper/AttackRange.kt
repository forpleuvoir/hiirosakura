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
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.AttackRange

fun ContainerScope.AttackRangeComponentWrapper(
    key: Identifier,
    component: AttackRange,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (AttackRange, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    Button(
        Modifier.width(140f)
            .hoverTip {
                Row {
                    Column(Modifier.margin(right = 10f), horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
                        Text("min_reach")
                        Text("max_reach")
                        Text("min_creative_reach")
                        Text("max_creative_reach")
                        Text("hitbox_margin")
                        Text("mob_factor")
                    }
                    Column(horizontalAlignment = Alignment.Left, verticalArrangement = Arrangement.spacedBy(2f)) {
                        Text(component.minRange.toString())
                        Text(component.maxRange.toString())
                        Text(component.minCreativeRange.toString())
                        Text(component.maxCreativeRange.toString())
                        Text(component.hitboxMargin.toString())
                        Text(component.mobFactor.toString())
                    }
                }
            }
    ) {
        Text(IGLang.edit)
        click {
            AttackRangeEditor(key.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

fun AttackRangeEditor(
    title: Text,
    attackRange: AttackRange,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (AttackRange, Boolean) -> Unit,
): IGScreenImpl {
    val minRange = attackRange.minRange.asMutableState
    val maxRange = attackRange.maxRange.asMutableState
    val minCreativeRange = attackRange.minCreativeRange.asMutableState
    val maxCreativeRange = attackRange.maxCreativeRange.asMutableState
    val hitboxMargin = attackRange.hitboxMargin.asMutableState
    val mobFactor = attackRange.mobFactor.asMutableState

    return DataComponentEditor(
        title,
        {
            AttackRange(
                minRange.getValue(),
                maxRange.getValue(),
                minCreativeRange.getValue(),
                maxCreativeRange.getValue(),
                hitboxMargin.getValue(),
                mobFactor.getValue(),
            ) to true
        },
        onValueChange,
        modifier,
        screenModifier
    ) {
        DialogContent {
            Column(Modifier.width(220f), verticalArrangement = Arrangement.spacedBy(5f)) {
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("min_reach")
                    SwitchableFloatEditor(minRange, 0f..64f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("max_reach")
                    SwitchableFloatEditor(maxRange, 0f..64f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("min_creative_reach")
                    SwitchableFloatEditor(minCreativeRange, 0f..64f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("max_creative_reach")
                    SwitchableFloatEditor(maxCreativeRange, 0f..64f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("hitbox_margin")
                    SwitchableFloatEditor(hitboxMargin, 0f..1f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
                Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("mob_factor")
                    SwitchableFloatEditor(mobFactor, 0f..2f, 80f, defaultEditor = SwitchableNumberEditorType.Slider)
                }
            }
        }
    }
}