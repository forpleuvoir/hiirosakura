package moe.forpleuvoir.hiirosakura.gui.widget.serializereditor

import moe.forpleuvoir.hiirosakura.gui.widget.ExpandableTextEditor
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidgetImpl
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.button.IGButtonWidget
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowWidget
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import java.math.BigDecimal
import java.math.BigInteger


fun ContainerScope.SerializePrimitiveEntryEditor(
    key: String,
    serializePrimitive: SerializePrimitive,
    modifier: Modifier = Modifier,
    editorModifier: RowScope.() -> Modifier = { Modifier },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    keyWrapper: RowScope.(String) -> Unit = { TextLabel(it) },
    onValueChange: (SerializePrimitive) -> Unit,
    content: RowScope.() -> Unit
) = ElementEntry(key, Modifier.bgHoverHighlightBox().then(modifier), horizontalArrangement, verticalAlignment, keyWrapper) {
    Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
        SerializePrimitiveEditor(serializePrimitive, editorModifier(), onValueChange)
        content()
    }
}

fun ContainerScope.SerializePrimitiveEditor(
    serializePrimitive: SerializePrimitive,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit
): IGWidgetImpl = when {
    serializePrimitive.isString  -> StringEditor(serializePrimitive.asString, modifier, onValueChange)
    serializePrimitive.isNumber  -> NumberEditor(serializePrimitive.asNumber, modifier, onValueChange)
    serializePrimitive.isBoolean -> BooleanEditor(serializePrimitive.asBoolean, modifier, onValueChange)
    else                         -> Row(modifier) { TextLabel("Unsupported Type") }
}

private fun ContainerScope.StringEditor(
    str: String,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = str.asMutableState
    value.subscribe { onValueChange(SerializePrimitive(it)) }
    return ExpandableTextEditor(
        value,
        Literal("String"),
        modifier,
        horizontalArrangement = Arrangement.spacedBy(5f),
        textEditorModifier = { Modifier.width(101f) })
}

private fun ContainerScope.BooleanEditor(
    boolean: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): IGButtonWidget {
    val value = boolean.asMutableState
    value.subscribe { onValueChange(SerializePrimitive(it)) }
    return SwitchButton(value, modifier)
}

private fun ContainerScope.NumberEditor(
    number: Number,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): IGWidgetImpl = when (number) {
    is Int        -> IntEditor(number, modifier, onValueChange)
    is Long       -> LongEditor(number, modifier, onValueChange)
    is Short      -> ShortEditor(number, modifier, onValueChange)
    is Byte       -> BytetEditor(number, modifier, onValueChange)
    is Float      -> FloatEditor(number, modifier, onValueChange)
    is Double     -> DoubleEditor(number, modifier, onValueChange)
    is BigInteger -> BigIntEditor(number, modifier, onValueChange)
    is BigDecimal -> BigDecimalEditor(number, modifier, onValueChange)
    else          -> Row(modifier) { TextLabel("Unsupported Number") }
}

private const val number_editor_width = 120f

private fun ContainerScope.BigIntEditor(
    bigInt: BigInteger,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = (bigInt.toLong()).asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(BigInteger.valueOf(it)))
    }
    return LongEditor(
        value,
        Long.MIN_VALUE..Long.MAX_VALUE,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.IntEditor(
    int: Int,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = int.asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return IntEditor(
        value,
        Int.MIN_VALUE..Int.MAX_VALUE,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.LongEditor(
    long: Long,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = long.asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return LongEditor(
        value,
        Long.MIN_VALUE..Long.MAX_VALUE,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.ShortEditor(
    short: Short,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = (short.toInt()).asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return IntEditor(
        value,
        Short.MIN_VALUE..Short.MAX_VALUE,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.BytetEditor(
    byte: Byte,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = (byte.toInt()).asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return IntEditor(
        value,
        Byte.MIN_VALUE..Byte.MAX_VALUE,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}


private fun ContainerScope.FloatEditor(
    float: Float,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = float.asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return FloatEditor(
        value,
        Float.NEGATIVE_INFINITY..Float.POSITIVE_INFINITY,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.DoubleEditor(
    double: Double,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = double.asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(it))
    }
    return DoubleEditor(
        value,
        Double.NEGATIVE_INFINITY..Double.POSITIVE_INFINITY,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}

private fun ContainerScope.BigDecimalEditor(
    bigDecimal: BigDecimal,
    modifier: Modifier = Modifier,
    onValueChange: (SerializePrimitive) -> Unit,
): RowWidget {
    val value = (bigDecimal.toDouble()).asMutableState
    value.subscribe {
        onValueChange(SerializePrimitive(BigDecimal.valueOf(it)))
    }
    return DoubleEditor(
        value,
        Double.NEGATIVE_INFINITY..Double.POSITIVE_INFINITY,
        modifier = Modifier.width(number_editor_width).then(modifier),
        editorModifier = { Modifier.weight(1) }
    )
}