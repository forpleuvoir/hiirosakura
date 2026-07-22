package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.KeybindTickTask
import moe.forpleuvoir.hiirosakura.functional.task.QuickTickTaskExecuteScreen.hitAlignment
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.rememberRadialMenuState
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.input.MouseButton.*
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.closeScreen
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.ui.util.toComposeColor
import moe.forpleuvoir.ibukigourd.util.moveElement
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.world.item.ItemStack

class CustomRadialMenu(
    shortcuts: Keybind = Keybind(
        defaultSetting = KeybindSetting(
            passthrough = true,
            strict = false,
            trigger = KeyTriggerTiming.Press,
        )
    ),
    var setting: RadialMenuSetting = RadialMenuSetting(),
    tasks: List<IconTickTask>
) {

    companion object : Codec<CustomRadialMenu> {
        override fun deserialization(data: SerializeElement): Result<CustomRadialMenu> = DeserializationException.runCatching {
            data.checkType<SerializeObject, CustomRadialMenu> {
                CustomRadialMenu(
                    Keybind(
                        defaultSetting = KeybindSetting(
                            passthrough = true,
                            strict = false,
                            trigger = KeyTriggerTiming.Press,
                        )
                    ).apply { deserialization(it["shortcuts"]!!) },
                    RadialMenuSetting.deserialization(it.requireKey("setting")).getOrThrow(),
                    it.requireKey("tasks").requireType<SerializeArray>().map { task -> IconTickTask.deserialization(task).getOrThrow() }
                )
            }
        }

        override fun serialization(target: CustomRadialMenu): SerializeElement = SerializeObject.build {
            "shortcuts" to target.shortcuts.serialization()
            "setting" to RadialMenuSetting.serialization(target.setting)
            "tasks" arr {
                target.tasks.forEach { add(it.value.serialization()) }
            }
        }

    }

    val shortcuts: Keybind = shortcuts.apply { action = { open() } }

    private var nextTaskKey: Long = 0

    val tasks: List<Keyed<IconTickTask>>
        field = mutableStateListOf()

    init {
        this.tasks.addAll(tasks.map { Keyed(nextTaskKey++, it) })
    }

    fun addTask(task: IconTickTask) {
        tasks.add(Keyed(nextTaskKey++, task))
    }

    fun updateTask(index: Int, task: IconTickTask) {
        tasks.getOrNull(index)?.let {
            tasks[index] = it.copyValue(task)
        }
    }

    fun removeTaskAt(index: Int): Keyed<IconTickTask> =
        tasks.removeAt(index)

    fun moveTask(fromIndex: Int, toIndex: Int) {
        tasks.moveElement(fromIndex, toIndex)
    }

    fun load() {
        InputHandler.register(shortcuts)
    }

    fun unload() {
        InputHandler.unregister(shortcuts)
    }

    fun open() = openComposeScreen {
        IbukiGourdTheme {
            Box(Modifier.fillMaxSize()) {
                val s = setting
                var editorState by remember { mutableStateOf<TaskEditorState?>(null) }
                var deletingTask by remember { mutableStateOf<Keyed<IconTickTask>?>(null) }
                RadialMenu(
                    options = tasks,
                    state = rememberRadialMenuState(),
                    innerRadius = s.innerRadius.dp,
                    outerRadius = s.outerRadius.dp,
                    optionRadius = s.optionRadius.dp,
                    startAngleDegree = -90f,
                    gap = s.gap,
                    cornerRadius = s.cornerRadius.dp,
                    optionsPerPage = s.pageSize,
                    idleOuterColor = s.outerColor.toComposeColor,
                    idleInnerColor = s.innerColor.toComposeColor,
                    idleBorderColor = s.borderColor.toComposeColor,
                    selectedOuterColor = s.outerSelectedColor.toComposeColor,
                    selectedInnerColor = s.innerSelectedColor.toComposeColor,
                    selectedBorderColor = s.selectedBorderColor.toComposeColor,
                    borderWidth = s.borderWidth.dp,
                    onOptionClick = { keyed, button ->
                        when (button) {
                            LEFT -> keyed?.let {
                                it.value.execute()
                                closeScreen()
                            }

                            MIDDLE -> keyed?.let { deletingTask = it }

                            // 右键选项编辑，右键空选区新建
                            RIGHT -> editorState =
                                TaskEditorState(keyed?.key, keyed?.value ?: KeybindTickTask.empty)

                            else -> Unit
                        }
                    },
                    onEmptyClick = { button ->
                        if (button != LEFT) closeScreen()
                    },
                    optionContent = { task, _ ->
                        val stack = remember(task.value.icon) { ItemStack(task.value.icon) }
                        if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
                    },
                    centerContent = { task ->
                        if (task != null) Text(task.value.nameAsInlineStyleText, color = Color.White)
                        else Text(HSLang.Task.unSelected, color = Color.White)
                    },
                )

                Text(HSLang.Task.quickExecuteHint, Modifier.align(hitAlignment), color = Color.White)

                // 删除确认
                deletingTask?.let { keyed ->
                    SimpleAlertDialog(
                        onDismissRequest = { deletingTask = null },
                        onConfirmRequest = { true },
                        title = { Text(HSLang.Common.deleteConfirm(keyed.value.name)) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val index = TaskManager.taskList.indexOfFirst { it.key == keyed.key }
                                    if (index >= 0) TaskManager.removeAt(index)
                                    deletingTask = null
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError,
                                )
                            ) {
                                Text(IGLang.Misc.confirm)
                            }
                        },
                    )
                }

                // 编辑 / 新建
                editorState?.let { state ->
                    TaskEditorDialog(
                        task = state.task,
                        title = { if (state.key == null) Text(HSLang.Task.newTask) else Text(HSLang.Task.editTask) },
                        onDismissRequest = { editorState = null },
                    ) { newTask ->
                        val key = state.key
                        if (key == null) {
                            addTask(newTask)
                        } else {
                            val index = tasks.indexOfFirst { it.key == key }
                            if (index >= 0) updateTask(index, newTask) else addTask(newTask)
                        }
                    }
                }
            }

        }
    }
}

private class TaskEditorState(
    val key: Long?,
    val task: IconTickTask,
)