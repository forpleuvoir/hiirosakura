package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.hiirosakura.ui.icon.HSIcons
import moe.forpleuvoir.hiirosakura.ui.icon.VectorIcon
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Assignment
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Task
import moe.forpleuvoir.hiirosakura.ui.compat.NavigationRail
import moe.forpleuvoir.hiirosakura.ui.compat.NavigationRailItem

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
                icon = { VectorIcon(HSIcons.Task) },
                label = { Text(component = HSLang.Task.tasks) }
            )
            NavigationRailItem(
                selected = selectedPage == 1,
                onClick = { selectedPage = 1 },
                icon = { VectorIcon(HSIcons.Assignment) },
                label = { Text(component = HSLang.Task.runningTasks) }
            )
            NavigationRailItem(
                selected = selectedPage == 2,
                onClick = { selectedPage = 2 },
                icon = { Icon(Icons.Setting) },
                label = { Text(component = HSLang.Task.settings) }
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
