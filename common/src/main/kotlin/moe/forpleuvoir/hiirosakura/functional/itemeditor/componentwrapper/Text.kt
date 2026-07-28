package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditorDefaults
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditor
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditorState
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.rectInMcWindow
import moe.forpleuvoir.ibukigourd.render.extension.pushTextLines
import moe.forpleuvoir.ibukigourd.text.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.state.isQuickAction
import moe.forpleuvoir.ibukigourd.ui.preset.state.rememberTextFieldState
import moe.forpleuvoir.ibukigourd.ui.skia.LocalSkiaSurface
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

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

        AssistChip(
            {},
            modifier = Modifier
                .fillMaxHeight()
                .width(DataComponentEditorDefaults.entrySize.width)
                .vanillaTooltip(value),
            label = {
                Text(value, overflow = TextOverflow.Ellipsis, maxLines = 1)
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (isQuickAction)
                        showInlineEditorDialog = true
                    else
                        showDialog = true
                }) {
                    Icon(Icons.EditNote, null)
                }
            }
        )

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
    val state = rememberTextFieldState(InlineStyleTextParser.inline(value)) {
        editingValue = InlineStyleTextParser.parse(it, InlineStyleTextParser.noneEventModifier)
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
                OutlinedTextField(
                    state = state,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedLabelBox(
                    label = {
                        Text(HSLang.TextEditor.preview)
                    },
                    modifier = Modifier.weight(1.25f).fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        var area by remember { mutableStateOf<Rect?>(null) }
                        val surface = LocalSkiaSurface.current
                        LaunchedEffect(surface) {
                            while (isActive) {
                                withFrameNanos {
                                    area ?: return@withFrameNanos
                                    surface.postRender {
                                        pushTextLines(
                                            editingValue,
                                            area = area!!.roundToIntRect(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            defaultColor = Colors.WHITE,
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(
                            Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { area = it.rectInMcWindow() }
                        )
                    }
                }

            }
        }
    )
}