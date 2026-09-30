package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenu
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskEditorDialog
import moe.forpleuvoir.hiirosakura.ui.icon.Play
import moe.forpleuvoir.hiirosakura.ui.widget.PagePanel
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import moe.forpleuvoir.hiirosakura.ui.widget.hsItemAnimation
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.DragHandle
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSetButton
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSettingSetButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButtonDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.hoverHighlight
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/** 任务行的尺寸与间距。 */
private object TaskPaneDefaults {

    /** 快捷键按钮宽度：与其它界面里的快捷键控件一致。 */
    val KeybindButtonWidth: Dp = 280.dp

    /** 图标按钮与相邻控件的间距。 */
    val IconSpacing: Dp = 8.dp

    /** 快捷键整组宽度：按钮 + 间距 + 一个图标按钮槽（扩展设置）。 */
    val KeybindRowWidth: Dp = KeybindButtonWidth + IconSpacing + IconButtonDefaults.minSize.width

    /** 图标格：容下一个物品图标。 */
    val IconColumn: Dp = 56.dp

    /** 行内边距。 */
    val RowPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /** 行内元素间距。 */
    val ItemSpacing: Dp = 8.dp

    /** 相邻两行的间距。 */
    val RowSpacing: Dp = 2.dp
}

/**
 * 右列：选中轮盘菜单的快捷键 + 任务列表。
 *
 * 任务行不画容器，悬停时由 [hoverHighlight] 铺一层高亮；行拖拽手柄的手势由调用方在
 * [ReorderableItem] 作用域内取好后传入（`Modifier.draggableHandle()` 只在该作用域内可用）。
 */
@Composable
fun RadialMenuTaskPane(
    menu: CustomRadialMenu?,
    menuKey: String?,
    modifier: Modifier = Modifier,
) {
    if (menu == null || menuKey == null) {
        PagePanel(modifier = modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    var showNewTaskDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    // null = 不显示编辑浮层，其余为 tasks 下标
    var editingTaskIndex by remember { mutableStateOf<Int?>(null) }

    PagePanel(
        modifier = modifier.fillMaxSize(),
        toolbar = {
            // 切换菜单时只让两块面板的内容淡入淡出，面板自身保持不动
            AnimatedContent(
                targetState = menuKey,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.fillMaxWidth(),
                label = "RadialMenuToolbar",
            ) { targetKey ->
                RadialMenuToolbar(
                    menu = CustomRadialMenuManager.customRadialMenus[targetKey] ?: menu,
                    onNewTask = { showNewTaskDialog = true },
                    onImportTasks = { showImportDialog = true },
                )
            }
        },
    ) {
        AnimatedContent(
            targetState = menuKey,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "RadialMenuTaskList",
        ) { targetKey ->
            RadialMenuTaskList(
                menu = CustomRadialMenuManager.customRadialMenus[targetKey] ?: menu,
                onEditTask = { editingTaskIndex = it },
            )
        }
    }

    editingTaskIndex?.let { index ->
        val keyedTask = menu.tasks.getOrNull(index)
        if (keyedTask == null) {
            editingTaskIndex = null
        } else {
            TaskEditorDialog(
                task = keyedTask.value,
                title = { Text(component = HSLang.Task.editTask) },
                onDismissRequest = { editingTaskIndex = null },
            ) { newTask ->
                menu.updateTask(index, newTask)
                CustomRadialMenuManager.save()
                editingTaskIndex = null
            }
        }
    }

    if (showNewTaskDialog) {
        TaskEditorDialog(
            task = IconTickTask.empty,
            title = { Text(component = HSLang.Task.newTask) },
            onDismissRequest = { showNewTaskDialog = false },
        ) { newTask ->
            menu.addTask(newTask)
            CustomRadialMenuManager.save()
            showNewTaskDialog = false
        }
    }

    if (showImportDialog) {
        ImportTasksDialog(
            onDismissRequest = { showImportDialog = false },
            onConfirm = { importedTasks ->
                importedTasks.forEach { menu.addTask(it) }
                CustomRadialMenuManager.save()
            }
        )
    }
}

/**
 * 选中菜单的工具栏：快捷键与其扩展设置、「新建任务」与「从任务管理器导入任务」。
 */
@Composable
private fun RadialMenuToolbar(
    menu: CustomRadialMenu,
    onNewTask: () -> Unit,
    onImportTasks: () -> Unit,
) {
    var keybindVersion by remember(menu) { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.width(TaskPaneDefaults.KeybindRowWidth),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TaskPaneDefaults.ItemSpacing),
        ) {
            // 设置改的是 keybind 自身，显示文本不会因重画更新，按版本重建按钮
            key(keybindVersion) {
                KeybindSetButton(keybind = menu.shortcuts, modifier = Modifier.weight(1f))
            }
            KeybindSettingSetButton(
                keybindSetting = menu.shortcuts.setting,
                onValueChange = {
                    menu.shortcuts.setFrom(it)
                    keybindVersion++
                },
                iconScale = LocalSokitsuPixelScale.current,
                contentPadding = ConfigControlDefaults.IconButtonPadding,
            )
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = onNewTask) {
            Icon(Icons.Add)
            Spacer(Modifier.width(8.dp))
            Text(component = HSLang.Task.newTask)
        }
        Button(onClick = onImportTasks) {
            Icon(Icons.Import)
            Spacer(Modifier.width(8.dp))
            Text(component = HSLang.CustomRadialMenu.importTasks)
        }
    }
}

/**
 * 选中菜单的任务列表：行可拖拽排序，滚动条占自己一列。
 */
@Composable
private fun RadialMenuTaskList(
    menu: CustomRadialMenu,
    onEditTask: (Int) -> Unit,
) {
    if (menu.tasks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        menu.moveTask(from.index, to.index)
        CustomRadialMenuManager.save()
    }

    Row(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(TaskPaneDefaults.RowSpacing),
        ) {
            itemsIndexed(menu.tasks, key = { _, keyed -> keyed.key }) { index, keyed ->
                ReorderableItem(
                    state = reorderState,
                    key = keyed.key,
                    animateItemModifier = hsItemAnimation(),
                ) {
                    RadialMenuTaskRow(
                        task = keyed.value,
                        // 拖拽手势只能在 ReorderableItem 的作用域里取，因此在调用点算好再传进去
                        dragHandleModifier = Modifier.draggableHandle(),
                        onExecute = { keyed.value.execute() },
                        onEdit = { onEditTask(index) },
                        onRemove = {
                            menu.removeTaskAt(index)
                            CustomRadialMenuManager.save()
                        },
                    )
                }
            }
        }

        ScrollbarColumn(listState)
    }
}

/**
 * 任务行：拖拽手柄、图标、名称，右侧是执行 / 编辑 / 删除。
 *
 * @param dragHandleModifier 拖拽手势修饰符，由调用方在 [ReorderableItem] 作用域内取好后传入
 */
@Composable
private fun RadialMenuTaskRow(
    task: IconTickTask,
    dragHandleModifier: Modifier,
    onExecute: () -> Unit,
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
                .padding(TaskPaneDefaults.RowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TaskPaneDefaults.ItemSpacing),
        ) {
            DragHandle(
                modifier = dragHandleModifier,
                iconScale = iconScale,
                contentPadding = EditDialogContentDefaults.iconPadding,
            )

            val icon = remember(task.icon) { ItemStack(task.icon) }
            if (!icon.isEmpty) {
                Box(Modifier.width(TaskPaneDefaults.IconColumn), contentAlignment = Alignment.Center) {
                    ItemIcon(icon, showTooltip = false)
                }
            }

            Text(
                component = task.nameAsInlineStyleText,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            IconButton(onExecute, Modifier.tooltip {
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
