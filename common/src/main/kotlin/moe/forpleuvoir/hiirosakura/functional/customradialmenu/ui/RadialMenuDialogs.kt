package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenu
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager.validateMenuName
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.RadialMenuSetting
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.TaskManager
import moe.forpleuvoir.ibukigourd.input.KeyTriggerTiming
import moe.forpleuvoir.ibukigourd.input.Keybind
import moe.forpleuvoir.ibukigourd.input.KeybindSetting
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.preset.*
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

@Composable
fun CreateRadialMenuDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, menu: CustomRadialMenu) -> Unit,
) {
    val nameState = rememberTextFieldState("")
    val keybind = remember {
        Keybind(
            defaultSetting = KeybindSetting(
                strict = false,
                trigger = KeyTriggerTiming.Press,
            )
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.CustomRadialMenu.add) },
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = {
            val name = nameState.text.toString().trim()
            when {
                name.isEmpty() -> {
                    errorMessage = HSLang.Common.cantBeEmpty(HSLang.Common.name).plainText
                    false
                }

                name in CustomRadialMenuManager.customRadialMenus -> {
                    errorMessage = HSLang.CustomRadialMenu.exists(name).plainText
                    false
                }

                else -> true
            }
        },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.width(400.dp)) {
                OutlinedTextField(
                    nameState,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(HSLang.Common.name) },
                    lineLimits = TextFieldLineLimits.SingleLine
                )
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
                KeybindAssistChip(keybind, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                val name = nameState.text.toString().trim()
                val menu = CustomRadialMenu(shortcuts = keybind, tasks = emptyList())
                onConfirm(name, menu)
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

@Composable
fun RenameMenuDialog(
    currentName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (newName: String) -> Unit,
) {
    val nameState = rememberTextFieldState(currentName)
    var errorMessage by remember { mutableStateOf<Component?>(null) }

    LaunchedEffect(nameState.text) {
        errorMessage = validateMenuName(nameState.text.toString())
    }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.CustomRadialMenu.editName) },
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = { errorMessage == null },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.width(400.dp)) {
                OutlinedTextField(
                    state = nameState,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    label = {
                        errorMessage?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        } ?: Text(HSLang.Common.name)
                    },
                    lineLimits = TextFieldLineLimits.SingleLine
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    onConfirm(nameState.text.toString().trim())
                    onDismissRequest()
                },
                enabled = errorMessage == null
            ) {
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

@Composable
fun RadialMenuSettingDialog(
    setting: RadialMenuSetting,
    onDismissRequest: () -> Unit,
    onConfirm: (RadialMenuSetting) -> Unit,
) {
    var draft by remember(setting) { mutableStateOf(setting) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.CustomRadialMenu.setting) },
        modifier = Modifier.padding(24.dp).width(800.dp),
        onConfirmRequest = {
            if (draft.innerRadius >= draft.outerRadius) {
                errorMessage = "innerRadius (${draft.innerRadius}) must be < outerRadius (${draft.outerRadius})"
                false
            } else true
        },
        content = {
            Box {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.fillMaxWidth().padding(end = 12.dp).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    @Composable
                    fun EntryRow(content: @Composable RowScope.() -> Unit) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            content = content
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingInnerColor.plainText)
                        ColorAssistChip(
                            draft.innerColor,
                            { draft = draft.copy(innerColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingInnerColor) })
                    }
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingOuterColor.plainText)
                        ColorAssistChip(
                            draft.outerColor,
                            { draft = draft.copy(outerColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingOuterColor) })
                    }
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingInnerSelectedColor.plainText)
                        ColorAssistChip(
                            draft.innerSelectedColor,
                            { draft = draft.copy(innerSelectedColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingInnerSelectedColor) })
                    }
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingOuterSelectedColor.plainText)
                        ColorAssistChip(
                            draft.outerSelectedColor,
                            { draft = draft.copy(outerSelectedColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingOuterSelectedColor) })
                    }
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingBorderColor.plainText)
                        ColorAssistChip(
                            draft.borderColor,
                            { draft = draft.copy(borderColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingBorderColor) })
                    }
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingSelectedBorderColor.plainText)
                        ColorAssistChip(
                            draft.selectedBorderColor,
                            { draft = draft.copy(selectedBorderColor = it) },
                            editorTitle = { Text(HSLang.CustomRadialMenu.settingSelectedBorderColor) })
                    }

                    val width = 280.dp
                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingIconScale.plainText)
                        FloatSlider(
                            draft.iconScale,
                            { draft = draft.copy(iconScale = it) },
                            valueRange = 0.2f..2f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingInnerRadius.plainText)
                        FloatSlider(
                            draft.innerRadius,
                            { draft = draft.copy(innerRadius = it) },
                            valueRange = 80f..250f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingOuterRadius.plainText)
                        FloatSlider(
                            draft.outerRadius,
                            { draft = draft.copy(outerRadius = it) },
                            valueRange = 200f..500f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingOptionRadius.plainText)
                        FloatSlider(
                            draft.optionRadius,
                            { draft = draft.copy(optionRadius = it) },
                            valueRange = 150f..400f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingGap.plainText)
                        FloatSlider(draft.gap, { draft = draft.copy(gap = it) }, valueRange = 0f..30f, modifier = Modifier.width(width).height(24.dp))
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingPageSize.plainText)
                        IntSlider(draft.pageSize, { draft = draft.copy(pageSize = it) }, valueRange = 4..12, modifier = Modifier.width(width).height(24.dp))
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingCornerRadius.plainText)
                        FloatSlider(
                            draft.cornerRadius,
                            { draft = draft.copy(cornerRadius = it) },
                            valueRange = 0f..20f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }

                    EntryRow {
                        Text(HSLang.CustomRadialMenu.settingBorderWidth.plainText)
                        FloatSlider(
                            draft.borderWidth,
                            { draft = draft.copy(borderWidth = it) },
                            valueRange = 0f..10f,
                            modifier = Modifier.width(width).height(24.dp)
                        )
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(draft)
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

@Composable
fun ImportTasksDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (List<IconTickTask>) -> Unit,
) {
    var selectedKeys by remember { mutableStateOf(setOf<Long>()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.CustomRadialMenu.importTasks) },
        modifier = Modifier.padding(24.dp),
        content = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                if (TaskManager.taskList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(IGLang.Misc.hasNothing, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Box(Modifier.fillMaxSize()) {
                        val state = rememberLazyListState()
                        LazyColumn(modifier = Modifier.padding(end = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp), state = state) {
                            items(TaskManager.taskList, key = { it.key }) { keyed ->
                                val checked = keyed.key in selectedKeys
                                Row(
                                    Modifier.fillMaxWidth()
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable {
                                            selectedKeys = if (checked) selectedKeys - keyed.key
                                            else selectedKeys + keyed.key
                                        }.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Checkbox(checked, {})
                                    val stack = remember(keyed.value.icon) { ItemStack(keyed.value.icon) }
                                    if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
                                    Text(InlineStyleText(keyed.value.name), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(state),
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        )
                    }
                }
            }
        },
        onConfirmRequest = { true },
        confirmButton = {
            Button(onClick = {
                val imported = TaskManager.taskList.filter { it.key in selectedKeys }.map { keyed ->
                    val task = keyed.value
                    IconTickTask(
                        name = task.name,
                        setting = task.setting,
                        executeOn = task.executeOn,
                        executorType = task.executorType,
                        executor = task.executor,
                        icon = task.icon,
                    )
                }
                onConfirm(imported)
                onDismissRequest()
            }, enabled = selectedKeys.isNotEmpty()) {
                Text(IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(IGLang.Misc.cancel)
            }
        }
    )
}
