package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableNumberEditorType
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.FloatSlider
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import net.minecraft.resources.Identifier
import kotlin.time.Duration.Companion.milliseconds

private const val WIDTH = 115f

fun ContainerScope.FloatComponentWrapper(
    key: Identifier,
    component: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    textMapper: (Float) -> MutableText = { Literal("%.2f".format(it)) },
    defaultEditor: SwitchableNumberEditorType = SwitchableNumberEditorType.fromRange(valueRange),
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Float, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    val valueState = component.asMutableState
    valueState.subscribe { onValueChange(it, false) }
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        val state = mutableStateOf(defaultEditor.value)
        SwitchableProxy(
            {
                FloatSlider(valueState, valueRange, textMapper = textMapper, modifier = Modifier.width(WIDTH).hoverTip {
                    FloatComponentPreview(valueState::getValue, textMapper)
                })
            },
            {
                FloatEditor(valueState, valueRange, modifier = Modifier.width(WIDTH).hoverTip {
                    FloatComponentPreview(valueState::getValue, textMapper)
                }, editorModifier = { Modifier.weight(1) })
            },
            state
        )
        Button {
            click { state.switch() }
            Icon(IconTextures.SWITCH)
        }
    }
}


private fun ContainerScope.FloatComponentPreview(
    component: () -> Float,
    textMapper: (Float) -> Text = { Literal("%.2f".format(it)) },
    modifier: Modifier = Modifier,
) = Text(mutableStateBy { textMapper(component()) }, modifier = modifier, TextSetting(textLabelUpdateInterval = 50.milliseconds))