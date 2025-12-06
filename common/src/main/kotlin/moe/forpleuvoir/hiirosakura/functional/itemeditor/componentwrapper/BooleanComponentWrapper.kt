package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import net.minecraft.resources.ResourceLocation

fun ContainerScope.BooleanComponentWrapper(
    id: ResourceLocation,
    component: Boolean,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Boolean, Boolean) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val valueState = component.asMutableState
    valueState.subscribe { onValueChange(it, false) }
    SwitchButton(valueState)
}