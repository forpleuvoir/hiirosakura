package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxHeight
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.maxWidth
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextArea
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.MutableState

fun ContainerScope.ExpandableTextEditor(
    text: MutableState<String>,
    title: Text,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textEditorModifier: RowScope.() -> Modifier = { Modifier },
    dialogModifier: Modifier = Modifier
) = Row(modifier, horizontalArrangement, verticalAlignment) {
    TextEditor(textEditorModifier()) { bindState(text) }
    EditButton {
        Dialog(Modifier.maxWidth(330f).maxHeight(185f).then(dialogModifier)) {
            TextLabel(title)
            TextArea(Modifier.fill().weight(1)) {
                bindState(text)
            }
        }.open()
    }
}