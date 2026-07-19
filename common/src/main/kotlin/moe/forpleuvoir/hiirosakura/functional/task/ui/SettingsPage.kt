package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigGroupWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.ui.configwrapper.LocalAutoExpandConfigGroupLimit

@Composable
internal fun SettingsPage(modifier: Modifier) {
    Box(modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalAutoExpandConfigGroupLimit provides 25) {
            val state = rememberScrollState()
            ConfigsWrapper(TaskManager.Config.children, Modifier.verticalScroll(state).padding(12.dp))

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
