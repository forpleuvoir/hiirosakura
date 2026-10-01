package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField

/**
 * 由外部字符串驱动的单行输入框。
 *
 * sokitsu 的 [TextField] 只接受 `TextFieldState`，而旧调用点（配置键、序列化键名等）是
 * `value: String` + `onValueChange` 形态。这里在内部维持一个 [rememberTextFieldState]，
 * 把用户的输入回传出去，并在外部值变化时回写状态（值相等时不写，避免打断输入）。
 *
 * @param value 当前值
 * @param onValueChange 输入回调
 * @param isError 错误态
 */
@Composable
fun ValueTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
) {
    val state = rememberTextFieldState(value)

    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collect { text ->
            if (text != value) onValueChange(text)
        }
    }
    LaunchedEffect(value) {
        if (state.text.toString() != value) state.setTextAndPlaceCursorAtEnd(value)
    }

    TextField(
        state = state,
        enabled = enabled,
        isError = isError,
        lineLimits = lineLimits,
        modifier = modifier.fillMaxWidth(),
    )
}
