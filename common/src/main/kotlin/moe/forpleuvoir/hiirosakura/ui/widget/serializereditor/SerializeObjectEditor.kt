package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.compose_minecraft.platform.ui.thenIf
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.ElementContainer
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.plus
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.rememberExpandState
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.rememberMaxLabelWidth
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.removed
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.renamed
import moe.forpleuvoir.hiirosakura.ui.widget.serializereditor.internal.replaced
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.hiirosakura.ui.widget.ValueTextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button

@Composable
fun SerializeObjectEditor(
    serializeObject: SerializeObject,
    onValueChange: (SerializeObject) -> Unit,
) {
    var showAdder by remember { mutableStateOf(false) }

    val expanded = rememberExpandState(default = serializeObject.size <= 5)
    val keyWidth = rememberMaxLabelWidth(
        labels = serializeObject.keys.map { "$it :" },
        style = keyLabelStyle(),
    )

    ElementContainer(
        title = { DataComponentDialogTitle(component = HSLang.SerializeEditor.objectType) },
        count = serializeObject.size,
        expanded = expanded.value,
        onToggle = { expanded.value = !expanded.value },
        onAdd = { showAdder = true },
        emptyText = HSLang.SerializeEditor.emptyObject.plainText,
    ) {
        serializeObject.entries.forEach { (key, value) ->
            SerializeObjectEntry(
                key = key,
                value = value,
                keyWidth = keyWidth,
                onValueChange = { newValue ->
                    onValueChange(serializeObject.replaced(key, newValue))
                },
                onRemove = {
                    onValueChange(serializeObject.removed(key))
                },
                onRename = { newKey ->
                    if (!serializeObject.containsKey(newKey)) {
                        onValueChange(serializeObject.renamed(key, newKey))
                    }
                },
            )
        }
    }


    if (showAdder) {
        SerializeElementAdderDialog(
            key = serializeObject.size.toString(),
            keyVerify = { !serializeObject.containsKey(it) },
            onDismiss = { showAdder = false },
            onConfirm = { key, element ->
                onValueChange(serializeObject.plus(key, element))
                showAdder = false
            },
        )
    }
}

@Composable
private fun SerializeObjectEntry(
    key: String,
    value: SerializeElement,
    keyWidth: Dp,
    onValueChange: (SerializeElement) -> Unit,
    onRemove: () -> Unit,
    onRename: (String) -> Unit,
) {
    var showRenameDialog by remember { mutableStateOf(false) }

    val keyTopPadding = when (value) {
        is SerializeObject, is SerializeArray -> 6.dp
        is SerializePrimitive                 -> if (value.isString || value.isNumber) 8.dp else 0.dp
        else                                  -> 0.dp
    }

    val keyModifier = Modifier.width(keyWidth).thenIf(keyTopPadding > 0.dp) { Modifier.padding(top = keyTopPadding) }

    SerializeElementEntryEditor(
        key = key,
        data = value,
        keyWrapper = {
            KeyLabel(
                text = key,
                color = SokitsuTheme.colorScheme.primary,
                onClick = { showRenameDialog = true },
                modifier = keyModifier,
            )
        },
        onValueChange = onValueChange,
        actions = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(30.dp),
            ) {
                Icon(Icons.Delete, Modifier.size(15.dp))
            }
        },
    )

    if (showRenameDialog) {
        RenameKeyDialog(
            currentKey = key,
            keyVerify = { newKey -> newKey != key },
            onDismiss = { showRenameDialog = false },
            onConfirm = { newKey ->
                onRename(newKey)
                showRenameDialog = false
            },
        )
    }
}

@Composable
fun SerializeObjectEntryEditor(
    serializeObject: SerializeObject,
    onValueChange: (SerializeObject) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = rememberExpandState(default = serializeObject.size <= 5)
    var showAdder by remember { mutableStateOf(false) }

    val keyWidth = rememberMaxLabelWidth(
        labels = serializeObject.keys.map { "$it :" },
        style = keyLabelStyle(),
    )

    ElementContainer(
        modifier = modifier,
        title = { DataComponentDialogTitle(component = HSLang.SerializeEditor.objectType) },
        count = serializeObject.size,
        expanded = expanded.value,
        onToggle = { expanded.value = !expanded.value },
        onAdd = { showAdder = true },
        emptyText = HSLang.SerializeEditor.emptyObject.plainText,
    ) {
        serializeObject.entries.forEach { (key, value) ->
            SerializeObjectEntry(
                key = key,
                value = value,
                keyWidth = keyWidth,
                onValueChange = { newValue ->
                    onValueChange(serializeObject.replaced(key, newValue))
                },
                onRemove = {
                    onValueChange(serializeObject.removed(key))
                },
                onRename = { newKey ->
                    if (!serializeObject.containsKey(newKey)) {
                        onValueChange(serializeObject.renamed(key, newKey))
                    }
                },
            )
        }
    }


    if (showAdder) {
        SerializeElementAdderDialog(
            key = serializeObject.size.toString(),
            keyVerify = { !serializeObject.containsKey(it) },
            onDismiss = { showAdder = false },
            onConfirm = { key, element ->
                onValueChange(serializeObject.plus(key, element))
                showAdder = false
            },
        )
    }
}

@Composable
private fun RenameKeyDialog(
    currentKey: String,
    keyVerify: (String) -> Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var newKey by remember { mutableStateOf(currentKey) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    SimpleAlertDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            val k = newKey.trim()
            if (k.isBlank()) {
                errorMessage = HSLang.SerializeEditor.keyCannotBeEmpty.plainText
                false
            } else if (!keyVerify(k)) {
                errorMessage = HSLang.SerializeEditor.keyAlreadyExists.plainText
                false
            } else {
                onConfirm(k)
                true
            }
        },
        title = { DataComponentDialogTitle(component = HSLang.SerializeEditor.renameKey) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DataComponentField(component = HSLang.SerializeEditor.key, modifier = Modifier.fillMaxWidth()) {
                    ValueTextField(value = newKey, onValueChange = { newKey = it; errorMessage = null }, modifier = Modifier.fillMaxWidth())
                }
                errorMessage?.let {
                    Text(
                        text = it,
                        color = SokitsuTheme.colorScheme.error,
                        style = SokitsuTheme.typography.body,
                    )
                }
            }
        },
    )
}