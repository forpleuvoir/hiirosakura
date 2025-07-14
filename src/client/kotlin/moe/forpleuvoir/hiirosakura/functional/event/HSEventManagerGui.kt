package moe.forpleuvoir.hiirosakura.functional.event

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType.*
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.MoveButton
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.gui.widget.EditButton
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextAreaWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.translateComment
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.collection.notifiableList
import moe.forpleuvoir.nebula.event.Event
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.time.Duration.Companion.seconds
import moe.forpleuvoir.ibukigourd.task.TickTask.Setting as TickTaskSetting

fun ContainerScope.HSEventManagerGui(
    modifier: Modifier = Modifier
) = Column(
    modifier,
    verticalArrangement = Arrangement.spacedBy(5f)
) {
    val filterList = notifiableList(HSEventManager.subscriberList)
    var name = ""
    val selectedEvent: MutableState<KClass<out Event>> = mutableStateOf(Event::class)

    fun onChanged() {
        filterList.disableNotify {
            filterList.clear()
            filterList.addAll(
                HSEventManager.subscriberList.filter {
                    it.name.contains(name) && it.eventType.isSubclassOf(selectedEvent.getValue())
                }
            )
        }
        filterList.onChange(filterList)
    }

    selectedEvent.subscribe {
        onChanged()
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(5f),
    ) {
        EventSelector(
            listOf(Event::class) + HSEventManager.subscribableEvents,
            selectedEvent,
            modifier = Modifier.width(120f).height(20f),
            amountStep = 15f
        )
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
            TextLabel(IGLang.add)
            click {
                EventSubscriberEditor(HSEventSubscriber.empty) {
                    HSEventManager.add(it)
                    onChanged()
                }.open()
            }
        }
    }

    //------------ Content ------------\\
    ColumnListWrapped(
        modifier = Modifier.fill().weight(1),
        listModifier = { Modifier.weight(1).fill() },
    ) {
        if (filterList.isEmpty()) TextLabel(IGLang.hasNothing)
        filterList.forEachIndexed { index, eventSubscriber ->
            Row(
                modifier = Modifier.fill()
                    .padding(horizontal = 2f, vertical = 4f)
                    .bgHoverHighlightBox(),
                horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
            ) {
                val eventIndex = HSEventManager.subscriberList.indexOf(eventSubscriber)
                MoveButton(eventIndex, HSEventManager.subscriberList.lastIndex, {
                    HSEventManager.moveUp(eventIndex)
                    onChanged()
                }, {
                    HSEventManager.moveDown(eventIndex)
                    onChanged()
                })
                //text
                Row(
                    modifier = Modifier.weight(1),
                    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
                ) {
                    TextLabel(
                        eventSubscriber.eventType.translateText.withColor(Colors.DARK_GOLD),
                        Modifier.hoverText(eventSubscriber.eventType.translateComment)
                    )
                    TextLabel(Literal("=>").withColor(Colors.LIME))
                    TextLabel(InlineStyleText(eventSubscriber.name))
                }
                //enabled
                SwitchButton(mutableStateOf(eventSubscriber::enabled), modifier = Modifier.hoverText(HSLang.enable))
                //edit
                EditButton {
                    EventSubscriberEditor(eventSubscriber) {
                        eventSubscriber.fromEventSubscriber(it)
                        onChanged()
                    }.open()
                }
                //delete
                RemoveButton {
                    HSEventManager.remove(eventSubscriber)
                    onChanged()
                }
            }
        }
    }.apply {
        filterList.subscribe {
            executeRecompose()
        }
    }
}

fun EventSubscriberEditor(
    eventSubscriber: HSEventSubscriber,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    newEventSubscriberConsumer: (HSEventSubscriber) -> Unit
): IGScreenImpl {
    var name = eventSubscriber.name
    val eventType = mutableStateOf(eventSubscriber.eventType)
    val executorType = mutableStateOf(eventSubscriber.executorType)
    val enabled = mutableStateOf(eventSubscriber.enabled)

    val task = eventSubscriber.executor as? HSTickTask ?: HSTickTask.empty

    var executor = when (eventSubscriber.executorType) {
        Command  -> (eventSubscriber.executor as CommandExecutor).asString
        Message  -> (eventSubscriber.executor as MessageExecutor).asString
        Script   -> (eventSubscriber.executor as ScriptExecutor).asString
        TickTask -> task.executor.asString
    }

    val taskDelay = mutableStateOf(task.setting.delay)
    val taskPeriod = mutableStateOf(task.setting.period)
    val taskTimes = mutableStateOf(task.setting.times)
    val taskExecuteOn = mutableStateOf(task.executeOn)
    val taskExecutorType = mutableStateOf(task.executorType)

    val isTaskEditor = mutableStateOf(eventSubscriber.executorType == TickTask)
    executorType.subscribe {
        isTaskEditor.setValue(it == TickTask)
    }

    var nameEditorTransform: (() -> Transform)? = null
    return Dialog(
        Modifier.maxHeight(280f).width(380f).then(modifier),
        Modifier.onClose {
            TipHandler.popTip("#HSEVENT_MANAGER")
        }.then(screenModifier)
    ) {
        //title
        TextLabel(HSLang.eventSubscriberEditor)

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
                        .hoverText(HSLang.eventSubscriberName)
                ) {
                    text = name
                    textConsumer {
                        name = it
                    }
                    nameEditorTransform = { this.owner().transform }
                }
                //enabled
                SwitchButton(enabled, Modifier.hoverText(HSLang.enable))
                //eventType
                EventSelector(HSEventManager.subscribableEvents, eventType, modifier = Modifier.weight(3))
                //executorType
                EnumSelector(executorType, ExecutorType.entries, modifier = Modifier.weight(2))
            }
            SwitchableProxy(
                {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5f)
                    ) {
                        //delay
                        IntEditor(taskDelay, 0..999, modifier = Modifier.weight(1).hoverText(HSLang.taskDelay), editorModifier = { Modifier.weight(1) })
                        //period
                        IntEditor(taskPeriod, 1..999, modifier = Modifier.weight(1).hoverText(HSLang.taskPeriod), editorModifier = { Modifier.weight(1) })
                        //times
                        IntEditor(taskTimes, 1..999, modifier = Modifier.weight(1).hoverText(HSLang.taskTimes), editorModifier = { Modifier.weight(1) })
                        //executeOn
                        EnumSelector(taskExecuteOn, modifier = Modifier.weight(2).hoverText(HSLang.taskExecuteOn))
                        //executorType
                        EnumSelector(
                            taskExecutorType,
                            options = HSTickTask.ExecutorType.entries,
                            modifier = Modifier.weight(2).hoverText(HSLang.taskExecutorType)
                        )
                    }
                },
                { Widget(Modifier.margin(top = -3).size(0f, 0f)) },
                isTaskEditor
            )
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
                TextLabel(IGLang.confirm)
                click {
                    if (name.isEmpty()) {
                        TipHandler.pushTip(
                            "#HSEVENT_MANAGER",
                            4.seconds,
                            nameEditorTransform!!,
                            Tip { TextLabel(HSLang.cantBeEmpty(HSLang.eventSubscriberName).withColor(Colors.RED)) })
                        return@click
                    }
                    val _executor = when (executorType.getValue()) {
                        Command  -> CommandExecutor(executor)
                        Message  -> MessageExecutor(executor)
                        Script   -> ScriptExecutor(executor)
                        TickTask -> HSTickTask(
                            name = name,
                            setting = TickTaskSetting(
                                delay = taskDelay.getValue(),
                                period = taskPeriod.getValue(),
                                times = taskTimes.getValue()
                            ),
                            executeOn = taskExecuteOn.getValue(),
                            executorType = taskExecutorType.getValue(),
                            executor = taskExecutorType.getValue().fromString(executor)
                        )
                    }
                    newEventSubscriberConsumer(
                        HSEventSubscriber(
                            name = name,
                            enabled = enabled.getValue(),
                            eventType = eventType.getValue(),
                            executorType = executorType.getValue(),
                            executor = _executor
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