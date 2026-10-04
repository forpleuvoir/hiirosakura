package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.HSUiDefaults
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
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSetButton
import moe.forpleuvoir.ibukigourd.ui.colorpicker.ColorPickButton
import moe.forpleuvoir.ibukigourd.util.toComposeColor
import moe.forpleuvoir.ibukigourd.util.toNebulaColor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FloatSlider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntSlider
import moe.forpleuvoir.ibukigourd.ui.keybind.KeybindSettingSetButton
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.ScrollbarColumn
import androidx.compose.ui.graphics.RectangleShape

/** 对话框内容宽度：控件宽度 + 间距 + 一个图标按钮槽（快捷键的扩展设置按钮）。 */
private val DialogRowWidth = 528.dp

/** 控件与右侧动作按钮之间的间距。 */
private val DialogActionGap = 8.dp

/** 设置浮层里所有控件的统一宽度：颜色按钮与数值滑条同宽。 */
private val SettingControlWidth = 280.dp

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
        title = { Text(component = HSLang.CustomRadialMenu.add) },
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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.width(DialogRowWidth),
            ) {
                val error = errorMessage
                // 有错时挂一个"固定"气泡：不靠悬停，错误一出现就一直显示（与配置项里的重复键提示同款）
                val errorTooltip = error?.let { message ->
                    Modifier.tooltip(pinned = true) { Text(message) }
                } ?: Modifier
                TextField(
                    nameState,
                    modifier = Modifier.fillMaxWidth().then(errorTooltip),
                    hint = HSLang.Common.name,
                    isError = error != null,
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DialogActionGap),
                ) {
                    KeybindSetButton(keybind = keybind, modifier = Modifier.weight(1f))
                    KeybindSettingSetButton(
                        keybindSetting = keybind.setting,
                        onValueChange = { keybind.setFrom(it) },
                        iconScale = HSUiDefaults.ICON_SCALE,
                        contentPadding = ConfigControlDefaults.IconButtonPadding,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val name = nameState.text.toString().trim()
                val menu = CustomRadialMenu(shortcuts = keybind, tasks = emptyList())
                onConfirm(name, menu)
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
        title = { Text(component = HSLang.CustomRadialMenu.editName) },
        modifier = Modifier.padding(24.dp),
        onConfirmRequest = { errorMessage == null },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.width(DialogRowWidth),
            ) {
                // 名字是否可用（为空 / 重名）走"固定"气泡提示，不再在框上方挂一行标签
                val error = errorMessage
                val errorTooltip = error?.let { message ->
                    Modifier.tooltip(pinned = true) { Text(message) }
                } ?: Modifier
                TextField(
                    state = nameState,
                    modifier = Modifier.fillMaxWidth().then(errorTooltip),
                    hint = HSLang.Common.name,
                    isError = error != null,
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(nameState.text.toString().trim())
                    onDismissRequest()
                },
                enabled = errorMessage == null
            ) {
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
fun RadialMenuSettingDialog(
    setting: RadialMenuSetting,
    onDismissRequest: () -> Unit,
    onConfirm: (RadialMenuSetting) -> Unit,
) {
    var draft by remember(setting) { mutableStateOf(setting) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = HSLang.CustomRadialMenu.setting) },
        modifier = Modifier.padding(24.dp).width(800.dp),
        onConfirmRequest = {
            if (draft.innerRadius >= draft.outerRadius) {
                errorMessage = "innerRadius (${draft.innerRadius}) must be < outerRadius (${draft.outerRadius})"
                false
            } else true
        },
        content = {
            Row {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    errorMessage?.let {
                        Text(it, color = SokitsuTheme.colorScheme.error)
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
                        Text(
                            text = HSLang.CustomRadialMenu.settingInnerColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingInnerColorComment) },
                        )
                        ColorPickButton(
                            color = draft.innerColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(innerColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingInnerColor) },
                        )
                    }
                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingOuterColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingOuterColorComment) },
                        )
                        ColorPickButton(
                            color = draft.outerColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(outerColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingOuterColor) },
                        )
                    }
                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingInnerSelectedColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingInnerSelectedColorComment) },
                        )
                        ColorPickButton(
                            color = draft.innerSelectedColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(innerSelectedColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingInnerSelectedColor) },
                        )
                    }
                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingOuterSelectedColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingOuterSelectedColorComment) },
                        )
                        ColorPickButton(
                            color = draft.outerSelectedColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(outerSelectedColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingOuterSelectedColor) },
                        )
                    }
                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingBorderColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingBorderColorComment) },
                        )
                        ColorPickButton(
                            color = draft.borderColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(borderColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingBorderColor) },
                        )
                    }
                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingSelectedBorderColor.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingSelectedBorderColorComment) },
                        )
                        ColorPickButton(
                            color = draft.selectedBorderColor.toComposeColor(),
                            onValueChange = { draft = draft.copy(selectedBorderColor = it.toNebulaColor()) },
                            modifier = Modifier.width(SettingControlWidth),
                            title = { Text(component = HSLang.CustomRadialMenu.settingSelectedBorderColor) },
                        )
                    }


                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingIconScale.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingIconScaleComment) },
                        )
                        FloatSlider(
                            draft.iconScale,
                            { draft = draft.copy(iconScale = it) },
                            valueRange = 0.2f..2f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingInnerRadius.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingInnerRadiusComment) },
                        )
                        FloatSlider(
                            draft.innerRadius,
                            { draft = draft.copy(innerRadius = it) },
                            valueRange = 80f..250f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingOuterRadius.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingOuterRadiusComment) },
                        )
                        FloatSlider(
                            draft.outerRadius,
                            { draft = draft.copy(outerRadius = it) },
                            valueRange = 200f..500f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingOptionRadius.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingOptionRadiusComment) },
                        )
                        FloatSlider(
                            draft.optionRadius,
                            { draft = draft.copy(optionRadius = it) },
                            valueRange = 150f..400f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingGap.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingGapComment) },
                        )
                        FloatSlider(draft.gap, { draft = draft.copy(gap = it) }, valueRange = 0f..30f, modifier = Modifier.width(SettingControlWidth))
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingPageSize.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingPageSizeComment) },
                        )
                        IntSlider(draft.pageSize, { draft = draft.copy(pageSize = it) }, valueRange = 4..12, modifier = Modifier.width(SettingControlWidth))
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingCornerRadius.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingCornerRadiusComment) },
                        )
                        FloatSlider(
                            draft.cornerRadius,
                            { draft = draft.copy(cornerRadius = it) },
                            valueRange = 0f..20f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingBorderWidth.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingBorderWidthComment) },
                        )
                        FloatSlider(
                            draft.borderWidth,
                            { draft = draft.copy(borderWidth = it) },
                            valueRange = 0f..10f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }

                    EntryRow {
                        Text(
                            text = HSLang.CustomRadialMenu.settingTiltDegree.plainText,
                            modifier = Modifier.tooltip { Text(component = HSLang.CustomRadialMenu.settingTiltDegreeComment) },
                        )
                        FloatSlider(
                            draft.tiltDegree,
                            { draft = draft.copy(tiltDegree = it) },
                            valueRange = 0f..20f,
                            modifier = Modifier.width(SettingControlWidth)
                        )
                    }
                }

                ScrollbarColumn(scrollState)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(draft)
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
fun ImportTasksDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (List<IconTickTask>) -> Unit,
) {
    var selectedKeys by remember { mutableStateOf(setOf<Long>()) }

    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = HSLang.CustomRadialMenu.importTasks) },
        modifier = Modifier.padding(24.dp),
        content = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                if (TaskManager.taskList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(component = IGLang.Misc.hasNothing, color = SokitsuTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Row(Modifier.fillMaxSize()) {
                        val state = rememberLazyListState()
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(4.dp), state = state) {
                            items(TaskManager.taskList, key = { it.key }) { keyed ->
                                val checked = keyed.key in selectedKeys
                                Row(
                                    Modifier.fillMaxWidth()
                                        .clip(RectangleShape)
                                        .clickable {
                                            selectedKeys = if (checked) selectedKeys - keyed.key
                                            else selectedKeys + keyed.key
                                        }.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(if (checked) Icons.Checked else Icons.Unchecked, scale = 3)
                                    val stack = remember(keyed.value.icon) { ItemStack(keyed.value.icon) }

                                    if (!stack.isEmpty) ItemIcon(stack, showTooltip = false)
                                    Text(component = InlineStyleText(keyed.value.name), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        ScrollbarColumn(state)
                    }
                }
            }
        },
        onConfirmRequest = { true },
        confirmButton = {
            TextButton(onClick = {
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
                Text(component = IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = IGLang.Misc.cancel)
            }
        }
    )
}
