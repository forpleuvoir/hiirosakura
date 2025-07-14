package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import net.minecraft.component.type.NbtComponent
import net.minecraft.util.Identifier

fun ContainerScope.NbtCompoundComponentWrapper(
    id: Identifier,
    component: NbtComponent,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (NbtComponent) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {


}