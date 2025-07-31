package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minHeight
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextWidgetScope
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.style.style
import moe.forpleuvoir.ibukigourd.util.state.asState
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.text.Style
import net.minecraft.util.Identifier


fun ContainerScope.IdentifierText(
    id: Identifier,
    style: Style = Style.EMPTY,
    modifier: Modifier = Modifier,
    setting: TextSetting = TextSetting(),
    scope: TextWidgetScope.() -> Unit = {}
) = TextLabel(id.asTranslateText().setStyle(style), modifier, setting, scope)


fun <C : Any> DataComponentWrapperDialog(
    title: Text,
    newComponent: () -> Pair<C, Boolean>,
    onValueChange: (C, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onConfirm: () -> Boolean = { true },
    content: ColumnScope.() -> Unit
) = ConfirmDialog(
    title.asState,
    modifier,
    screenModifier,
    onConfirm = {
        if (onConfirm()) {
            val (newComponent, recompose) = newComponent()
            onValueChange(newComponent, recompose)
            closeScreen()
        }
    }
) {
    content()
}

fun ContainerScope.DataComponentWrapperRow(
    id: Identifier,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: RowScope.() -> Unit
) = Row(Modifier.bgHoverHighlightBox().padding(horizontal = 2f).minHeight(19f).then(modifier), horizontalArrangement = Arrangement.SpaceBetween) {
    val style = DataComponentWrappers.isAdaptedComponent(id).pick(
        style(HSVColor(195f, 1f, 1f)),
        style(HSVColor(5f, .6f, 1f))
    )
    IdentifierText(id, style, Modifier.weight(1))
    Row(Modifier, horizontalArrangement, verticalAlignment) {
        content()
        DeleteButton(
            { HSLang.deleteConfirm(id) },
            { }
        ) {
            removeAction()
            this@DataComponentWrapperRow.executeRecompose()
        }
    }
}