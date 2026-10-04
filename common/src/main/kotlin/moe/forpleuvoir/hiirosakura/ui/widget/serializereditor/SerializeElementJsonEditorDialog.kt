package moe.forpleuvoir.hiirosakura.ui.widget.serializereditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JsonSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightDefaults
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.hiirosakura.ui.widget.truncateLines
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.json.JsonDialect

/** JSON 文本区宽度占可用宽度的比例：竖长排版，不铺满整个屏幕。 */
private const val JsonEditorWidthFraction = 0.5f

/**
 * JSON 文本区最小高度。
 *
 * 取值大于多数屏幕的可用高度，实际由父约束夹到「标题与按钮之外剩下的高度」，
 * 因此文本区总是占满可用高度而不会顶出屏幕。
 */
private val JsonEditorMinHeight = 1600.dp

/**
 * 纯 JSON 文本编辑浮层：整块 JSON 文本区（带 JSON 语法高亮）。
 *
 * 确认时以 [JsonDialect] 解码文本区内容；解码失败保持浮层打开、把文本区标为错误态并显示错误文本。
 *
 * @param title 浮层标题
 * @param initialData 初始数据，其 JSON 文本作为文本区初值
 * @param onDismissRequest 关闭请求
 * @param onConfirmRequest 确认回调，参数为解码后的数据；返回 false 时保持浮层打开
 */
@Composable
fun SerializeElementJsonEditorDialog(
    title: @Composable () -> Unit,
    initialData: SerializeElement,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (SerializeElement) -> Boolean,
) {
    val state = rememberTextFieldState(JsonDialect.encode(initialData))
    var errorMessage by remember { mutableStateOf<String?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(24.dp),
        title = title,
        onConfirmRequest = {
            runCatching { JsonDialect.decode(state.text.toString()).getOrThrow() }.fold(
                onSuccess = { onConfirmRequest(it) },
                onFailure = { e ->
                    errorMessage = e.stackTraceToString().truncateLines(8)
                    false
                },
            )
        },
        content = {
            Column {
                val transformation = rememberSyntaxHighlightTransformation(
                    language = JsonSyntaxLanguage,
                    theme = SyntaxHighlightDefaults.theme(),
                    text = state.text.toString(),
                )
                TextField(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth(JsonEditorWidthFraction)
                        .heightIn(min = JsonEditorMinHeight)
                        .codeEditorShortcuts(state),
                    lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 12),
                    outputTransformation = transformation,
                    fontSize = SokitsuTheme.typography.body.fontSize,
                    isError = errorMessage != null,
                )
                errorMessage?.let {
                    Text(it, color = SokitsuTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
    )
}
