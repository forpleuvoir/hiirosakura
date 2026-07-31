package moe.forpleuvoir.hiirosakura.functional.event.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JexlSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightDefaults
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import kotlin.enums.enumEntries


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                        OutlinedTextField(
                            name,
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = { Text(HSLang.Task.name) },
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedLabelBox(
                            label = { Text(HSLang.Common.enable) },
                            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 4.dp),
                            modifier = Modifier.height(66.5.dp)
                        ) {
                            Switch(enabled, onCheckedChange = { enabled = it })
                        }
                    }
                    EventTypeSelector(eventType, { eventType = it }, modifier = Modifier.weight(1f), label = {
                        Text(HSLang.Event.eventType)
                    })
                    EnumSelector(type, { type = it }, enumEntries(), modifier = Modifier.weight(1f), label = {
                        Text(HSLang.Task.executorType)
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
                                IntField(
                                    delay, { delay = it }, range = 0..1141514, modifier = Modifier.weight(1f),
                                    labelPosition = TextFieldLabelPosition.Attached(true),
                                    label = { Text(HSLang.Task.delay) }
                                )
                                IntField(
                                    period, { period = it }, range = 1..1141514, modifier = Modifier.weight(1f),
                                    labelPosition = TextFieldLabelPosition.Attached(true),
                                    label = { Text(HSLang.Task.period) }
                                )
                                IntField(
                                    times, { times = it }, range = 1..1141514, modifier = Modifier.weight(1f),
                                    labelPosition = TextFieldLabelPosition.Attached(true),
                                    label = { Text(HSLang.Task.times) }
                                )
                            }
                        }

                        EnumSelector(
                            executeOn, { executeOn = it }, enumEntries(),
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = {
                                Text(HSLang.Task.executeOn)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        EnumSelector(
                            executorType, { executorType = it }, enumEntries(),
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = {
                                Text(HSLang.Task.executorType)
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
                    OutlinedTextField(
                        state,
                        modifier = Modifier
                            .fillMaxSize()
                            .codeEditorShortcuts(state),
                        outputTransformation = rememberSyntaxHighlightTransformation(JexlSyntaxLanguage, SyntaxHighlightDefaults.theme(), state.text.toString()),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        scrollState = scrollState,
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(end = 24.dp),
                    )

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(vertical = 12.dp),
                        adapter = rememberScrollbarAdapter(scrollState)
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
                Text(HSLang.Task.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(HSLang.Task.cancel)
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTypeSelector(
    selected: String,
    onSelect: (String) -> Unit,
    items: List<String> = EventTypes.ids,
    itemEquals: (String, String) -> Boolean = { a, b -> a == b },
    content: @Composable (String) -> Unit = {
        Text(EventTypes.id2Text(it), modifier = Modifier.plainTooltip {
            Text(EventTypes.id2TextComment(it))
        })
    },
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(true),
    label: @Composable (() -> Unit)? = null,
    itemContent: @Composable (String, Boolean) -> Unit = { item, _ ->
        Text(EventTypes.id2Text(item), modifier = Modifier.plainTooltip {
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
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
) = StringSelector(
    selected = selected,
    onSelect = onSelect,
    items = items,
    itemEquals = itemEquals,
    content = content,
    labelPosition = labelPosition,
    label = label,
    itemContent = itemContent,
    enabled = enabled,
    searchFilter = searchFilter,
    modifier = modifier,
    itemLeadingIcon = itemLeadingIcon,
    itemTrailingIcon = itemTrailingIcon,
    textStyle = textStyle,
    interactionSource = interactionSource,
    shape = shape,
    colors = colors,
    contentPadding = contentPadding,
)