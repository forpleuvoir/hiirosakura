package moe.forpleuvoir.hiirosakura.functional.task

import com.ibm.icu.impl.coll.Collation
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.task.KeyBindTickTask.Companion.withKeyBind
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderBox
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextAreaWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.collection.notifiableList
import moe.forpleuvoir.nebula.common.util.primitive.pick
import kotlin.time.Duration.Companion.seconds

fun WidgetContainerScope.TaskManagerGui(
    modifier: Modifier = Modifier
) = Row(
    modifier,
    verticalArrangement = Arrangement.spacedBy(5f)
) {
    val filterList = notifiableList(TaskManager.taskList)
    //------------  Header ------------\\
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f),
    ) {
        SearchBar(
            textConsumer = { str ->
                filterList.enableNotify = true
                filterList.clear()
                filterList.addAll(
                    TaskManager.taskList.filter {
                        it.name.contains(str)
                    }
                )
            },
            hintText = stateOf(IGLang.search.plainText),
            modifier = Modifier.weight(1),
            textEditorModifier = { Modifier.weight(1) }
        )
        Button {
            TextLabel(IGLang.add)
            click {
                TaskEditor(HSTickTask.empty) {
                    TaskManager.add(it.withKeyBind())
                    filterList.clear()
                    filterList.addAll(TaskManager.taskList)
                }.open()
            }
        }
        Button {
            Icon(IconTextures.SETTING)
            click {
                //TODO Open Setting
            }
        }
    }
    //------------ Content ------------\\


    RowListWrapped(
        modifier = Modifier.fill().weight(1),
        listModifier = { Modifier.weight(1).fill() },
    ) {
        if (filterList.isEmpty()) TextLabel(IGLang.hasNothing)
        filterList.forEachIndexed { index, task ->

            var alpha = 0f
            val maxAlpha = 0.25f
            // alpha per tick
            val aupt = maxAlpha * 0.15f
            val adpt = maxAlpha * 0.25f
            val color = Colors.CYAN.alpha(alpha)

            fun updateAlpha(wasMouseOver: Boolean, delta: Float) {
                alpha = if (wasMouseOver)
                    (alpha + aupt * delta).coerceIn(0f, maxAlpha)
                else (alpha - adpt * delta).coerceIn(0f, maxAlpha)
            }

            Column(
                modifier = Modifier.fill()
                    .padding(horizontal = 2f)
                    .render { context, x, y, delta ->
                        updateAlpha(wasMouseOver, delta)
                        context.batchRenderBox {
                            pushRoundBox(transform, color.alpha(alpha), 2)
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
            ) {
                //move
                val taskIndex = TaskManager.taskList.indexOf(task)
                MoveButton(taskIndex, TaskManager.taskList.lastIndex, {
                    TaskManager.moveUp(taskIndex)
                    filterList.clear()
                    filterList.addAll(TaskManager.taskList)
                }, {
                    TaskManager.moveDown(taskIndex)
                    filterList.clear()
                    filterList.addAll(TaskManager.taskList)
                })
                //text
                Column(
                    modifier = Modifier.weight(1),
                    horizontalArrangement = Arrangement.Left
                ) {
                    TextLabel(task.name, modifier = Modifier.weight(1))
                }
                //run
                FlatButton(
                    hoveredColor = Colors.LIME.alpha(0.25f),
                    modifier = Modifier.hoverText(HSLang.taskExecute).size(14f, 14f)
                ) {
                    click { task.execute() }
                    Icon(IconTextures.RIGHT, HSVColor(120f, 1f, 0.5f))
                }
                //key bind
                KeyBindButton(task.keyBind)
                KeyBindSettingButton(task.keyBind, Literal(task.name))
                //edit
                FlatButton(
                    hoveredColor = Colors.LIME.alpha(0.25f),
                    modifier = Modifier.hoverText(IGLang.edit).size(14f, 14f)
                ) {
                    click {
                        TaskEditor(task) {
                            task.fromTask(it)
                            filterList.onChange(filterList)
                        }.open()
                    }
                    Icon(IconTextures.EDIT, modifier = Modifier.size(12f, 12f))
                }

                //delete
                FlatButton(
                    hoveredColor = Colors.RED.alpha(0.25f),
                    modifier = Modifier.hoverText(IGLang.remove).size(14f, 14f)
                ) {
                    click {
                        TaskManager.remove(task)
                        filterList.clear()
                        filterList.addAll(TaskManager.taskList)
                    }
                    Icon(IconTextures.DELETE, HSVColor(0f, .1f, .25f), modifier = Modifier.size(12f, 12f))
                }
            }
        }

    }.apply {
        filterList.subscribe {
            execute {
                this.recompose()
            }
        }
    }
}

fun TaskEditor(
    task: HSTickTask,
    modifier: Modifier = Modifier,
    newTaskConsumer: (HSTickTask) -> Unit
): IGScreenImpl {
    var name = task.name
    val delay = mutableStateOf(task.setting.delay)
    val period = mutableStateOf(task.setting.period)
    val times = mutableStateOf(task.setting.times)
    val executeOn = mutableStateOf(task.executeOn)
    val executorType = mutableStateOf(task.executorType)
    var executor = task.executor.asString

    var nameEditorTransform: (() -> Transform)? = null

    return Dialog(
        Modifier.maxHeight(280f).width(360f).then(modifier),
    ) {
        //title
        TextLabel(HSLang.taskManager)

        Row(
            modifier = Modifier.weight(1),
            verticalArrangement = Arrangement.spacedBy(3f)
        ) {
            Column(
                horizontalArrangement = Arrangement.spacedBy(5f)
            ) {
                //name
                TextEditor(
                    modifier = Modifier
                        .weight(2)
                        .hoverText(HSLang.taskName)
                ) {
                    text = name
                    textConsumer {
                        name = it
                    }
                    nameEditorTransform = { this.owner().transform }
                }
                //delay
                IntEditor(delay, 0..999, modifier = Modifier.weight(1).hoverText(HSLang.taskDelay), editorModifier = { Modifier.weight(1) })
                //period
                IntEditor(period, 1..999, modifier = Modifier.weight(1).hoverText(HSLang.taskPeriod), editorModifier = { Modifier.weight(1) })
                //times
                IntEditor(times, 1..999, modifier = Modifier.weight(1).hoverText(HSLang.taskTimes), editorModifier = { Modifier.weight(1) })
            }
            Column(
                horizontalArrangement = Arrangement.spacedBy(5f)
            ) {
                //executeOn
                EnumSelector(executeOn, modifier = Modifier.weight(1).hoverText(HSLang.taskExecuteOn))
                //executorType
                EnumSelector(executorType, options = ExecutorType.entries, modifier = Modifier.weight(1).hoverText(HSLang.taskExecutorType))
            }
            //executor
            TextAreaWrapped(
                modifier = Modifier.weight(1)
            ) {
                text = executor
                textConsumer {
                    executor = it
                }
            }
        }

        Column(
            Modifier.fill(),
            horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Right)
        ) {
            Button {
                TextLabel(IGLang.confirm)
                click {
                    if (name.isEmpty()) {
                        TipHandler.pushTip("#TASK_MANAGER", 4.seconds, nameEditorTransform!!, Tip { TextLabel(HSLang.taskNameEmpty.withColor(Colors.RED)) })
                        return@click
                    }
                    newTaskConsumer(
                        HSTickTask(
                            name = name,
                            setting = TickTask.Setting(
                                delay = delay.getValue(),
                                period = period.getValue(),
                                times = times.getValue()
                            ),
                            executeOn = executeOn.getValue(),
                            executorType = executorType.getValue(),
                            executor = executorType.getValue().fromString(executor)
                        )
                    )
                    mc.currentScreen?.close()
                }
            }
            Button {
                TextLabel(IGLang.cancel)
                click { mc.currentScreen?.close() }
            }
        }
    }
}


fun WidgetContainerScope.MoveButton(
    index: Int,
    lastIndex: Int,
    moveUp: () -> Unit = {},
    moveDown: () -> Unit = {}
) = Row {
    FlatButton(
        hoveredColor = Colors.BLACK.alpha(.15f),
        round = 0,
        modifier = Modifier.hoverText(IGLang.moveUp).padding(1).active(index > 0)
    ) {
        Icon(IconTextures.UP, modifier = Modifier, color = Colors.GRAY.alpha((index > 0).pick(1f, .25f)))
        click {
            moveUp()
        }
    }
    FlatButton(
        hoveredColor = Colors.BLACK.alpha(.15f),
        round = 0,
        modifier = Modifier.hoverText(IGLang.moveDown).padding(1).active(index != lastIndex)
    ) {
        Icon(
            IconTextures.DOWN,
            modifier = Modifier,
            color = Colors.GRAY.alpha((index != lastIndex).pick(1f, .25f))
        )
        click {
            moveDown()
        }
    }
}