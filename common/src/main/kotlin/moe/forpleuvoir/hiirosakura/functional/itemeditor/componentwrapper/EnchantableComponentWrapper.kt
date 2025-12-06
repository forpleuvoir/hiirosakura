package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.IntSlider
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.enchantment.Enchantable
import kotlin.time.Duration.Companion.milliseconds

fun ContainerScope.EnchantableComponentWrapper(
    key: ResourceLocation,
    component: Enchantable,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    defaultEditor: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Enchantable, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    val valueState = component.value().asMutableState
    valueState.subscribe {
        component = Enchantable(it)
        onValueChange(component, false)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        val state = mutableStateOf(defaultEditor)
        SwitchableProxy(
            {
                IntSlider(valueState, 1..Int.MAX_VALUE, modifier = Modifier.width(115f).hoverTip {
                    Preview(valueState::getValue)
                })
            },
            {
                IntEditor(valueState, 1..Int.MAX_VALUE, modifier = Modifier.width(115f).hoverTip {
                    Preview(valueState::getValue)
                }, editorModifier = { Modifier.weight(1) })
            },
            state
        )
        Button {
            click { state.switch() }
            Icon(IconTextures.SWITCH)
        }
    }
}


private fun ContainerScope.Preview(
    component: () -> Int,
    textMapper: (Int) -> Text = { Literal(it.toString()) },
    modifier: Modifier = Modifier,
) = Text(mutableStateBy { textMapper(component()) }, modifier = modifier, TextSetting(textLabelUpdateInterval = 50.milliseconds))