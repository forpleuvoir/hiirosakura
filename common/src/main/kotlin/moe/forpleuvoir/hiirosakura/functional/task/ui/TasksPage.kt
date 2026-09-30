package moe.forpleuvoir.hiirosakura.functional.task.ui

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.enums.enumEntries
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.HSTickTask
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.KeybindTickTask
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.icon.Play
import moe.forpleuvoir.hiirosakura.ui.util.canScroll
import moe.forpleuvoir.hiirosakura.ui.widget.EnumSelector
import moe.forpleuvoir.hiirosakura.ui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.ui.widget.LocalItemIconVanillaSize
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.task.TickTask
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSetButton
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSettingSetButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.values
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/** 任务行的尺寸：固定列按内容的自然尺寸给，名称与执行器两列按权重分剩余宽度。 */
private object TaskRowDefaults {

    /** 图标格：容下一个物品图标。 */
    val IconColumn: Dp = 56.dp

    /** 快捷键控件宽度：与配置页里快捷键行的控件宽度一致。 */
    val KeybindWidth: Dp = 280.dp

    /** 行内边距。 */
    val RowPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp)

    /** 行内元素间距。 */
    val ItemSpacing: Dp = 8.dp

    /** 相邻两行的间距。 */
    val RowSpacing: Dp = 2.dp
}

/** 任务编辑浮层的尺寸、列宽与字段取值范围。 */
private object TaskEditorDefaults {

    /** 浮层内容宽度。 */
    val Width: Dp = 1280.dp

    /** 浮层内容高度。 */
    val Height: Dp = 720.dp

    /** 属性区网格的列间距。 */
    val ColumnGap: Dp = 16.dp

    /** 属性区网格的列数：图标内嵌在名称格里，其余六项正好排成两行三列。 */
    const val COLUMNS: Int = 3

    /** 标签列宽度：各行标签列等宽，控件列因此逐行对齐（按最长的「执行器类型」取）。 */
    val LabelWidth: Dp = 128.dp

    /** 一列的宽度：三列 + 两个列间距正好铺满内容宽度，两行的控件因此等宽。 */
    val ColumnWidth: Dp get() = (Width - ColumnGap * (COLUMNS - 1)) / COLUMNS

    /** 任务名称格里的图标尺寸。 */
    val IconSize: DpSize = DpSize(48.dp, 48.dp)

    /** 两行属性之间的纵向间距。 */
    val RowSpacing: Dp = 12.dp

    /** 延迟 / 周期 / 次数三个字段的取值上限（按 20 tick/s 折算约 15 小时）。 */
    const val MAX_SETTING: Int = 1141514
}

@Composable
internal fun TasksPage(modifier: Modifier) {
    // null = 不显示编辑浮层；-1 = 新建；其余为 taskList 下标
    var editingTaskIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(component = HSLang.Task.tasks)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = {
                    runCatching {
                        TaskManager.reBindKey()
                    }.onSuccess {
                        ToastHandler.showContent { Text(component = HSLang.Common.success) }
                    }.onFailure {
                        ToastHandler.showContent { Text(it.message ?: "") }
                    }
                }) {
                    Icon(Icons.SyncAlt)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        component = HSLang.Task.reBindKey,
                        modifier = Modifier.tooltip { Text(component = HSLang.Task.reBindKeyComment) },
                    )
                }

                Button(onClick = { editingTaskIndex = -1 }) {
                    Icon(Icons.Add)
                    Spacer(Modifier.width(8.dp))
                    Text(component = HSLang.Task.newTask)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (TaskManager.taskList.isEmpty()) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            } else {
                TaskList(Modifier.fillMaxSize()) { editingTaskIndex = it }
            }
        }
    }

    editingTaskIndex?.let { index ->
        TaskEditorDialog(
            task = TaskManager.taskList.values().getOrElse(index) { KeybindTickTask.empty },
            title = {
                if (index == -1) Text(component = HSLang.Task.newTask)
                else Text(component = HSLang.Task.editTask)
            },
            onDismissRequest = { editingTaskIndex = null },
        ) { newTask ->
            if (index == -1) {
                TaskManager.add(newTask)
            } else {
                TaskManager.update(index, newTask)
            }
        }
    }
}

/**
 * 任务列表：每行一个任务，行拖拽由 [ReorderableItem] 承担（行 key 取 [Keyed.key]，
 * 重排与增删时行的组合状态跟数据走，不会错位）。
 *
 * @param onEdit 编辑按钮回调，参数为该行在 [TaskManager.taskList] 中的下标
 */
@Composable
private fun TaskList(modifier: Modifier, onEdit: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        TaskManager.moveElement(from.index, to.index)
    }
    val canScroll = listState.canScroll

    Row(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(TaskRowDefaults.RowSpacing),
        ) {
            itemsIndexed(TaskManager.taskList, key = { _, keyed -> keyed.key }) { index, keyed ->
                ReorderableItem(
                    state = reorderState,
                    key = keyed.key,
                    animateItemModifier = hsItemAnimation(),
                ) {
                    TaskRow(
                        task = keyed.value,
                        // 拖拽手势只能在 ReorderableItem 的作用域里取，因此在调用点算好再传进去
                        dragHandleModifier = Modifier.draggableHandle(),
                        onEdit = { onEdit(index) },
                        onRemove = { TaskManager.removeAt(index) },
                    )
                }
            }
        }

        // 滚动条占自己的一列，不叠在列表上
        if (canScroll) Spacer(Modifier.width(TaskRowDefaults.ItemSpacing))
        VerticalFlatScroller(adapter = rememberScrollerAdapter(listState))
    }
}

/**
 * 任务行：拖拽手柄、图标、名称、执行器摘要，右侧是快捷键与操作按钮。
 *
 * 行不画容器，悬停时由 [hoverHighlight] 铺一层高亮（与配置行同款反馈）。
 *
 * @param dragHandleModifier 拖拽手势修饰符，由调用方在 [ReorderableItem] 作用域内取好后传入
 */
@Composable
private fun TaskRow(
    task: KeybindTickTask,
    dragHandleModifier: Modifier,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val iconScale = LocalSokitsuPixelScale.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(Modifier.fillMaxWidth().hoverHighlight(interactionSource)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .padding(TaskRowDefaults.RowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TaskRowDefaults.ItemSpacing),
        ) {
            DragHandle(
                modifier = dragHandleModifier,
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            )

            val icon = remember(task.icon) { ItemStack(task.icon) }
            if (!icon.isEmpty) {
                Box(Modifier.width(TaskRowDefaults.IconColumn), contentAlignment = Alignment.Center) {
                    ItemIcon(icon, showTooltip = false)
                }
            }

            Text(
                component = task.nameAsInlineStyleText,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            TaskExecutorInfo(task, Modifier.weight(1f))

            KeybindControls(task.keybind, Modifier.width(TaskRowDefaults.KeybindWidth))

            IconButton({
                task.execute()
            }, Modifier.tooltip {
                Text(component = HSLang.Task.execute)
            }) {
                Icon(Icons.Play, scale = iconScale)
            }

            IconButton(onEdit, Modifier.tooltip {
                Text(component = HSLang.Task.editTask)
            }) {
                Icon(Icons.Edit, scale = iconScale)
            }

            RemoveConfirmButton(
                message = task.name,
                onConfirm = onRemove,
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            )
        }
    }
}

/** 执行器摘要：类型 + 内容预览，内容过长时省略。 */
@Composable
private fun TaskExecutorInfo(task: HSTickTask, modifier: Modifier = Modifier) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(TaskRowDefaults.ItemSpacing),
) {
    Text(task.executorType.translateText, color = SokitsuTheme.colorScheme.primary, maxLines = 1)
    Text(task.executor.asString(), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
}

/**
 * 快捷键控件：绑定按钮 + 扩展设置按钮（编辑，与配置页的快捷键行同款）。
 *
 * @param modifier 作用于整组控件
 */
@Composable
private fun KeybindControls(
    keybind: Keybind,
    modifier: Modifier = Modifier,
) {
    val iconScale = LocalSokitsuPixelScale.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TaskRowDefaults.ItemSpacing),
    ) {
        KeybindSetButton(keybind = keybind, modifier = Modifier.weight(1f))
        KeybindSettingSetButton(
            keybindSetting = keybind.setting,
            onValueChange = { keybind.setFrom(it) },
            iconScale = iconScale,
            contentPadding = EditDialogContentDefaults.iconPadding,
        )
    }
}

/**
 * 一行「标签 + 控件」：标签列固定 [TaskEditorDefaults.LabelWidth]，控件由 [content] 定宽
 * （同一排里各控件的宽度由调用方给，通常用 `Modifier.weight(1f)` 把该排铺满）。
 *
 * 标签列等宽是本组件的唯一职责 —— 同一排里各个控件因此落在同一列上。
 */
@Composable
private fun TaskLabeledRow(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(TaskRowDefaults.ItemSpacing),
    ) {
        val rowScope = this
        Box(
            modifier = Modifier.width(TaskEditorDefaults.LabelWidth),
            contentAlignment = Alignment.CenterStart,
        ) {
            label()
        }
        rowScope.content()
    }
}

/**
 * 任务编辑浮层：图标 / 名称、执行器类型 / 执行时间、延迟 / 周期 / 次数，其余高度给执行器内容。
 *
 * 快捷键不在本浮层里编辑（列表行上直接改）；保存时按 [task] 的运行时类型重建，
 * 原类型保留自己的扩展字段（图标、快捷键），未在本浮层展示的扩展字段沿用 [task] 上的原值。
 */
@Composable
fun <T : HSTickTask> TaskEditorDialog(
    task: T,
    title: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onTaskChange: (T) -> Unit,
) {

    val name = rememberTextFieldState(task.name)
    var delay by remember { mutableIntStateOf(task.setting.delay) }
    var period by remember { mutableIntStateOf(task.setting.period) }
    var times by remember { mutableIntStateOf(task.setting.times) }
    var executeOn by remember { mutableStateOf(task.executeOn) }
    var executorType by remember { mutableStateOf(task.executorType) }
    val executor = rememberTextFieldState(task.executor.asString())
    var icon by remember { mutableStateOf((task as? IconTickTask)?.icon) }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = { true },
        content = {
            Column(
                modifier = Modifier.size(TaskEditorDefaults.Width, TaskEditorDefaults.Height),
                verticalArrangement = Arrangement.spacedBy(TaskEditorDefaults.RowSpacing),
            ) {
                // 属性区两行三列：每格宽度相同，两行的控件因此等宽、逐列对齐，也不留空格子
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TaskEditorDefaults.ColumnGap),
                ) {
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.name) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(TaskRowDefaults.ItemSpacing),
                        ) {
                            // 图标不单独占一格：它是任务的标识，跟名称排在同一格里
                            icon?.let { current ->
                                CompositionLocalProvider(
                                    LocalItemIconVanillaSize provides TaskEditorDefaults.IconSize,
                                ) {
                                    ItemSelector(
                                        current,
                                        { icon = it },
                                        false,
                                        1.05f,
                                        Modifier.tooltip { Text(component = HSLang.Task.icon) },
                                    )
                                }
                            }
                            TextField(name, modifier = Modifier.weight(1f))
                        }
                    }
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.executorType) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        EnumSelector(
                            executorType, { executorType = it }, enumEntries(),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp),
                        )
                    }
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.executeOn) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        EnumSelector(
                            executeOn, { executeOn = it }, enumEntries(),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TaskEditorDefaults.ColumnGap),
                ) {
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.delay) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        IntField(
                            delay,
                            { delay = it },
                            valueRange = 0..TaskEditorDefaults.MAX_SETTING,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.period) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        IntField(
                            period,
                            { period = it },
                            valueRange = 1..TaskEditorDefaults.MAX_SETTING,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    TaskLabeledRow(
                        label = { Text(component = HSLang.Task.times) },
                        modifier = Modifier.width(TaskEditorDefaults.ColumnWidth),
                    ) {
                        IntField(
                            times,
                            { times = it },
                            valueRange = 1..TaskEditorDefaults.MAX_SETTING,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // 执行器内容：吃掉剩余高度做多行编辑；执行器类型已由上一行的选择器表达，不再重复标注
                TextField(
                    executor,
                    modifier = Modifier.weight(1f).fillMaxWidth().codeEditorShortcuts(executor),
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    contentPadding = TextFieldDefaults.contentPadding(),
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val setting = TickTask.Setting(delay, period, times)
                val content = executor.text.toString()
                val newTask: HSTickTask = when (task) {
                    is KeybindTickTask -> task.copy(
                        name = name.text.toString(),
                        setting = setting,
                        executeOn = executeOn,
                        executorType = executorType,
                        executor = executorType.fromString(content),
                        icon = icon ?: task.icon,
                    )

                    is IconTickTask    -> IconTickTask(
                        name = name.text.toString(),
                        setting = setting,
                        executeOn = executeOn,
                        executorType = executorType,
                        executor = executorType.fromString(content),
                        icon = icon ?: task.icon,
                    )

                    else               -> HSTickTask(
                        name = name.text.toString(),
                        setting = setting,
                        executeOn = executeOn,
                        executorType = executorType,
                        executor = executorType.fromString(content),
                    )
                }
                @Suppress("UNCHECKED_CAST")
                onTaskChange(newTask as T)
                onDismissRequest()
            }) {
                Text(component = HSLang.Task.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = HSLang.Task.cancel)
            }
        },
    )
}
