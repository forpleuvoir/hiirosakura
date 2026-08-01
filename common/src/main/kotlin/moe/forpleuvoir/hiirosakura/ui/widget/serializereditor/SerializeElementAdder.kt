package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

@Composable
fun SerializeElementAdderDialog(
    key: String,
    keyVerify: (String) -> Boolean,
    onDismiss: () -> Unit,
    onConfirm: (key: String, element: SerializeElement) -> Unit,
) {
    var currentKey by remember { mutableStateOf(key) }
    var selectedType by remember { mutableStateOf(SerializeElementType.String) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    SimpleAlertDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            val k = currentKey.trim()
            if (k.isBlank()) {
                errorMessage = HSLang.SerializeEditor.keyCannotBeEmpty.plainText
                false
            } else if (!keyVerify(k)) {
                errorMessage = HSLang.SerializeEditor.keyAlreadyExists.plainText
                false
            } else {
                onConfirm(k, selectedType.defaultValue.deepCopy())
                true
            }
        },
        title = { Text(HSLang.SerializeEditor.addElement) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentKey,
                    onValueChange = { currentKey = it; errorMessage = null },
                    singleLine = true,
                    label = { Text(HSLang.SerializeEditor.key) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { /* confirm handled by dialog */ }),
                )
                EnumSelector(
                    selected = selectedType,
                    onSelect = { selectedType = it },
                    items = SerializeElementType.entries,
                    label = { Text(HSLang.SerializeEditor.type) },
                    modifier = Modifier.fillMaxWidth(),
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
    )
}

@Composable
fun SerializeArrayElementAdderDialog(
    selectedType: SerializeElementType,
    onTypeChange: (SerializeElementType) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (element: SerializeElement) -> Unit,
) {
    SimpleAlertDialog(
        onDismissRequest = onDismiss,
        onConfirmRequest = {
            onConfirm(selectedType.defaultValue.deepCopy())
            true
        },
        title = { Text(HSLang.SerializeEditor.addElement) },
        content = {
            EnumSelector(
                selected = selectedType,
                onSelect = onTypeChange,
                items = SerializeElementType.entries,
                label = { Text(HSLang.SerializeEditor.type) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}