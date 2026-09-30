package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.LocalConfigGroupAutoExpandLimit
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter

/** 配置列表与滚动条共用的内边距。 */
private val SettingsPadding = 16.dp

@Composable
internal fun SettingsPage(modifier: Modifier) {
    val scrollState = rememberScrollState()

    // 任务管理器的分组整体默认展开：设置项不多，折叠着看要逐组点开
    CompositionLocalProvider(LocalConfigGroupAutoExpandLimit provides Int.MAX_VALUE) {
        Row(modifier.fillMaxSize().padding(SettingsPadding)) {
            ConfigsWrapper(
                TaskManager.Config.children,
                Modifier.weight(1f).verticalScroll(scrollState),
            )

            // 滚动条占自己的一列，不叠在配置列表上
            VerticalFlatScroller(adapter = rememberScrollerAdapter(scrollState))
        }
    }
}
