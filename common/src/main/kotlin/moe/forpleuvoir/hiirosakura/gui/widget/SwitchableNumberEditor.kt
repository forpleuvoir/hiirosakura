package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.DoubleEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.FloatEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.LongEditor
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch

enum class SwitchableNumberEditorType(val value: Boolean) {
    Slider(true),
    Editor(false)
}

fun ContainerScope.SwitchableIntEditor(
    valueState: MutableState<Int>,
    valueRange: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
    width: Float,
    defaultEditor: SwitchableNumberEditorType = if (valueRange.endInclusive - valueRange.start > 1000) SwitchableNumberEditorType.Slider else SwitchableNumberEditorType.Editor,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textMapper: (Int) -> Text = { Literal(it.toString()) },
) = Row(modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment) {
    val state = mutableStateOf(defaultEditor.value)
    SwitchableProxy(
        {
            IntSlider(valueState, valueRange, textMapper = textMapper, modifier = Modifier.width(width))
        },
        {
            IntEditor(valueState, valueRange, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
        },
        state
    )
    Button {
        click { state.switch() }
        Icon(IconTextures.SWITCH)
    }
}

fun ContainerScope.SwitchableLongEditor(
    valueState: MutableState<Long>,
    valueRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    width: Float,
    defaultEditor: SwitchableNumberEditorType = if (valueRange.endInclusive - valueRange.start > 1000) SwitchableNumberEditorType.Slider else SwitchableNumberEditorType.Editor,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textMapper: (Long) -> Text = { Literal(it.toString()) },
) = Row(modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment) {
    val state = mutableStateOf(defaultEditor.value)
    SwitchableProxy(
        {
            LongSlider(valueState, valueRange, textMapper = textMapper, modifier = Modifier.width(width))
        },
        {
            LongEditor(valueState, valueRange, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
        },
        state
    )
    Button {
        click { state.switch() }
        Icon(IconTextures.SWITCH)
    }
}


fun ContainerScope.SwitchableFloatEditor(
    valueState: MutableState<Float>,
    valueRange: ClosedFloatingPointRange<Float> = Float.MIN_VALUE..Float.MAX_VALUE,
    width: Float,
    defaultEditor: SwitchableNumberEditorType = if (valueRange.endInclusive - valueRange.start > 1000) SwitchableNumberEditorType.Slider else SwitchableNumberEditorType.Editor,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textMapper: (Float) -> Text = { Literal("%.2f".format(it)) },
) = Row(modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment) {
    val state = mutableStateOf(defaultEditor.value)
    SwitchableProxy(
        {
            FloatSlider(valueState, valueRange, textMapper = textMapper, modifier = Modifier.width(width))
        },
        {
            FloatEditor(valueState, valueRange, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
        },
        state
    )
    Button {
        click { state.switch() }
        Icon(IconTextures.SWITCH)
    }
}

fun ContainerScope.SwitchableDoubleEditor(
    valueState: MutableState<Double>,
    valueRange: ClosedFloatingPointRange<Double> = Double.MIN_VALUE..Double.MAX_VALUE,
    width: Float,
    defaultEditor: SwitchableNumberEditorType = if (valueRange.endInclusive - valueRange.start > 1000) SwitchableNumberEditorType.Slider else SwitchableNumberEditorType.Editor,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textMapper: (Double) -> Text = { Literal("%.2f".format(it)) },
) = Row(modifier, horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment) {
    val state = mutableStateOf(defaultEditor.value)
    SwitchableProxy(
        {
            DoubleSlider(valueState, valueRange, textMapper = textMapper, modifier = Modifier.width(width))
        },
        {
            DoubleEditor(valueState, valueRange, modifier = Modifier.width(width), editorModifier = { Modifier.weight(1) })
        },
        state
    )
    Button {
        click { state.switch() }
        Icon(IconTextures.SWITCH)
    }
}