package moe.forpleuvoir.hiirosakura.functional.task

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.EndTick
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.task.TickTaskScheduler
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Delete
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import net.minecraft.client.Minecraft
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.milliseconds

object HSTickTaskScheduler : TickTaskScheduler<Minecraft>() {

    private val hsTasks = ConcurrentLinkedQueue<Pair<TickTask<Minecraft>, HSTickTask>>()

    fun execute(task: HSTickTask) {
        val tickTask = task.asTickTask()
        hsTasks.add(tickTask to task)
        when (task.executeOn) {
            StartTick -> scheduleStartTick(tickTask)
            EndTick   -> scheduleEndTick(tickTask)
        }
    }

    override fun removeFromEnd(task: TickTask<Minecraft>) {
        super.removeFromEnd(task)
        hsTasks.removeIf { it.first == task }
    }

    override fun removeFromStart(task: TickTask<Minecraft>) {
        super.removeFromStart(task)
        hsTasks.removeIf { it.first == task }
    }

    //运行中的 任务列表
    @Composable
    fun RunningTaskDialog(close: () -> Unit) {
        AlertDialog(
            close,
            title = { Text(HSLang.taskRunning) },
            confirmButton = {
                TextButton(onClick = close) {
                    Text(IGLang.Misc.confirm)
                }
            },
            text = {
                Box {
                    val tasks by produceState(initialValue = emptyList<Pair<TickTask<Minecraft>, HSTickTask>>()) {
                        var previousSnapshot = emptyList<Pair<TickTask<Minecraft>, HSTickTask>>()

                        while (true) {
                            val currentSnapshot = hsTasks.toList()
                            if (currentSnapshot != previousSnapshot) {
                                value = currentSnapshot
                                previousSnapshot = currentSnapshot
                            }
                            delay(50.milliseconds)
                        }
                    }
                    val scrollState = rememberScrollState()
                    LazyColumn(Modifier.fillMaxSize()) {
                        tasks.forEach { (tickTask, task) ->
                            item {
                                RunningTask(tickTask, task, { remove(tickTask) })
                            }
                        }
                    }
                    VerticalScrollbar(rememberScrollbarAdapter(scrollState), Modifier.align(Alignment.CenterEnd))
                }
            }
        )
    }

    @Composable
    private fun RunningTask(tickTask: TickTask<Minecraft>, task: HSTickTask, remove: () -> Unit) {
        Row(
            Modifier.padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
        ) {
            var counter by remember { mutableIntStateOf(tickTask.times - tickTask.counter) }
            LaunchedEffect(tickTask) {
                while (isActive) {
                    delay(16.milliseconds)
                    counter = tickTask.times - tickTask.counter
                }
            }
            Text(task.name + "@" + tickTask.hashCode(), modifier = Modifier.weight(1f))
            Text(HSLang.taskRunningPeriod(tickTask.period))
            Text(HSLang.taskRunningRemainingTimes(counter))
            IconButton(remove) {
                Icon(Icons.Delete, null)
            }
        }
    }

}