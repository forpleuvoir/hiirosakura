package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.task.KeyBindTickTask.Companion.withKeyBind
import moe.forpleuvoir.hiirosakura.gui.widget.ItemSelector
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextAreaWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import moe.forpleuvoir.nebula.common.util.collection.notifiableList
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.world.item.Items
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.TaskManagerGui(
    modifier: Modifier = Modifier
) = Column(
    modifier,
    verticalArrangement = Arrangement.spacedBy(5f)
) {
    val filterList = notifiableList(TaskManager.taskList)
    var name = ""

    fun onChanged() {
        filterList.disableNotify {
            filterList.clear()
            filterList.addAll(
                TaskManager.taskList.filter {
                    it.name.contains(name)
                }
            )
        }
        filterList.onChange(filterList)
    }

    //------------  Header ------------\\
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f),
    ) {
        SearchBar(
            textConsumer = { str ->
                name = str
                onChanged()
            },
            hintText = stateOf(IGLang.search.plainText),
            modifier = Modifier.weight(1),
            textEditorModifier = { Modifier.weight(1) }
        )
        Button {
            Text(IGLang.add)
            click {
                TaskEditor(KeyBindTickTask.empty) {
                    TaskManager.add(it.withKeyBind())
                    onChanged()
                }.open()
            }
        }
        Button {
            Text(HSLang.taskReBindKey, Modifier.hoverText(HSLang.taskReBindKeyComment))
            click {
                runCatching {
                    TaskManager.reBindKey()
                }.onSuccess {
                    Toast.showToast(HSLang.success)
                }.onFailure {
                    Toast.showToast(it.message ?: "")
                }
            }
        }
        Button {
            Text(HSLang.taskRunning)
            click {
                HSTickTaskScheduler.openScreen()
            }
        }
        Button {
            Icon(IconTextures.SETTING)
            click {
                SimpleDialog(
                    title = stateOf(TaskManager.Config.translateText)
                ) {
                    ConfigsWrapper(
                        TaskManager.Config.configs(),
                        modifier = Modifier
                            .maxWidth(400f)
                            .maxHeight(260f)
                            .disableRenderBackground()
                            .padding(0),
                        listModifier = {
                            Modifier.weight(1)
                        }
                    )
                }.open()
            }
        }
    }
    //------------ Content ------------\\


    ColumnListWrapped(
        modifier = Modifier.fill().weight(1),
        listModifier = { Modifier.weight(1).fill() },
    ) {
        if (filterList.isEmpty()) Text(IGLang.hasNothing)
        filterList.forEachIndexed { index, task ->
            Row(
                modifier = Modifier.fill()
                    .padding(horizontal = 2f)
                    .bgHoverHighlightBox(),
                horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
            ) {
                //move
                val taskIndex = TaskManager.taskList.indexOf(task)
                MoveButton(taskIndex, TaskManager.taskList.lastIndex, {
                    TaskManager.moveUp(taskIndex)
                    onChanged()
                }, {
                    TaskManager.moveDown(taskIndex)
                    onChanged()
                })
                //Icon
                ItemIcon(task.icon)
                //text
                Row(
                    modifier = Modifier.weight(1),
                    horizontalArrangement = Arrangement.Left
                ) {
                    Text(InlineStyleText(task.name))
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
                KeyBindSetterButton(task.keyBind)
                KeyBindSettingSetterButton(task.keyBind, Literal(task.name))
                //edit
                FlatButton(
                    hoveredColor = Colors.LIME.alpha(0.25f),
                    modifier = Modifier.hoverText(IGLang.edit).size(14f, 14f)
                ) {
                    click {
                        TaskEditor(task) {
                            task.fromTask(it)
                            onChanged()
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
                        onChanged()
                    }
                    Icon(IconTextures.DELETE, HSVColor(0f, .1f, .25f), modifier = Modifier.size(12f, 12f))
                }
            }
        }

    }.apply {
        filterList.subscribe {
            executeRecompose()
        }
    }
}

fun TaskEditor(
    task: HSTickTask,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    newTaskConsumer: (HSTickTask) -> Unit
): IGScreenImpl {
    var name = task.name
    val delay = mutableStateOf(task.setting.delay)
    val period = mutableStateOf(task.setting.period)
    val times = mutableStateOf(task.setting.times)
    val executeOn = mutableStateOf(task.executeOn)
    val executorType = mutableStateOf(task.executorType)
    val icon = mutableStateOf(
        if (task is KeyBindTickTask) {
            task.icon
        } else Items.MELON
    )

    var executor = task.executor.asString()

    var nameEditorTransform: (() -> Transform)? = null

    return Dialog(
        Modifier.maxHeight(280f).width(360f).then(modifier),
        screenModifier = Modifier.onClose {
            TipHandler.popTip("#TASK_MANAGER")
        }.then(screenModifier)
    ) {
        //title
        Text(HSLang.taskEditor)

        Column(
            modifier = Modifier.weight(1),
            verticalArrangement = Arrangement.spacedBy(3f)
        ) {
            Row(
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(5f)
            ) {
                if (task is KeyBindTickTask) {
                    //icon
                    ItemSelector(
                        icon,
                        modifier = Modifier.weight(1).hoverText(HSLang.taskIcon),
                    )
                }
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

        Row(
            Modifier.fill(),
            horizontalArrangement = Arrangement.spacedBy(4f, Alignment.Right)
        ) {
            Button {
                Text(IGLang.confirm)
                click {
                    if (name.isEmpty()) {
                        TipHandler.pushTip(
                            "#TASK_MANAGER",
                            4.seconds,
                            nameEditorTransform!!,
                            Tip { Text(HSLang.cantBeEmpty(HSLang.taskName).withColor(Colors.RED)) }
                        )
                        return@click
                    }
                    newTaskConsumer(
                        if (task is KeyBindTickTask) {
                            KeyBindTickTask(
                                name = name,
                                setting = TickTask.Setting(
                                    delay = delay.getValue(),
                                    period = period.getValue(),
                                    times = times.getValue()
                                ),
                                executeOn = executeOn.getValue(),
                                executorType = executorType.getValue(),
                                icon = icon.getValue(),
                                keyBind = task.keyBind,
                                executor = executorType.getValue().fromString(executor)
                            )
                        } else
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
                    closeScreen()
                }
            }
            Button {
                Text(IGLang.cancel)
                click { closeScreen() }
            }
        }
    }
}


fun ContainerScope.MoveButton(
    index: Int,
    lastIndex: Int,
    moveUp: () -> Unit = {},
    moveDown: () -> Unit = {}
) = Column {
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