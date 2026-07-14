package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.EndTick
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.task.TickTaskScheduler
import net.minecraft.client.Minecraft
import java.util.concurrent.ConcurrentLinkedQueue

object HSTickTaskScheduler : TickTaskScheduler<Minecraft>() {

    private val hsTasks = ConcurrentLinkedQueue<Pair<TickTask<Minecraft>, HSTickTask>>()

    val runningTasks: List<Pair<TickTask<Minecraft>, HSTickTask>>
        get() = hsTasks.toList()

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

}