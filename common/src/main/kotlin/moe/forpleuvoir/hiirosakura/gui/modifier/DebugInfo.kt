package moe.forpleuvoir.hiirosakura.gui.modifier

import moe.forpleuvoir.ibukigourd.gui.base.element.GuiElementUserData.name
import moe.forpleuvoir.ibukigourd.gui.modifier.DebugInfoScope
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.style
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors

fun DebugInfoScope.HoveredWidget(format: String = "HoveredWidget:%s", color: ARGBColor = Colors.LIME) = info { screen, _, _, _, _ ->
    Literal(format.format(screen.hoveredWidget()?.name ?: "null")).style { color(color) }

}