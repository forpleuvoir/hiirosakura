package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import net.minecraft.util.Identifier

fun ContainerScope.UnitComponentWrapper(
    id: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    TextLabel(Literal("Unit"))
}