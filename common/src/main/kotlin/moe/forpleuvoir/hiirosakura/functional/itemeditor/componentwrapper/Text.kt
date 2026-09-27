package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditor
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditorState
import moe.forpleuvoir.ibukigourd.text.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.text.input.rememberTextFieldState

@Composable
fun TextComponentWrapper(
    key: Identifier,
    value: Component,
    onValueChange: (Component) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) = DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Box(modifier = Modifier.height(DataComponentEditorDefaults.entrySize.height).padding(vertical = 4.dp)) {
        var showDialog by remember { mutableStateOf(false) }

        var showInlineEditorDialog by remember { mutableStateOf(false) }

        FlatButton(
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .width(DataComponentEditorDefaults.entrySize.width)
                .vanillaTooltip(value),
        ) {
Text(value, overflow = TextOverflow.Ellipsis, maxLines = 1)
IconButton(onClick = {
    if (isQuickAction)
        showInlineEditorDialog = true
    else
        showDialog = true
}) {
    Icon(Icons.Edit)
}
        }
if (showDialog) {
            RichTextEditorDialog(value, onValueChange, { Text(key) }) { showDialog = false }
        }

        if (showInlineEditorDialog) {
            InlineStyleTextEditorDialog(value, onValueChange, { Text(key) }) { showInlineEditorDialog = false }
        }
    }
}

@Composable
fun RichTextEditorDialog(
    value: Component,
    onValueChange: (Component) -> Unit,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    val state = remember { RichTextEditorState.fromMcText(value) }
    FlexibleDialog(
        onDismissRequest,
        modifier = Modifier.padding(24.dp).height(520.dp).width(720.dp),
        onConfirmRequest = {
            onValueChange(state.mcText)
            true
        },
        title = title,
        content = {
            RichTextEditor(
                state = state
            )
        }
    )
}

@Composable
fun InlineStyleTextEditorDialog(
    value: Component,
    onValueChange: (Component) -> Unit,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    var editingValue by remember { mutableStateOf(value) }

    val state = rememberTextFieldState(InlineStyleTextParser.inline(value))

    // 输入即时同步回编辑值（新版 TextFieldState 不再接受 onChange 回调）
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { text ->
            editingValue = InlineStyleTextParser.parse(text, InlineStyleTextParser.noneEventModifier)
        }
    }
    FlexibleDialog(
        onDismissRequest,
        modifier = Modifier.padding(24.dp).height(520.dp).width(720.dp),
        onConfirmRequest = {
            onValueChange(editingValue)
            true
        },
        title = title,
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                TextField(
                    state = state,
                    modifier = Modifier .fillMaxWidth() .weight(1f),
                )
                Spacer(Modifier.height(12.dp))
                LabelBox(
                    label = {
                        Text(component = HSLang.TextEditor.preview)
                    },
                    modifier = Modifier.weight(1.25f).fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            component = editingValue,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

            }
        }
    )
}