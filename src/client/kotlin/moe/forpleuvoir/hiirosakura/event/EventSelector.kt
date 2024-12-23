package moe.forpleuvoir.hiirosakura.event

import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenu
import moe.forpleuvoir.nebula.event.Event

fun WidgetContainerScope.EventSelector(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onSelected: (Event) -> Unit,
    onClosed: () -> Unit = {},
) {

}