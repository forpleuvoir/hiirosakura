package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenu
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskEditorDialog
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.Download
import moe.forpleuvoir.hiirosakura.ui.icon.defaults.PlayArrow
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.Add
import moe.forpleuvoir.ibukigourd.ui.icon.defaults.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.*
import moe.forpleuvoir.ibukigourd.ui.preset.modifier.plainTooltip
import net.minecraft.world.item.ItemStack
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun RadialMenuTaskPane(
    menu: CustomRadialMenu?,
    menuKey: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (menu == null || menuKey == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return
        }

        // Top toolbar
        var showNewTaskDialog by remember { mutableStateOf(false) }
        var showImportDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            KeybindAssistChip(menu.shortcuts, modifier = Modifier.width(280.dp))

            Spacer(Modifier.weight(1f))

            FilledTonalButton(onClick = { showNewTaskDialog = true }) {
                Icon(Icons.Add, null)
                Spacer(Modifier.width(4.dp))
                Text(HSLang.Task.newTask)
            }
            FilledTonalButton(onClick = { showImportDialog = true }) {
                Icon(Icons.Download, null)
                Spacer(Modifier.width(4.dp))
                Text(HSLang.CustomRadialMenu.importTasks)
            }
        }

        // Task list
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (menu.tasks.isEmpty()) {
                Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                var editingTaskIndex by remember { mutableStateOf(-1) }
                val lazyListState = rememberLazyListState()
                val hapticFeedback = LocalHapticFeedback.current
                val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    menu.moveTask(from.index, to.index)
                    CustomRadialMenuManager.save()
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
                val canScroll = lazyListState.canScrollBackward || lazyListState.canScrollForward
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = if (canScroll) 12.dp else 0.dp).fillMaxSize(),
                    state = lazyListState,
                ) {
                    itemsIndexed(menu.tasks, key = { _, keyed -> keyed.key }) { index, keyedTask ->
                        ReorderableItem(reorderableLazyListState, keyedTask.key) { isDragging ->
                            val scale by animateFloatAsState(if (isDragging) 1.015f else 1.0f)
                            val handleInteraction = remember { MutableInteractionSource() }
                            val handleHovered by handleInteraction.collectIsHoveredAsState()
                            Card(modifier = Modifier.fillMaxWidth().scale(scale)) {
                                Row(
                                    Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        DragHandle(hapticFeedback, handleInteraction, handleHovered, isDragging)
                                        val stack = remember(keyedTask.value.icon) { ItemStack(keyedTask.value.icon) }
                                        if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
                                        Text(InlineStyleText(keyedTask.value.name))
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        IconButton({
                                            keyedTask.value.execute()
                                        }, Modifier.plainTooltip { Text(HSLang.Task.execute) }) {
                                            Icon(Icons.PlayArrow, null)
                                        }

                                        IconButton({
                                            editingTaskIndex = index
                                        }, Modifier.plainTooltip { Text(HSLang.Task.editTask) }) {
                                            Icon(Icons.EditNote, null)
                                        }

                                        RemoveConfirmButton(
                                            message = keyedTask.value.name,
                                            action = {
                                                menu.removeTaskAt(index)
                                                CustomRadialMenuManager.save()
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyListState),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                // Edit task dialog
                if (editingTaskIndex >= 0) {
                    val keyedTask = menu.tasks.getOrNull(editingTaskIndex)
                    if (keyedTask != null) {
                        TaskEditorDialog(
                            task = keyedTask.value,
                            title = { Text(HSLang.Task.editTask) },
                            onDismissRequest = { editingTaskIndex = -1 },
                        ) { newTask ->
                            menu.updateTask(editingTaskIndex, newTask)
                            CustomRadialMenuManager.save()
                            editingTaskIndex = -1
                        }
                    } else {
                        editingTaskIndex = -1
                    }
                }
            }
        }

        // New task dialog
        if (showNewTaskDialog) {
            TaskEditorDialog(
                task = IconTickTask.empty,
                title = { Text(HSLang.Task.newTask) },
                onDismissRequest = { showNewTaskDialog = false },
            ) { newTask ->
                menu.addTask(newTask)
                CustomRadialMenuManager.save()
                showNewTaskDialog = false
            }
        }

        // Import dialog
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
}
