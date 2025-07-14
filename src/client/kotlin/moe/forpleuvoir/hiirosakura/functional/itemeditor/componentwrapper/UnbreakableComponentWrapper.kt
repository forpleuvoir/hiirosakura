package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import net.minecraft.component.type.UnbreakableComponent
import net.minecraft.util.Identifier


fun ContainerScope.UnbreakableComponentWrapper(
    id: Identifier,
    component: UnbreakableComponent,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (UnbreakableComponent, Boolean) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val state = mutableStateOf(component.showInTooltip)
    state.subscribe {
        onValueChange(UnbreakableComponent(state.getValue()), true)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        TextLabel("show_in_tooltip")
        SwitchButton(state)
    }
}
