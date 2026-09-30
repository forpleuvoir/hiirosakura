package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTaskScheduler
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack

/**
 * 运行中任务的快照。
 *
 * 调度器状态不参与快照观察，靠轮询取；[remaining] 一并取走，条目因此不必再挂自己的定时器。
 *
 * @param tickTask 本次执行的调度实例，同一任务重复执行时各实例互不相同
 * @param task 任务定义
 * @param remaining 剩余执行次数
 */
private data class RunningTaskSnapshot(
    val tickTask: TickTask<Minecraft>,
    val task: HSTickTask,
    val remaining: Int,
)

/** 轮询间隔：剩余次数与列表增删的刷新粒度。 */
private val PollInterval = 50.milliseconds

@Composable
internal fun RunningTasksPage(modifier: Modifier) {
    Box(modifier.fillMaxSize()) {
        val tasks: List<RunningTaskSnapshot> by produceState(initialValue = emptyList()) {
            while (true) {
                val snapshot = HSTickTaskScheduler.runningTasks.map { (tickTask, task) ->
                    RunningTaskSnapshot(tickTask, task, tickTask.times - tickTask.counter)
                }
                if (snapshot != value) value = snapshot
                delay(PollInterval)
            }
        }

        if (tasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val scrollState = rememberLazyListState()
            Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tasks.forEach { snapshot ->
                        item {
                            RunningTaskRow(snapshot) { HSTickTaskScheduler.remove(snapshot.tickTask) }
                        }
                    }
                }

                // 滚动条占自己的一列，不叠在列表上
                if (scrollState.canScroll) Spacer(Modifier.width(8.dp))
                VerticalFlatScroller(adapter = rememberScrollerAdapter(scrollState))
            }
        }
    }
}

/** 单条运行中的任务：图标、名称与执行周期在左，剩余次数与停止按钮在右。 */
@Composable
private fun RunningTaskRow(snapshot: RunningTaskSnapshot, onRemove: () -> Unit) {
    Surface(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val icon = (snapshot.task as? IconTickTask)?.icon
            if (icon != null) {
                val stack = remember(icon) { ItemStack(icon) }
                if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(snapshot.task.nameAsInlineStyleText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    component = HSLang.Task.runningPeriod(snapshot.tickTask.period),
                    color = SokitsuTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Text(component = HSLang.Task.runningRemainingTimes(snapshot.remaining))

            IconButton(onRemove, Modifier.tooltip { Text(IGLang.Misc.remove) }) {
                Icon(Icons.Delete, scale = LocalSokitsuPixelScale.current)
            }
        }
    }
}
