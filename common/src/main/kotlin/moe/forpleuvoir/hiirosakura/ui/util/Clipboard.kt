package moe.forpleuvoir.hiirosakura.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.launch

/**
 * 写入系统剪贴板的回调。
 *
 * compose-minecraft 的场景根已注入 [LocalClipboard]，旧版 IbukiGourd 的 `MinecraftClipboard` 已随 UI
 * 重写删除。写入是挂起操作，所以这里取一次协程作用域，返回可直接在点击回调里调用的 lambda。
 */
@Composable
fun rememberClipboardWriter(): (String) -> Unit {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    return remember(clipboard, scope) {
        { text -> scope.launch { clipboard.setClipEntry(ClipEntry(text)) } }
    }
}
