package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import net.minecraft.resources.Identifier

@Composable
fun IdentifierComponentWrapper(
    key: Identifier,
    value: Identifier,
    onValueChange: (Identifier) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }
        AssistChip(
            {},
            modifier = Modifier.fillMaxHeight().width(DataComponentEditorDefaults.entrySize.width),
            label = {
                Text(value, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

        if (showDialog) {
            IdentifierEditorDialog(value, onValueChange, { Text(key) }, { showDialog = false })
        }
    }
}

@Composable
fun IdentifierEditorDialog(
    value: Identifier,
    onValueChange: (Identifier) -> Unit,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    val namespace = rememberTextFieldState(value.namespace)
    val path = rememberTextFieldState(value.path)

    val checkNamespace: Boolean = Identifier.isValidNamespace(namespace.text.toString())
    val checkPath: Boolean = Identifier.isValidPath(path.text.toString())

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        onConfirmRequest = { true },
        title = title,
        content = {
            Column {
                OutlinedTextField(
                    namespace,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = {
                        Row {
                            Text("Namespace")
                            if (!checkNamespace) {
                                Spacer(Modifier.width(8.dp))
                                Text("Non [a-z0-9_.-] character in namespace of location")
                            }
                        }
                    },
                    isError = !checkNamespace,
                    modifier = Modifier.fillMaxWidth().height(68.dp),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    path,
                    labelPosition = TextFieldLabelPosition.Attached(true),
                    label = {
                        Row {
                            Text("Path")
                            if (!checkPath) {
                                Spacer(Modifier.width(8.dp))
                                Text("Non [a-z0-9/._-] character in path of location")
                            }
                        }
                    },
                    isError = !checkPath,
                    modifier = Modifier.fillMaxWidth().height(68.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (checkNamespace && checkPath) {
                    onValueChange(Identifier.fromNamespaceAndPath(namespace.text.toString(), path.text.toString()))
                    onDismissRequest()
                }
            }, enabled = checkNamespace && checkPath) {
                Text(IGLang.Misc.confirm)
            }
        }
    )
}