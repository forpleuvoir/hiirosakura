package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.ElementContainer
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.plus
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.rememberExpandState
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.rememberMaxLabelWidth
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.replacedAt
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.withoutAt
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Delete
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive

@Composable
fun SerializeArrayEditor(
    serializeArray: SerializeArray,
    onValueChange: (SerializeArray) -> Unit,
) {
    var showAdder by remember { mutableStateOf(false) }
    var addingType by remember { mutableStateOf(SerializeElementType.String) }
    val expanded = rememberExpandState(default = serializeArray.size <= 5)
    val keyWidth = rememberMaxLabelWidth(
        labels = serializeArray.indices.map { "[$it] :" },
        style = keyLabelStyle(),
    )

    ElementContainer(
        title = { Text(HSLang.SerializeEditor.arrayType) },
        count = serializeArray.size,
        expanded = expanded.value,
        onToggle = { expanded.value = !expanded.value },
        onAdd = { showAdder = true },
        emptyText = HSLang.SerializeEditor.emptyArray.plainText,
    ) {
        serializeArray.forEachIndexed { index, element ->
            SerializeArrayEntry(
                index = index,
                value = element,
                keyWidth = keyWidth,
                onValueChange = { newValue ->
                    onValueChange(serializeArray.replacedAt(index, newValue))
                },
                onRemove = {
                    onValueChange(serializeArray.withoutAt(index))
                },
            )
        }
    }

    if (showAdder) {
        SerializeArrayElementAdderDialog(
            selectedType = addingType,
            onTypeChange = { addingType = it },
            onDismiss = { showAdder = false },
            onConfirm = { element ->
                onValueChange(serializeArray.plus(element))
                showAdder = false
            },
        )
    }
}

@Composable
private fun SerializeArrayEntry(
    index: Int,
    value: SerializeElement,
    keyWidth: Dp,
    onValueChange: (SerializeElement) -> Unit,
    onRemove: () -> Unit,
) {
    val keyTopPadding = when (value) {
        is SerializeObject, is SerializeArray -> 6.dp
        is SerializePrimitive -> if (value.isString || value.isNumber) 8.dp else 0.dp
        else -> 0.dp
    }
    val keyModifier = Modifier.width(keyWidth).then(
        if (keyTopPadding > 0.dp) Modifier.padding(top = keyTopPadding) else Modifier
    )

    SerializeElementEntryEditor(
        key = index.toString(),
        data = value,
        keyWrapper = {
            KeyLabel(
                text = "[$index]",
                color = MaterialTheme.colorScheme.tertiary,
                modifier = keyModifier,
            )
        },
        onValueChange = onValueChange,
        actions = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(30.dp),
            ) {
                Icon(Icons.Delete, null, Modifier.size(15.dp))
            }
        },
    )
}

@Composable
fun SerializeArrayEntryEditor(
    serializeArray: SerializeArray,
    onValueChange: (SerializeArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = rememberExpandState(default = serializeArray.size <= 5)
    var showAdder by remember { mutableStateOf(false) }
    var addingType by remember { mutableStateOf(SerializeElementType.String) }
    val keyWidth = rememberMaxLabelWidth(
        labels = serializeArray.indices.map { "[$it] :" },
        style = keyLabelStyle(),
    )

    ElementContainer(
        modifier = modifier,
        title = { Text(HSLang.SerializeEditor.arrayType) },
        count = serializeArray.size,
        expanded = expanded.value,
        onToggle = { expanded.value = !expanded.value },
        onAdd = { showAdder = true },
        emptyText = HSLang.SerializeEditor.emptyArray.plainText,
    ) {
        serializeArray.forEachIndexed { index, element ->
            SerializeArrayEntry(
                index = index,
                value = element,
                keyWidth = keyWidth,
                onValueChange = { newValue ->
                    onValueChange(serializeArray.replacedAt(index, newValue))
                },
                onRemove = {
                    onValueChange(serializeArray.withoutAt(index))
                },
            )
        }
    }

    if (showAdder) {
        SerializeArrayElementAdderDialog(
            selectedType = addingType,
            onTypeChange = { addingType = it },
            onDismiss = { showAdder = false },
            onConfirm = { element ->
                onValueChange(serializeArray.plus(element))
                showAdder = false
            },
        )
    }
}