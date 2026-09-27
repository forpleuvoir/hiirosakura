package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
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
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.hiirosakura.ui.compat.NumberFieldStyle
import moe.forpleuvoir.hiirosakura.ui.compat.LocalNumberFieldStyle

/**
 * 文本 / 数值编辑器的统一宽度，可通过 CompositionLocal 覆盖。
 */
val LocalSerializeValueFieldWidth = compositionLocalOf { 240.dp }

private val ValueFieldHeight = 48.dp

private val ValueFieldContentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

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
        else -> UnsupportedEditor(modifier)
    }
}

@Composable
private fun StringEditor(
    str: String,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(str) { mutableStateOf(str) }
    CompactValueField(
        value = value,
        label = "String",
        onValueChange = { v ->
            value = v
            onValueChange(SerializePrimitive(v))
        },
        modifier = modifier.width(LocalSerializeValueFieldWidth.current),
    )
}

@Composable
private fun BooleanEditor(
    boolean: Boolean,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var checked by remember(boolean) { mutableStateOf(boolean) }
    Switch(
        checked = checked,
        onCheckedChange = { c ->
            checked = c
            onValueChange(SerializePrimitive(c))
        },
        modifier = modifier,
    )
}

@Composable
private fun NumberEditor(
    number: Number,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    when (number) {
        is Int -> IntEditor(number, onValueChange, modifier)
        is Long -> LongEditor(number, onValueChange, modifier)
        is Short -> ShortEditor(number, onValueChange, modifier)
        is Byte -> ByteEditor(number, onValueChange, modifier)
        is Float -> FloatEditor(number, onValueChange, modifier)
        is Double -> DoubleEditor(number, onValueChange, modifier)
        is BigInteger -> BigIntEditor(number, onValueChange, modifier)
        is BigDecimal -> BigDecimalEditor(number, onValueChange, modifier)
        else -> UnsupportedEditor(modifier)
    }
}

@Composable
private fun IntEditor(
    int: Int,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                LabelBox(label = { Text("Int") }, modifier = modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight)) {
            IntField(
                value = int,
                onValueChange = { onValueChange(SerializePrimitive(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }
    }
}

@Composable
private fun LongEditor(
    long: Long,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                LabelBox(label = { Text("Long") }, modifier = modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight)) {
            LongField(
                value = long,
                onValueChange = { onValueChange(SerializePrimitive(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }
    }
}

@Composable
private fun ShortEditor(
    short: Short,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(short) { mutableStateOf(short.toString()) }
    CompactValueField(
        value = value,
        label = "Short",
        onValueChange = { v ->
            value = v
            v.toShortOrNull()?.let { onValueChange(SerializePrimitive(it)) }
        },
        isError = value.isNotEmpty() && value.toShortOrNull() == null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        modifier = modifier.width(LocalSerializeValueFieldWidth.current),
    )
}

@Composable
private fun ByteEditor(
    byte: Byte,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(byte) { mutableStateOf(byte.toString()) }
    CompactValueField(
        value = value,
        label = "Byte",
        onValueChange = { v ->
            value = v
            v.toByteOrNull()?.let { onValueChange(SerializePrimitive(it)) }
        },
        isError = value.isNotEmpty() && value.toByteOrNull() == null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        modifier = modifier.width(LocalSerializeValueFieldWidth.current),
    )
}

@Composable
private fun FloatEditor(
    float: Float,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                LabelBox(label = { Text("Float") }, modifier = modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight)) {
            FloatField(
                value = float,
                onValueChange = { onValueChange(SerializePrimitive(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }
    }
}

@Composable
private fun DoubleEditor(
    double: Double,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                LabelBox(label = { Text("Double") }, modifier = modifier
                .width(LocalSerializeValueFieldWidth.current)
                .height(ValueFieldHeight)) {
            DoubleField(
                value = double,
                onValueChange = { onValueChange(SerializePrimitive(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
        }
    }
}

@Composable
private fun BigIntEditor(
    bigInt: BigInteger,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(bigInt) { mutableStateOf(bigInt.toString()) }
    CompactValueField(
        value = value,
        label = "BigInt",
        onValueChange = { v ->
            value = v
            runCatching { BigInteger(v) }.getOrNull()?.let { onValueChange(SerializePrimitive(it)) }
        },
        isError = value.isNotEmpty() && runCatching { BigInteger(value) }.isFailure,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        modifier = modifier.width(LocalSerializeValueFieldWidth.current),
    )
}

@Composable
private fun BigDecimalEditor(
    bigDecimal: BigDecimal,
    onValueChange: (SerializePrimitive) -> Unit,
    modifier: Modifier,
) {
    var value by remember(bigDecimal) { mutableStateOf(bigDecimal.toString()) }
    CompactValueField(
        value = value,
        label = "BigDecimal",
        onValueChange = { v ->
            value = v
            runCatching { BigDecimal(v) }.getOrNull()?.let { onValueChange(SerializePrimitive(it)) }
        },
        isError = value.isNotEmpty() && runCatching { BigDecimal(value) }.isFailure,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        modifier = modifier.width(LocalSerializeValueFieldWidth.current),
    )
}

@Composable
private fun CompactValueField(
    value: String,
    label: String,
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
    LabelBox(label = { Text(label) }) {
        TextField(
            state = state,
            modifier = modifier.height(ValueFieldHeight),
            isError = isError,
            lineLimits = TextFieldLineLimits.SingleLine,
            contentPadding = ValueFieldContentPadding,
            keyboardOptions = keyboardOptions,
        )
    }
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