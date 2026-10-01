package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import net.minecraft.resources.Identifier
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton

@Composable
fun IdentifierComponentWrapper(
    key: Identifier,
    value: Identifier,
    onValueChange: (Identifier) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        onEdit = { showDialog = true },
    ) {
        Text(value, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    if (showDialog) {
        IdentifierEditorDialog(value, onValueChange, { DataComponentDialogTitle(key) }, { showDialog = false })
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DataComponentField(title = { Text("Namespace", fontSize = SokitsuTheme.typography.body.fontSize) }) {
                    Column {
                        TextField(
                            namespace,
                            isError = !checkNamespace,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (!checkNamespace) {
                            Text("Non [a-z0-9_.-] character in namespace of location")
                        }
                    }
                }
                DataComponentField(title = { Text("Path", fontSize = SokitsuTheme.typography.body.fontSize) }) {
                    Column {
                        TextField(
                            path,
                            isError = !checkPath,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (!checkPath) {
                            Text("Non [a-z0-9/._-] character in path of location")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (checkNamespace && checkPath) {
                    onValueChange(Identifier.fromNamespaceAndPath(namespace.text.toString(), path.text.toString()))
                    onDismissRequest()
                }
            }, enabled = checkNamespace && checkPath) {
                Text(component = IGLang.Misc.confirm)
            }
        }
    )
}
