package moe.forpleuvoir.hiirosakura.functional.event.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.enums.enumEntries
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.event.EventTypes
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType.Command
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType.Message
import moe.forpleuvoir.hiirosakura.functional.event.HSEventSubscriber.ExecutorType.Script
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.executor.CommandExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.MessageExecutor
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.hiirosakura.ui.widget.StringSelector
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextFieldDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip

/** 订阅编辑浮层的尺寸、列宽与字段取值范围。 */
private object EventEditorDefaults {

    /** 浮层内容宽度。 */
    val Width: Dp = 1280.dp

    /** 浮层内容高度。 */
    val Height: Dp = 720.dp

    /** 标签列宽度：各行标签列等宽，控件列因此逐行对齐。 */
    val LabelWidth: Dp = 140.dp

    /** 同一排里各控件的间距。 */
    val ColumnGap: Dp = 16.dp

    /** 浮层内各行之间的纵向间距。 */
    val RowSpacing: Dp = 12.dp

    /** 延迟 / 周期 / 次数三个字段的取值上限（按 20 tick/s 折算约 15 小时）。 */
    const val MAX_SETTING: Int = 1141514
}

/**
 * 事件订阅编辑浮层：名称 / 事件类型 / 执行器类型，任务型执行器另有执行时间、内层执行器类型与
 * 延迟 / 周期 / 次数；其余高度给内容编辑器。
 *
 * 每一排由若干「标签 + 控件」组成，标签列定宽、控件定宽，排内控件因此逐列对齐。
 * 是否启用由列表行的开关控制，浮层不提供该字段。
 */
@Composable
fun HSEventSubscriberEditorDialog(
    value: HSEventSubscriber,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onValueChange: (HSEventSubscriber) -> Unit,
) {
    val name = rememberTextFieldState(value.name)
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

    val tickTask = type == ExecutorType.TickTask

    /** 当前执行器类型对应的内容编辑器状态。 */
    val contentState = when (type) {
        Command               -> cmdContent
        Message               -> msgContent
        Script                -> scriptContent
        ExecutorType.TickTask -> taskContent
    }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = { true },
        content = {
            Column(
                modifier = Modifier.size(EventEditorDefaults.Width, EventEditorDefaults.Height),
                verticalArrangement = Arrangement.spacedBy(EventEditorDefaults.RowSpacing),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EventEditorDefaults.ColumnGap),
                ) {
                    EventLabeledRow(
                        label = { Text(component = HSLang.Task.name) },
                        modifier = Modifier.weight(1f),
                    ) {
                        TextField(name, modifier = Modifier.fillMaxWidth())
                    }
                    EventLabeledRow(
                        label = { Text(component = HSLang.Event.eventType) },
                        modifier = Modifier.weight(1f),
                    ) {
                        EventTypeSelector(
                            eventType, { eventType = it },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp),
                        )
                    }
                    EventLabeledRow(
                        label = { Text(component = HSLang.Task.executorType) },
                        modifier = Modifier.weight(1f),
                    ) {
                        EnumSelector(
                            type, { type = it }, enumEntries(),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp),
                        )
                    }
                }

                // 任务型执行器才有执行时间、内层执行器类型与调度参数
                if (tickTask) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EventEditorDefaults.ColumnGap),
                    ) {
                        EventLabeledRow(
                            label = { Text(component = HSLang.Task.executeOn) },
                            modifier = Modifier.weight(1f),
                        ) {
                            EnumSelector(
                                executeOn, { executeOn = it }, enumEntries(),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp),
                            )
                        }
                        EventLabeledRow(
                            label = { Text(component = HSLang.Task.executorType) },
                            modifier = Modifier.weight(1f),
                        ) {
                            EnumSelector(
                                executorType, { executorType = it }, enumEntries(),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp),
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EventEditorDefaults.ColumnGap),
                    ) {
                        EventLabeledRow(
                            label = { Text(component = HSLang.Task.delay) },
                            modifier = Modifier.weight(1f),
                        ) {
                            IntField(
                                delay,
                                { delay = it },
                                valueRange = 0..EventEditorDefaults.MAX_SETTING,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        EventLabeledRow(
                            label = { Text(component = HSLang.Task.period) },
                            modifier = Modifier.weight(1f),
                        ) {
                            IntField(
                                period,
                                { period = it },
                                valueRange = 1..EventEditorDefaults.MAX_SETTING,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        EventLabeledRow(
                            label = { Text(component = HSLang.Task.times) },
                            modifier = Modifier.weight(1f),
                        ) {
                            IntField(
                                times,
                                { times = it },
                                valueRange = 1..EventEditorDefaults.MAX_SETTING,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                // 内容编辑器：吃掉剩余高度做多行编辑
                TextField(
                    contentState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .codeEditorShortcuts(contentState),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    contentPadding = TextFieldDefaults.contentPadding(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val result = HSEventSubscriber(
                    name = name.text.toString(),
                    enabled = value.enabled,
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

/**
 * 一行「标签 + 控件」：标签列固定 [EventEditorDefaults.LabelWidth]，控件宽度由 [content] 决定
 * （同一排里通常用 `Modifier.weight(1f)` 平分剩余宽度）。
 *
 * 标签列等宽是本组件的唯一职责 —— 同一排里各个控件因此落在同一列上。
 */
@Composable
private fun EventLabeledRow(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(EventEditorDefaults.ColumnGap),
    ) {
        val rowScope = this
        Box(
            modifier = Modifier.width(EventEditorDefaults.LabelWidth),
            contentAlignment = Alignment.CenterStart,
        ) {
            label()
        }
        rowScope.content()
    }
}

/**
 * 事件类型选择器：把 id 列表包成带文案的选择器。
 *
 * @param selected 当前事件类型 id
 * @param onSelect 选中回调
 * @param content 触发件里显示的当前值
 * @param label 标签；为 null 时只有触发件（标签由调用方自己排）
 */
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
    contentPadding: PaddingValues = TextFieldDefaults.contentPadding(),
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
