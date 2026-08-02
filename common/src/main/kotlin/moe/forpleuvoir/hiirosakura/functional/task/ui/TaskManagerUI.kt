package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Assignment
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Task
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Settings
import moe.forpleuvoir.ibukigourd.ui.preset.Text

@Composable
fun TaskManagerUI(modifier: Modifier = Modifier) {
    var selectedPage by remember { mutableIntStateOf(0) }

    Row(modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(Modifier.height(8.dp))
            NavigationRailItem(
                selected = selectedPage == 0,
                onClick = { selectedPage = 0 },
                icon = { Icon(Icons.Task, null) },
                label = { Text(HSLang.Task.tasks) }
            )
            NavigationRailItem(
                selected = selectedPage == 1,
                onClick = { selectedPage = 1 },
                icon = { Icon(Icons.Assignment, null) },
                label = { Text(HSLang.Task.runningTasks) }
            )
            NavigationRailItem(
                selected = selectedPage == 2,
                onClick = { selectedPage = 2 },
                icon = { Icon(Icons.Settings, null) },
                label = { Text(HSLang.Task.settings) }
            )
        }

        Spacer(Modifier.width(12.dp))
        VerticalDivider()

        when (selectedPage) {
            0 -> TasksPage(Modifier.weight(1f))
            1 -> RunningTasksPage(Modifier.weight(1f))
            2 -> SettingsPage(Modifier.weight(1f))
        }
    }
}
