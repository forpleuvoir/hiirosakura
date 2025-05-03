package moe.forpleuvoir.hiirosakura.functional.task

import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.EndTick
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecuteOn.StartTick
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler.launch
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.task.TickTaskScheduler
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import net.minecraft.client.MinecraftClient
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.milliseconds

object HSTickTaskScheduler : TickTaskScheduler<MinecraftClient>() {

    private val hsTasks = ConcurrentLinkedQueue<Pair<TickTask<MinecraftClient>, HSTickTask>>()

    fun execute(task: HSTickTask) {
        val tickTask = task.asTickTask()
        hsTasks.add(tickTask to task)
        when (task.executeOn) {
            StartTick -> scheduleStartTick(tickTask)
            EndTick   -> scheduleEndTick(tickTask)
        }
    }

    override fun removeFromEnd(task: TickTask<MinecraftClient>) {
        super.removeFromEnd(task)
        hsTasks.removeIf { it.first == task }
    }

    override fun removeFromStart(task: TickTask<MinecraftClient>) {
        super.removeFromStart(task)
        hsTasks.removeIf { it.first == task }
    }

    fun openScreen() = Dialog(
        Modifier
    ) {
        TextLabel(stateOf(HSLang.taskRunning))
        ColumnListWrapped(
            listModifier = { Modifier.width(360f).height(200f) }
        ) {
            if (hsTasks.isEmpty()) TextLabel(IGLang.hasNothing)
            hsTasks.forEach { (tickTask, task) ->
                Row(
                    Modifier.padding(horizontal = 2f),
                    horizontalArrangement = Arrangement.spacedBy(5f, Alignment.CenterHorizontally)
                ) {
                    TextLabel(task.name + "@" + tickTask.hashCode(), modifier = Modifier.weight(1))
                    TextLabel(HSLang.taskRunningPeriod(tickTask.period))
                    TextLabel(mutableStateBy {
                        val counter = tickTask.times - tickTask.counter
                        if (counter <= 0) {
                            launch {
                                delay(50)
                                this@ColumnListWrapped.executeRecompose()
                            }
                        }
                        HSLang.taskRunningRemainingTimes(counter)
                    }, setting = TextSetting(textLabelUpdateInterval = 50.milliseconds))
                    RemoveButton {
                        remove(tickTask)
                        launch {
                            delay(50)
                            this@ColumnListWrapped.executeRecompose()
                        }
                    }
                }
            }
        }
    }.open()

}