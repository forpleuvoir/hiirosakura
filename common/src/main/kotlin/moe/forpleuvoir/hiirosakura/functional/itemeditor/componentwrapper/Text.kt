package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDisplayRow
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentField
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentSection
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.Text
import moe.forpleuvoir.hiirosakura.ui.modifier.vanillaTooltip
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditor
import moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor.RichTextEditorState
import moe.forpleuvoir.ibukigourd.text.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.compose_minecraft.platform.ui.text.LocalDefaultFont
import moe.forpleuvoir.compose_minecraft.platform.ui.text.MinecraftFonts
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text as SokitsuText

@Composable
fun TextComponentWrapper(
    key: Identifier,
    value: Component,
    onValueChange: (Component) -> Unit,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    var showDialog by remember { mutableStateOf(false) }

    var showInlineEditorDialog by remember { mutableStateOf(false) }

    DataComponentDisplayRow(
        key, removeAction, modifier, horizontalArrangement, verticalAlignment,
        frameModifier = Modifier.vanillaTooltip(value),
        onEdit = {
            if (isQuickAction)
                showInlineEditorDialog = true
            else
                showDialog = true
        },
    ) {
        Text(value, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
    if (showDialog) {
        RichTextEditorDialog(value, onValueChange, { DataComponentDialogTitle(key) }) { showDialog = false }
    }

    if (showInlineEditorDialog) {
        InlineStyleTextEditorDialog(value, onValueChange, { DataComponentDialogTitle(key) }) { showInlineEditorDialog = false }
    }
}

/** 文本编辑浮层的尺寸约束：宽度只夹上下限，高度只给上限。 */
private object TextEditorDialogDefaults {

    /** 浮层宽度下限：多行编辑区与格式工具栏一行放得下。 */
    val MinWidth: Dp = 720.dp

    /** 浮层宽度上限。 */
    val MaxWidth: Dp = 1000.dp

    /** 浮层高度上限：编辑区与预览区共用的可视高度。 */
    val MaxHeight: Dp = 760.dp

    /** 浮层尺寸约束：宽度只夹上下限，具体宽度由窗口决定。 */
    val DialogModifier: Modifier
        get() = Modifier
            .widthIn(min = MinWidth, max = MaxWidth)
            .heightIn(max = MaxHeight)
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
        modifier = TextEditorDialogDefaults.DialogModifier,
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

    // 输入即时同步回编辑值
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { text ->
            editingValue = InlineStyleTextParser.parse(text, InlineStyleTextParser.noneEventModifier)
        }
    }
    FlexibleDialog(
        onDismissRequest,
        modifier = TextEditorDialogDefaults.DialogModifier,
        onConfirmRequest = {
            onValueChange(editingValue)
            true
        },
        title = title,
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                TextField(
                    state = state,
                    lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 6, maxHeightInLines = 12),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
                Spacer(Modifier.height(12.dp))
                DataComponentSection(
                    title = {
                        Text(component = HSLang.TextEditor.preview, fontSize = SokitsuTheme.typography.body.fontSize)
                    },
                    modifier = Modifier.weight(1.25f).fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CompositionLocalProvider(LocalDefaultFont provides MinecraftFonts.Default) {
                            SokitsuText(
                                component = editingValue,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                            )
                        }
                    }
                }

            }
        }
    )
}
