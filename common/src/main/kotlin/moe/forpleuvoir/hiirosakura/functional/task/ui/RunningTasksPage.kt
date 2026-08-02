package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTaskScheduler
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTaskScheduler.remove
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Delete
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import net.minecraft.client.Minecraft
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun RunningTasksPage(modifier: Modifier) {
    Box(modifier) {
        val tasks by produceState(initialValue = emptyList()) {
            var previousSnapshot = emptyList<Pair<TickTask<Minecraft>, HSTickTask>>()

            while (true) {
                val currentSnapshot = HSTickTaskScheduler.runningTasks.toList()
                if (currentSnapshot != previousSnapshot) {
                    value = currentSnapshot
                    previousSnapshot = currentSnapshot
                }
                delay(50.milliseconds)
            }
        }
        if (tasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val scrollState = rememberLazyListState()
            LazyColumn(
                Modifier.fillMaxSize().padding(8.dp),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tasks.forEach { (tickTask, task) ->
                    item {
                        RunningTask(tickTask, task, { remove(tickTask) })
                    }
                }
            }
            VerticalScrollbar(rememberScrollbarAdapter(scrollState), Modifier.align(Alignment.CenterEnd))
        }
    }
}


@Composable
private fun RunningTask(tickTask: TickTask<Minecraft>, task: HSTickTask, remove: () -> Unit) {
    Card {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var counter by remember { mutableIntStateOf(tickTask.times - tickTask.counter) }
            LaunchedEffect(tickTask) {
                while (isActive) {
                    delay(16.milliseconds)
                    counter = tickTask.times - tickTask.counter
                }
            }
            Text(task.name + "@" + tickTask.hashCode(), modifier = Modifier.weight(1f))
            Text(HSLang.Task.runningPeriod(tickTask.period))
            Text(HSLang.Task.runningRemainingTimes(counter))
            IconButton(remove) {
                Icon(Icons.Delete, null)
            }
        }
    }
}
