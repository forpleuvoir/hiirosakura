package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import net.minecraft.network.chat.Component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.ui.sokitsu.DoubleField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LongField
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import java.math.BigDecimal
import java.math.BigInteger
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch

/**
 * 鏂囨湰 / 鏁板€肩紪杈戝櫒鐨勭粺涓€瀹藉害锛屽彲閫氳繃 CompositionLocal 瑕嗙洊銆? */
val LocalSerializeValueFieldWidth = compositionLocalOf { 240.dp }

private val ValueFieldHeight = 48.dp

private val ValueFieldContentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

/** 类型标签：主题正文字号。 */
@Composable
private fun TypeLabel(type: String) =
    Text(component = Component.literal(type), fontSize = SokitsuTheme.typography.body.fontSize)

/** 原始类型编辑器：类型标签在左，控件紧随其后。 */
@Composable
private fun PrimitiveField(
    type: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
) {
    TypeLabel(type)
    content()
}

@Composable
fun SerializePrimitiveEditor(
    serializePrimitive: SerializePrimitive,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        serializePrimitive.isString  -> StringEditor(serializePrimitive.asString!!, onValueChange, modifier)
        serializePrimitive.isBoolean -> BooleanEditor(serializePrimitive.asBoolean!!, onValueChange, modifier)
        serializePrimitive.isNumber  -> NumberEditor(serializePrimitive.asNumber!!, onValueChange, modifier)
        else                         -> UnsupportedEditor(modifier)
    }
}

@Composable
private fun StringEditor(
    str: String,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(str) { mutableStateOf(str) }
    PrimitiveField(type = "String", modifier = modifier) {
        CompactValueField(
            value = value,
            onValueChange = { v ->
                value = v
                onValueChange(SerializePrimitive(v))
            },
            modifier = Modifier.width(LocalSerializeValueFieldWidth.current),
        )
    }
}

@Composable
private fun BooleanEditor(
    boolean: Boolean,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var checked by remember(boolean) { mutableStateOf(boolean) }
    PrimitiveField(type = "Boolean", modifier = modifier) {
        Switch(
            checked = checked,
            onCheckedChange = { c ->
                checked = c
                onValueChange(SerializePrimitive(c))
            },
        )
    }
}

@Composable
private fun NumberEditor(
    number: Number,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    when (number) {
        is Int        -> IntEditor(number, onValueChange, modifier)
        is Long       -> LongEditor(number, onValueChange, modifier)
        is Short      -> ShortEditor(number, onValueChange, modifier)
        is Byte       -> ByteEditor(number, onValueChange, modifier)
        is Float      -> FloatEditor(number, onValueChange, modifier)
        is Double     -> DoubleEditor(number, onValueChange, modifier)
        is BigInteger -> BigIntEditor(number, onValueChange, modifier)
        is BigDecimal -> BigDecimalEditor(number, onValueChange, modifier)
        else          -> UnsupportedEditor(modifier)
    }
}

@Composable
private fun IntEditor(
    int: Int,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    PrimitiveField(type = "Int", modifier = modifier) {
        IntField(
            value = int,
            onValueChange = { onValueChange(SerializePrimitive(it)) },
            modifier = Modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight),
        )
    }
}

@Composable
private fun LongEditor(
    long: Long,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    PrimitiveField(type = "Long", modifier = modifier) {
        LongField(
            value = long,
            onValueChange = { onValueChange(SerializePrimitive(it)) },
            modifier = Modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight),
        )
    }
}

@Composable
private fun ShortEditor(
    short: Short,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(short) { mutableStateOf(short.toString()) }
    PrimitiveField(type = "Short", modifier = modifier) {
        CompactValueField(
            value = value,
            onValueChange = { v ->
                value = v
                v.toShortOrNull()?.let { onValueChange(SerializePrimitive(it)) }
            },
            isError = value.isNotEmpty() && value.toShortOrNull() == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.width(LocalSerializeValueFieldWidth.current),
        )
    }
}

@Composable
private fun ByteEditor(
    byte: Byte,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(byte) { mutableStateOf(byte.toString()) }
    PrimitiveField(type = "Byte", modifier = modifier) {
        CompactValueField(
            value = value,
            onValueChange = { v ->
                value = v
                v.toByteOrNull()?.let { onValueChange(SerializePrimitive(it)) }
            },
            isError = value.isNotEmpty() && value.toByteOrNull() == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.width(LocalSerializeValueFieldWidth.current),
        )
    }
}

@Composable
private fun FloatEditor(
    float: Float,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    PrimitiveField(type = "Float", modifier = modifier) {
        FloatField(
            value = float,
            onValueChange = { onValueChange(SerializePrimitive(it)) },
            modifier = Modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight),
        )
    }
}

@Composable
private fun DoubleEditor(
    double: Double,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    PrimitiveField(type = "Double", modifier = modifier) {
        DoubleField(
            value = double,
            onValueChange = { onValueChange(SerializePrimitive(it)) },
            modifier = Modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight),
        )
    }
}

@Composable
private fun BigIntEditor(
    bigInt: BigInteger,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(bigInt) { mutableStateOf(bigInt.toString()) }
    PrimitiveField(type = "BigInteger", modifier = modifier) {
        CompactValueField(
            value = value,
            onValueChange = { v ->
                value = v
                runCatching { BigInteger(v) }.getOrNull()?.let { onValueChange(SerializePrimitive(it)) }
            },
            isError = value.isNotEmpty() && runCatching { BigInteger(value) }.isFailure,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.width(LocalSerializeValueFieldWidth.current),
        )
    }
}

@Composable
private fun BigDecimalEditor(
    bigDecimal: BigDecimal,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(bigDecimal) { mutableStateOf(bigDecimal.toString()) }
    PrimitiveField(type = "BigDecimal", modifier = modifier) {
        CompactValueField(
            value = value,
            onValueChange = { v ->
                value = v
                runCatching { BigDecimal(v) }.getOrNull()?.let { onValueChange(SerializePrimitive(it)) }
            },
            isError = value.isNotEmpty() && runCatching { BigDecimal(value) }.isFailure,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            modifier = Modifier.width(LocalSerializeValueFieldWidth.current),
        )
    }
}

@Composable
private fun CompactValueField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val state = rememberTextFieldState(value)
    val latestOnValueChange = rememberUpdatedState(onValueChange)
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { text ->
            latestOnValueChange.value(text)
        }
    }
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.edit { replace(0, length, value) }
        }
    }
    TextField(
        state = state,
        modifier = modifier.height(ValueFieldHeight),
        isError = isError,
        lineLimits = TextFieldLineLimits.SingleLine,
        contentPadding = ValueFieldContentPadding,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
private fun UnsupportedEditor(modifier: Modifier = Modifier) {
    Text(
        text = "Unsupported Type",
        style = SokitsuTheme.typography.body,
        color = SokitsuTheme.colorScheme.error,
        modifier = modifier,
    )
}