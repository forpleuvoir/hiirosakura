package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.IGButtonWidget
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor

fun WidgetContainerScope.RemoveButton(
    action: (IGButtonWidget) -> Unit,
) = FlatButton(
    hoveredColor = Colors.RED.alpha(0.25f),
    modifier = Modifier.hoverText(IGLang.remove).size(14f, 14f)
) {
    click(action)
    Icon(IconTextures.DELETE, HSVColor(0f, .1f, .25f), modifier = Modifier.size(12f, 12f))
}

fun WidgetContainerScope.AddButton(
    action: (IGButtonWidget) -> Unit,
) = FlatButton(
    hoveredColor = Colors.LIME.alpha(0.25f),
    modifier = Modifier.hoverText(IGLang.add).size(14f, 14f)
) {
    click(action)
    Icon(IconTextures.PLUS, HSVColor(120f, 1f, .65f), modifier = Modifier.size(9f, 9f))
}

fun WidgetContainerScope.EditButton(
    action: (IGButtonWidget) -> Unit,
) = FlatButton(
    hoveredColor = Colors.LIME.alpha(0.25f),
    modifier = Modifier.hoverText(IGLang.edit).size(14f, 14f)
) {
    click(action)
    Icon(IconTextures.EDIT, modifier = Modifier.size(12f, 12f))
}