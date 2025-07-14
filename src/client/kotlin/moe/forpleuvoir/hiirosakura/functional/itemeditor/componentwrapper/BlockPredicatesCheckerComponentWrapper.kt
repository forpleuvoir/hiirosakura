package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import net.minecraft.item.BlockPredicatesChecker
import net.minecraft.util.Identifier

fun ContainerScope.BlockPredicatesCheckerWrapper(
    id: Identifier,
    component: BlockPredicatesChecker,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (BlockPredicatesChecker) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {



}