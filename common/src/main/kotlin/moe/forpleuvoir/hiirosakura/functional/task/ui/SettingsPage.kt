package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.ui.widget.PagePanel
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.LocalConfigGroupAutoExpandLimit

@Composable
internal fun SettingsPage(modifier: Modifier) {
    val scrollState = rememberScrollState()

    // 任务管理器的分组整体默认展开：设置项不多，折叠着看要逐组点开
    CompositionLocalProvider(LocalConfigGroupAutoExpandLimit provides Int.MAX_VALUE) {
        PagePanel(modifier = modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                ConfigsWrapper(
                    TaskManager.Config.children,
                    Modifier.weight(1f).verticalScroll(scrollState),
                )

                // 滚动条占自己的一列，不叠在配置列表上
                ScrollbarColumn(scrollState)
            }
        }
    }
}
