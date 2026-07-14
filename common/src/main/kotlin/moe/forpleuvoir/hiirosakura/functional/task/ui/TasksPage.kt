package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.KeybindTickTask
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.ui.icon.default.PlayArrow
import moe.forpleuvoir.hiirosakura.ui.widget.ItemSelector
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.enums.enumEntries

@Composable
internal fun TasksPage(modifier: Modifier) {

    Column(modifier.padding(16.dp).fillMaxSize()) {
        var showEditor by remember { mutableStateOf(false) }
        var editingTaskIndex by remember { mutableStateOf(-1) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(HSLang.Task.tasks, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(onClick = {
                    runCatching {
                        TaskManager.reBindKey()
                    }.onSuccess {
                        ToastHandler.showContent { Text(HSLang.Common.success) }
                    }.onFailure {
                        ToastHandler.showContent { Text(it.message ?: "") }
                    }
                }) {
                    Text(HSLang.Task.reBindKey, Modifier.plainTooltip { Text(HSLang.Task.reBindKeyComment) })
                }

                FilledTonalButton(onClick = {
                    editingTaskIndex = -1
                    showEditor = true
                }) {
                    Text(HSLang.Task.newTask)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (TaskManager.taskList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                TaskManager.moveElement(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize(), state = lazyListState) {
                itemsIndexed(TaskManager.taskList, key = { _, keyed -> keyed.key }) { index, task ->
                    ReorderableItem(reorderableLazyListState, task.key) { isDragging ->
                        val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                        val handleInteraction = remember { MutableInteractionSource() }
                        val handleHovered by handleInteraction.collectIsHoveredAsState()
                        Card(
                            modifier = Modifier.fillMaxWidth().scale(scale)
                        ) {
                            Row(
                                Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    DragHandle(
                                        hapticFeedback,
                                        handleInteraction,
                                        handleHovered,
                                        isDragging
                                    )
                                    val icon = remember(task.value.icon) { ItemStack(task.value.icon) }
                                    if (!icon.isEmpty) {
                                        ItemIcon(icon, showTooltip = false)
                                    }
                                    Text(InlineStyleText(task.value.name))
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton({
                                        task.value.execute()
                                    }, Modifier.plainTooltip {
                                        Text(HSLang.Task.execute)
                                    }) {
                                        Icon(Icons.PlayArrow, null)
                                    }

                                    KeybindAssistChip(task.value.keybind, modifier = Modifier.width(280.dp))

                                    IconButton({
                                        showEditor = true
                                        editingTaskIndex = index
                                    }, Modifier.plainTooltip {
                                        Text(HSLang.Task.editTask)
                                    }) {
                                        Icon(Icons.EditNote, null)
                                    }

                                    RemoveConfirmButton(
                                        task.value.name,
                                        { TaskManager.removeAt(index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showEditor) {
            ScriptEditorDialog(
                task = TaskManager.taskList.values().getOrElse(editingTaskIndex) { KeybindTickTask.empty },
                title = { if (editingTaskIndex == -1) Text(HSLang.Task.newTask) else Text(HSLang.Task.editTask.plainText) },
                onDismissRequest = { showEditor = false },
            ) { newTask ->
                if (editingTaskIndex == -1) {
                    TaskManager.add(newTask)
                } else {
                    TaskManager.update(editingTaskIndex, newTask)
                }
            }
        }
    }

}

@Composable
fun <T : HSTickTask> ScriptEditorDialog(
    task: T,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onTaskChange: (T) -> Unit,
) {

    val name = rememberTextFieldState(task.name)
    var delay by remember { mutableIntStateOf(0) }
    var period by remember { mutableIntStateOf(1) }
    var times by remember { mutableIntStateOf(1) }
    var executeOn by remember { mutableStateOf(task.executeOn) }
    var executorType by remember { mutableStateOf(task.executorType) }

    val executor = rememberTextFieldState(task.executor.asString())

    var icon by remember { mutableStateOf(if (task is IconTickTask) task.icon else null) }

    val keybind = if (task is KeybindTickTask) task.keybind else null

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
                    icon?.let {
                        ItemSelector(it, { newIcon -> icon = newIcon }, false, Modifier.plainTooltip {
                            Text(HSLang.Task.icon)
                        })
                    }
                    OutlinedTextField(name, labelPosition = TextFieldLabelPosition.Attached(true), label = { Text(HSLang.Task.name) })
                    CompositionLocalProvider(LocalNumberFieldStyle provides NumberFieldStyle.Outlined) {
                        IntField(
                            delay, { delay = it }, range = 0..1141514, modifier = Modifier.width(120.dp),
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = { Text(HSLang.Task.delay) }
                        )
                        IntField(
                            period, { period = it }, range = 1..1141514, modifier = Modifier.width(120.dp),
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = { Text(HSLang.Task.period) }
                        )
                        IntField(
                            times, { times = it }, range = 1..1141514, modifier = Modifier.width(120.dp),
                            labelPosition = TextFieldLabelPosition.Attached(true),
                            label = { Text(HSLang.Task.times) }
                        )
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
                //实际内容编辑器

                //TODO替换成 脚本编辑器
                Box {
                    val scrollState = rememberScrollState()
                    OutlinedTextField(
                        executor,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                        labelPosition = TextFieldLabelPosition.Attached(true),
                        label = { Text(executorType.translateText) },
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
                val newTasks = when (task) {
                    is KeybindTickTask -> KeybindTickTask(
                        name = name.text.toString(),
                        setting = TickTask.Setting(delay, period, times),
                        executeOn = executeOn,
                        executorType = executorType,
                        icon = icon!!,
                        keybind = keybind!!,
                        executor = executorType.fromString(executor.text.toString())
                    )

                    is IconTickTask    -> IconTickTask(
                        name = name.text.toString(),
                        setting = TickTask.Setting(delay, period, times),
                        executeOn = executeOn,
                        executorType = executorType,
                        icon = icon!!,
                        executor = executorType.fromString(executor.text.toString())
                    )

                    else               -> HSTickTask(
                        name = name.text.toString(),
                        setting = TickTask.Setting(delay, period, times),
                        executeOn = executeOn,
                        executorType = executorType,
                        executor = executorType.fromString(executor.text.toString())
                    )
                }
                @Suppress("UNCHECKED_CAST")
                onTaskChange(newTasks as T)
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
