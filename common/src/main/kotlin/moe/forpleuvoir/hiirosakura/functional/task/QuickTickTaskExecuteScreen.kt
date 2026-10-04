package moe.forpleuvoir.hiirosakura.functional.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskEditorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenuDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.rememberRadialMenuState
import moe.forpleuvoir.ibukigourd.config.item.configKeybind
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.input.MouseButton.*
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.ibukigourd.util.toNebulaColor
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configColor
import moe.forpleuvoir.nebula.config.item.configFloat
import moe.forpleuvoir.nebula.config.item.configInt
import net.minecraft.world.item.ItemStack
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen
import moe.forpleuvoir.compose_minecraft.platform.screen.ComposeScreen

object QuickTickTaskExecuteScreen : ConfigGroup("quick_tick_task_execute") {

    val keybind by configKeybind(
        "keybind", Keybind(
            defaultSetting = KeybindSetting(
                passthrough = true,
                strict = false,
                trigger = KeyTriggerTiming.Press,
            )
        ) {
            open()
        })

    val innerColor by configColor("inner_color", RadialMenuDefaults.IdleInnerColor.toNebulaColor())

    val outerColor by configColor("outer_color", RadialMenuDefaults.IdleOuterColor.toNebulaColor())

    val idleBorderColor by configColor("idle_border_color", RadialMenuDefaults.IdleBorderColor.toNebulaColor())

    val innerSelectedColor by configColor("inner_selected_color", RadialMenuDefaults.SelectedInnerColor.toNebulaColor())

    val outerSelectedColor by configColor("outer_selected_color", RadialMenuDefaults.SelectedOuterColor.toNebulaColor())

    val selectedBorderColor by configColor("selected_border_color", RadialMenuDefaults.SelectedBorderColor.toNebulaColor())

    val iconScale by configFloat("icon_scale", 1.3f, 0.2f, 2f)

    val innerRadius by configFloat("inner_radius", RadialMenuDefaults.InnerRadius.value, 40f, 300f)

    val outerRadius by configFloat("outer_radius", RadialMenuDefaults.OuterRadius.value, 150f, 500f)

    val optionRadius by configFloat("option_radius", RadialMenuDefaults.OptionRadius.value, 100f, 450f)

    val gapDistance by configFloat("gap_distance", RadialMenuDefaults.GAP, 0.5f, 20f)

    val cornerRadius by configFloat("corner_radius", RadialMenuDefaults.CornerRadius.value, 0f, 20f)

    val borderWidth by configFloat("border_width", RadialMenuDefaults.BorderWidth.value, 0f, 10f)

    val tiltDegree by configFloat("tilt_degree", RadialMenuDefaults.TILT_DEGREE, 0f, 45f)

    val singlePageMaxCount by configInt("single_page_max_count", RadialMenuDefaults.OPTIONS_PRE_PAGE, 4, 12)

    internal val hitAlignment = BiasAlignment(0f, 0.85f)

    /** 打开快捷任务执行轮盘。
     *
     * 菜单内容为 [TaskManager.taskList] 中的任务，只有悬浮在扇区上才会触发动作：
     * 左键直接执行并关闭屏幕；中键删除（需确认）；右键选项编辑，右键空选区新建；
     * 右键扇区以外的地方（中心、环外）关闭屏幕。
     */
    fun open(): ComposeScreen = openComposeScreen {
        SokitsuTheme {
            Box(Modifier.fillMaxSize()) {
                var editorState by remember { mutableStateOf<TaskEditorState?>(null) }

                var deletingTask by remember { mutableStateOf<Keyed<KeybindTickTask>?>(null) }
                RadialMenu(
                    // 每次重组复制一份列表，保证增删任务后 RadialMenu 内部 remember(options) 能及时失效
                    options = TaskManager.taskList.toList(),
                    state = rememberRadialMenuState(),
                    innerRadius = innerRadius.dp,
                    outerRadius = outerRadius.dp,
                    optionRadius = optionRadius.dp,
                    gap = gapDistance,
                    cornerRadius = cornerRadius.dp,
                    optionsPerPage = singlePageMaxCount,
                    idleOuterColor = outerColor.toComposeColor(),
                    idleInnerColor = innerColor.toComposeColor(),
                    idleBorderColor = idleBorderColor.toComposeColor(),
                    selectedOuterColor = outerSelectedColor.toComposeColor(),
                    selectedInnerColor = innerSelectedColor.toComposeColor(),
                    selectedBorderColor = selectedBorderColor.toComposeColor(),
                    borderWidth = borderWidth.dp,
                    tiltDegree = tiltDegree,
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
                    // 右键扇区以外的地方（中心、环外）关闭屏幕
                    onEmptyClick = { button ->
                        if (button != LEFT) closeScreen()
                    },
                    optionContent = { keyed, _ ->
                        val stack = remember(keyed.value.icon) { ItemStack(keyed.value.icon) }

                        if (!stack.isEmpty) ItemIcon(stack, modifier = Modifier.scale(iconScale), showTooltip = false, scaleOnHover = 1f)
                    },
                    centerContent = { keyed ->
                        if (keyed != null) Text(keyed.value.nameAsInlineStyleText, color = Color.White)
                        else Text(component = HSLang.Task.unSelected, color = Color.White)
                    },
                )

                Text(component = HSLang.Task.quickExecuteHint, Modifier.align(hitAlignment), color = Color.White)

                // 删除确认
                deletingTask?.let { keyed ->
                    SimpleAlertDialog(
                        onDismissRequest = { deletingTask = null },
                        onConfirmRequest = { true },
                        title = { Text(component = HSLang.Common.deleteConfirm(keyed.value.name)) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val index = TaskManager.taskList.indexOfFirst { it.key == keyed.key }

                                    if (index >= 0) TaskManager.removeAt(index)
                                    deletingTask = null
                                },
                                colors = FlatButtonDefaults.colors(
                                    color = SokitsuTheme.colorScheme.error,
                                    contentColor = SokitsuTheme.colorScheme.onError,
                                )
                            ) {
                                Text(component = IGLang.Misc.confirm)
                            }
                        },
                    )
                }

                // 编辑 / 新建
                editorState?.let { state ->
                    TaskEditorDialog(
                        task = state.task,
                        title = { if (state.key == null) Text(component = HSLang.Task.newTask) else Text(component = HSLang.Task.editTask) },
                        onDismissRequest = { editorState = null },
                    ) { newTask ->
                        val key = state.key
                        if (key == null) {
                            TaskManager.add(newTask)
                        } else {
                            val index = TaskManager.taskList.indexOfFirst { it.key == key }

                            if (index >= 0) TaskManager.update(index, newTask) else TaskManager.add(newTask)
                        }
                    }
                }
            }
        }
    }
}

/**
 * @param key 被编辑任务在 [TaskManager.taskList] 中的 key，null 表示新建任务
 */
private class TaskEditorState(
    val key: Long?,
    val task: KeybindTickTask,
)
