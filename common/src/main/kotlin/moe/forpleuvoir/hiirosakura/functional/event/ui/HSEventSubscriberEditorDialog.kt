package moe.forpleuvoir.hiirosakura.functional.event.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.event.EventTypes
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType.*
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.ibukigourd.task.TickTask
import kotlin.enums.enumEntries
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.hiirosakura.ui.widget.LabeledFieldDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.StringSelector
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.layout.fillMaxWidth
import moe.forpleuvoir.hiirosakura.ui.compat.NumberFieldStyle
import moe.forpleuvoir.hiirosakura.ui.compat.LocalNumberFieldStyle


@Composable
fun HSEventSubscriberEditorDialog(
    value: HSEventSubscriber,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onValueChange: (HSEventSubscriber) -> Unit,
) {
    val name = rememberTextFieldState(value.name)
    var enabled by remember { mutableStateOf(value.enabled) }

    var eventType by remember { mutableStateOf(value.eventTypeId) }

    var type by remember { mutableStateOf(value.executorType) }


    val default = remember { HSTickTask.empty }


    var delay by remember { mutableIntStateOf((value.executor as? HSTickTask)?.setting?.delay ?: default.setting.delay) }

    var period by remember { mutableIntStateOf((value.executor as? HSTickTask)?.setting?.period ?: default.setting.period) }

    var times by remember { mutableIntStateOf((value.executor as? HSTickTask)?.setting?.times ?: default.setting.times) }

    var executeOn by remember { mutableStateOf((value.executor as? HSTickTask)?.executeOn ?: default.executeOn) }

    var executorType by remember { mutableStateOf((value.executor as? HSTickTask)?.executorType ?: default.executorType) }


    val cmdContent = rememberTextFieldState((value.executor as? CommandExecutor)?.command ?: "")
    val msgContent = rememberTextFieldState((value.executor as? MessageExecutor)?.message ?: "")
    val scriptContent = rememberTextFieldState((value.executor as? ScriptExecutor)?.asString() ?: "")
    val taskContent = rememberTextFieldState((value.executor as? HSTickTask)?.executor?.asString() ?: "")

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = { true },
        content = {
            Column(
                modifier = Modifier.size(1280.dp, 720.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        Modifier.weight(1.5f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabelBox(
                            label = { Text(component = HSLang.Task.name) },
                            modifier = Modifier.weight(1f),
                        ) {
                            TextField(
                                name,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        LabelBox(
                            label = { Text(component = HSLang.Common.enable) },
                            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 4.dp),
                            modifier = Modifier.height(66.5.dp)
                        ) {
                            Switch(enabled, onCheckedChange = { enabled = it })
                        }
                    }
                    EventTypeSelector(eventType, { eventType = it }, modifier = Modifier.weight(1f), label = {
                        Text(component = HSLang.Event.eventType)
                    })
                    EnumSelector(type, { type = it }, enumEntries(), modifier = Modifier.weight(1f), label = {
                        Text(component = HSLang.Task.executorType)
                    })
                }


                if (type == ExecutorType.TickTask) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            Modifier.weight(1.5f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                                LabelBox(label = { Text(component = HSLang.Task.delay) }, modifier = Modifier.weight(1f)) {
                                    IntField(
                                        delay,
                                        { delay = it },
                                        valueRange = 0..1141514,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                                                                LabelBox(label = { Text(component = HSLang.Task.period) }, modifier = Modifier.weight(1f)) {
                                    IntField(
                                        period,
                                        { period = it },
                                        valueRange = 1..1141514,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                                                                LabelBox(label = { Text(component = HSLang.Task.times) }, modifier = Modifier.weight(1f)) {
                                    IntField(
                                        times,
                                        { times = it },
                                        valueRange = 1..1141514,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }

                        EnumSelector(
                            executeOn, { executeOn = it }, enumEntries(),
                            label = {
                                Text(component = HSLang.Task.executeOn)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        EnumSelector(
                            executorType, { executorType = it }, enumEntries(),
                            label = {
                                Text(component = HSLang.Task.executorType)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                //实际内容编辑器

                //TODO替换成 脚本编辑器
                Box {
                    val scrollState = rememberScrollState()
                    val state = when (type) {
                        Command               -> cmdContent
                        Message               -> msgContent
                        Script                -> scriptContent
                        ExecutorType.TickTask -> taskContent
                    }
                    TextField(
                        state,
                        modifier = Modifier .fillMaxSize() .codeEditorShortcuts(state),
                        
                        contentPadding = LabeledFieldDefaults.contentPadding(end = 24.dp),
                    )

                    VerticalScroller(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(vertical = 12.dp),
                        adapter = rememberScrollerAdapter(scrollState)
                    )
                }

            }
        },
        confirmButton = {
            Button(onClick = {
                val result = HSEventSubscriber(
                    name = name.text.toString(),
                    enabled = enabled,
                    eventTypeId = eventType,
                    executorType = type,
                    executor = when (type) {
                        Command               -> CommandExecutor(cmdContent.text.toString())
                        Message               -> MessageExecutor(msgContent.text.toString())
                        Script                -> ScriptExecutor(scriptContent.text.toString())
                        ExecutorType.TickTask -> HSTickTask(
                            name = name.text.toString(),
                            setting = TickTask.Setting(delay, period, times),
                            executeOn = executeOn,
                            executorType = executorType,
                            executor = executorType.fromString(taskContent.text.toString())
                        )
                    }
                )
                onValueChange(result)
                onDismissRequest()
            }) {
                Text(component = HSLang.Task.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = HSLang.Task.cancel)
            }
        }
    )
}


@Composable
fun EventTypeSelector(
    selected: String,
    onSelect: (String) -> Unit,
    items: List<String> = EventTypes.ids,
    itemEquals: (String, String) -> Boolean = { a, b -> a == b },
    content: @Composable (String) -> Unit = {
        Text(EventTypes.id2Text(it), modifier = Modifier.tooltip {
            Text(EventTypes.id2TextComment(it))
        })
    },
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (String, Boolean) -> Unit = { item, _ ->
        Text(EventTypes.id2Text(item), modifier = Modifier.tooltip {
            Text(EventTypes.id2TextComment(item))
        })
    },
    enabled: Boolean = true,
    searchFilter: ((String, String) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    itemLeadingIcon: ((Boolean) -> (@Composable (String) -> Unit)?)? = null,
    itemTrailingIcon: ((Boolean) -> (@Composable (String) -> Unit)?)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = LabeledFieldDefaults.contentPadding(),
) = StringSelector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    
    contentPadding = contentPadding,
)