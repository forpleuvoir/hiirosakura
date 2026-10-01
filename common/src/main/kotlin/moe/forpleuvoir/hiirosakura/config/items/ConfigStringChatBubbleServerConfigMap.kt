package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.LocalSokitsuPixelScale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleServerConfig
import moe.forpleuvoir.hiirosakura.lang.ChatLang
import moe.forpleuvoir.hiirosakura.ui.widget.InlineEditField
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.rememberTextFieldStateBinding
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogTitle
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigListRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringEditDialog
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asDerivedState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogAddButton
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContent
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentCards
import moe.forpleuvoir.ibukigourd.ui.editdialog.EditDialogContentDefaults
import moe.forpleuvoir.ibukigourd.ui.editdialog.RemoveConfirmButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.util.fabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberFabScrollVisibility
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.ibukigourd.ui.util.values
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap

context(group: ConfigGroup)
fun configChatBubbleServerConfigMap(name: String, defaultValue: Map<String, ChatBubbleServerConfig>) =
    configMap(name, defaultValue, ChatBubbleServerConfig)

//------------ UI Wrapper ------------\\

/** 服务器配置卡片的排版常量。 */
private object ChatBubbleServerCardDefaults {

    /** 一张卡片的最小宽度：标签列 + 编辑器列 + 卡片内边距。 */
    val MinCardWidth: Dp = 560.dp

    /** 一行的卡片数下限。 */
    const val MinColumns: Int = 2

    /** 一行的卡片数上限。 */
    const val MaxColumns: Int = 2

    /** 卡片间距，取卡片列表的缺省值。 */
    val CardSpacing: Dp = EditDialogContentDefaults.cardSpacing

    /** 卡片列表右侧细滚动条的占位宽度，算列数时先扣掉。 */
    val ScrollbarAllowance: Dp = 16.dp

    /** 浮层内容内边距，与 `FlexibleDialogDefaults.contentPadding` 一致。 */
    val DialogContentPadding: Dp = 24.dp

    /** 卡片内各行之间的纵向间距。 */
    val RowSpacing: Dp = 8.dp

    /** 标签列与编辑器之间的水平间距。 */
    val LabelGap: Dp = 8.dp

    /** 标签列左侧留白：标签不贴卡片边缘。 */
    val LabelStartPadding: Dp = 8.dp

    /** 开关行右侧留白：开关不贴卡片边缘。 */
    val SwitchEndPadding: Dp = 8.dp

    /** 标签列占行宽的权重（与 [EditorWeight] 配对比；各行一致 ⇒ 编辑器列等宽）。 */
    const val LabelWeight: Float = 0.32f

    /** 编辑器列占行宽的权重。 */
    const val EditorWeight: Float = 0.68f

    /** 浮层宽度下限：一行放得下 [MinColumns] 张卡片。 */
    val DialogMinWidth: Dp
        get() = MinCardWidth * MinColumns + CardSpacing * (MinColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层宽度上限：一行最多 [MaxColumns] 张卡片。 */
    val DialogMaxWidth: Dp
        get() = MinCardWidth * MaxColumns + CardSpacing * (MaxColumns - 1) + ScrollbarAllowance + DialogContentPadding * 2

    /** 浮层高度上限：可视区放得下两行卡片并有富余。 */
    val DialogMaxHeight: Dp = 1100.dp

    /** 浮层尺寸约束：宽度只夹上下限，具体宽度由窗口决定。 */
    val DialogModifier: Modifier
        get() = Modifier
            .widthIn(min = DialogMinWidth, max = DialogMaxWidth)
            .heightIn(max = DialogMaxHeight)

    /** 按浮层内容区的可用宽度算卡片列数。 */
    fun columnsFor(availableWidth: Dp): Int =
        ((availableWidth - ScrollbarAllowance + CardSpacing) / (MinCardWidth + CardSpacing))
            .toInt()
            .coerceIn(MinColumns, MaxColumns)
}

/** 卡片里编辑的一条服务器配置：服务器名称与配置分开存，名称可改。 */
private data class EditableServerEntry(
    val name: String,
    val value: ChatBubbleServerConfig,
)

/**
 * 服务器配置：**一条配置一张卡片**。
 *
 * 卡片体是「标签 + 编辑器」四行（服务器名称 / 解析表达式 / UUID / 玩家档案），名称行是只读框 +
 * 框内编辑按钮（改名走浮层判重名）；卡片头部两端是拖拽手柄与删除按钮。
 * 条目增删、排序与编辑都在副本上做，确认时才写回配置。
 *
 * @param config 映射配置项
 * @param modifier 作用于配置页上那一行
 * @param dialogModifier 附加到浮层的尺寸修饰；缺省用卡片布局的下限 / 上限
 */
@Composable
fun ChatBubbleServerConfigMapConfigWrapper(
    config: ConfigMap<ChatBubbleServerConfig>,
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = ChatBubbleServerCardDefaults.DialogModifier,
) {
    var editing by remember(config) { mutableStateOf(false) }
    val size by config.asDerivedState { it.size }

    ConfigListRow(config, IGLang.ConfigWrapper.mapConfigWrapperText(size), modifier) { editing = true }

    if (editing) {
        val editingValue = rememberKeyedList(config.entries.map { EditableServerEntry(it.key, it.value) }, key = config)
        var showAddDialog by remember { mutableStateOf(false) }
        // 卡片网格的滚动状态：浮动添加按钮的显隐以它为准
        val cardGridState = rememberLazyGridState()
        val addButtonVisibility = rememberFabScrollVisibility(cardGridState)

        FlexibleDialog(
            onDismissRequest = { editing = false },
            onConfirmRequest = {
                config.clear()
                editingValue.entries.forEach { (_, entry) -> config[entry.name] = entry.value }
                true
            },
            title = { ConfigDialogTitle(config) },
            modifier = dialogModifier,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fabScrollVisibility(addButtonVisibility),
                ) {
                    EditDialogContent(
                        modifier = Modifier.fillMaxSize(),
                        header = {},
                    ) { _ ->
                        BoxWithConstraints(Modifier.fillMaxWidth()) {
                            EditDialogContentCards(
                                state = editingValue,
                                lazyGridState = cardGridState,
                                columns = ChatBubbleServerCardDefaults.columnsFor(maxWidth),
                                removeButton = { index, entry ->
                                    RemoveConfirmButton(
                                        message = entry.name,
                                        onConfirm = { editingValue.removeAt(index) },
                                        iconScale = LocalIconScale.current,
                                        contentPadding = EditDialogContentDefaults.iconPadding,
                                    )
                                },
                            ) { _, entry, onValueChange ->
                                ChatBubbleServerCard(
                                    entry = entry,
                                    onValueChange = onValueChange,
                                    isDuplicateName = { candidate ->
                                        candidate != entry.name && editingValue.entries.any { it.value.name == candidate }
                                    },
                                )
                            }
                        }
                    }

                    EditDialogAddButton(
                        visibility = addButtonVisibility,
                        modifier = Modifier.align(Alignment.BottomEnd),
                    ) {
                        Button(
                            onClick = { showAddDialog = true },
                            contentPadding = ConfigControlDefaults.IconButtonPadding,
                        ) {
                            Icon(Icons.Add, scale = configIconScale())
                        }
                    }
                }
            },
        )

        if (showAddDialog) {
            ServerConfigAddDialog(
                defaultName = uniqueServerName(editingValue.entries.values()),
                existingNames = editingValue.entries.map { it.value.name },
                onDismissRequest = { showAddDialog = false },
                onConfirm = {
                    editingValue.add(it)
                    showAddDialog = false
                },
            )
        }
    }
}

/** 一条服务器配置的卡片体：四行「标签 + 编辑器」，标签列对齐、编辑器等宽。 */
@Composable
private fun ChatBubbleServerCard(
    entry: EditableServerEntry,
    onValueChange: (EditableServerEntry) -> Unit,
    isDuplicateName: (String) -> Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ChatBubbleServerCardDefaults.RowSpacing),
    ) {
        ChatBubbleServerRow(
            label = {
                Text(
                    component = ChatLang.bubbleServerName,
                    modifier = Modifier.tooltip { Text(component = ChatLang.bubbleServerNameComment) },
                    maxLines = 1,
                )
            },
        ) {
            ServerNameField(
                name = entry.name,
                isDuplicate = isDuplicateName,
                onNameChange = { onValueChange(entry.copy(name = it)) },
            )
        }
        ChatBubbleServerRow(
            label = {
                Text(
                    component = ChatLang.bubbleServerConfigRegex,
                    modifier = Modifier.tooltip { Text(component = InlineStyleText(ChatLang.bubbleServerConfigRegexComment.plainText)) },
                    maxLines = 1,
                )
            },
        ) {
            ServerRegexField(
                regex = entry.value.regex,
                onRegexChange = { onValueChange(entry.copy(value = entry.value.copy(regex = it))) },
            )
        }
        SwitchRow(
            label = {
                Text(
                    component = ChatLang.bubbleServerConfigEnableUUID,
                    modifier = Modifier.tooltip { Text(component = InlineStyleText(ChatLang.bubbleServerConfigEnableUUIDComment.plainText)) },
                    maxLines = 1,
                )
            },
            checked = entry.value.enableUUID,
            onCheckedChange = { onValueChange(entry.copy(value = entry.value.copy(enableUUID = it))) },
        )
        SwitchRow(
            label = {
                Text(
                    component = ChatLang.bubbleServerConfigEnableProfile,
                    modifier = Modifier.tooltip { Text(component = InlineStyleText(ChatLang.bubbleServerConfigEnableProfileComment.plainText)) },
                    maxLines = 1,
                )
            },
            checked = entry.value.enableProfile,
            onCheckedChange = { onValueChange(entry.copy(value = entry.value.copy(enableProfile = it))) },
        )
    }
}

/** 开关行：标签在左，开关贴编辑器列右端。 */
@Composable
private fun SwitchRow(
    label: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ChatBubbleServerRow(label = label) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = ChatBubbleServerCardDefaults.SwitchEndPadding),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

/** 解析表达式的展示与编辑：只读框 + 框内编辑按钮，编辑走多行浮层。 */
@Composable
private fun ServerRegexField(
    regex: String,
    onRegexChange: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    InlineEditField(
        onEdit = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(regex, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    if (showDialog) {
        StringEditDialog(
            title = { Text(component = ChatLang.bubbleServerConfigRegex) },
            initial = regex,
            onDismiss = { showDialog = false },
            onConfirm = {
                onRegexChange(it)
                showDialog = false
            },
        )
    }
}

/**
 * 一行「标签 + 编辑器」：两列按 [ChatBubbleServerCardDefaults] 的权重比例分宽，
 * 各行比例一致 ⇒ 编辑器列等宽；行高由内容自然决定。
 */
@Composable
private fun ChatBubbleServerRow(
    label: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(ChatBubbleServerCardDefaults.LabelGap),
    verticalAlignment = Alignment.CenterVertically,
) {
    val rowScope = this
    val editorModifier = Modifier.weight(ChatBubbleServerCardDefaults.EditorWeight)

    Box(
        modifier = Modifier
            .weight(ChatBubbleServerCardDefaults.LabelWeight)
            .padding(start = ChatBubbleServerCardDefaults.LabelStartPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        label()
    }
    Box(modifier = editorModifier) {
        rowScope.content()
    }
}

/** 服务器名称的展示与编辑：只读框 + 框内编辑按钮，改名走浮层（判重名）。 */
@Composable
private fun ServerNameField(
    name: String,
    isDuplicate: (String) -> Boolean,
    onNameChange: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    InlineEditField(
        onEdit = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    if (showDialog) {
        ServerNameEditDialog(
            initial = name,
            isDuplicate = isDuplicate,
            onDismissRequest = { showDialog = false },
            onConfirm = {
                onNameChange(it)
                showDialog = false
            },
        )
    }
}

/** 名称编辑浮层：输入新名称，与其他条目重名时禁用确认。 */
@Composable
private fun ServerNameEditDialog(
    initial: String,
    isDuplicate: (String) -> Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val state = rememberTextFieldState(initial)
    val newName = state.text.toString()
    val duplicated = remember(newName) { isDuplicate(newName) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.edit) },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (duplicated) {
                    Text(component = IGLang.ConfigWrapper.keyExists(newName))
                } else {
                    Text(component = ChatLang.bubbleServerName)
                }
                TextField(
                    state = state,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    modifier = Modifier.fillMaxWidth(),
                    isError = duplicated,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newName) },
                enabled = !duplicated && newName.isNotEmpty(),
            ) {
                Text(component = IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = IGLang.Misc.cancel)
            }
        },
    )
}

/** 生成一个当前不冲突的默认名称（`server1`、`server2`…）。 */
private fun uniqueServerName(existing: List<EditableServerEntry>): String {
    val used = existing.mapTo(mutableSetOf()) { it.name }
    var index = 1
    while ("server$index" in used) index++
    return "server$index"
}

/**
 * 新增服务器配置浮层：填名称、解析表达式与两个开关；名称与其他条目重名时禁用确认。
 *
 * @param defaultName 名称输入框的初值
 * @param existingNames 已有名称，用于判重
 * @param onDismissRequest 关闭浮层
 * @param onConfirm 接受新条目
 */
@Composable
private fun ServerConfigAddDialog(
    defaultName: String,
    existingNames: List<String>,
    onDismissRequest: () -> Unit,
    onConfirm: (EditableServerEntry) -> Unit,
) {
    val nameState = rememberTextFieldState(defaultName)
    val name = nameState.text.toString()
    val duplicated = remember(name) { name in existingNames }
    var value by remember { mutableStateOf(ChatBubbleServerConfig()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = IGLang.Misc.add) },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChatBubbleServerRow(
                    label = {
                        if (duplicated) Text(component = IGLang.ConfigWrapper.keyExists(name), maxLines = 1)
                        else Text(component = ChatLang.bubbleServerName, maxLines = 1)
                    },
                ) {
                    TextField(
                        state = nameState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        modifier = Modifier.fillMaxWidth(),
                        isError = duplicated,
                    )
                }
                ChatBubbleServerRow(
                    label = { Text(component = ChatLang.bubbleServerConfigRegex, maxLines = 1) },
                ) {
                    val regexState = rememberTextFieldStateBinding(value.regex) { value = value.copy(regex = it) }
                    TextField(
                        state = regexState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                SwitchRow(
                    label = { Text(component = ChatLang.bubbleServerConfigEnableUUID, maxLines = 1) },
                    checked = value.enableUUID,
                    onCheckedChange = { value = value.copy(enableUUID = it) },
                )
                SwitchRow(
                    label = { Text(component = ChatLang.bubbleServerConfigEnableProfile, maxLines = 1) },
                    checked = value.enableProfile,
                    onCheckedChange = { value = value.copy(enableProfile = it) },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(EditableServerEntry(name, value)) },
                enabled = !duplicated && name.isNotEmpty(),
            ) {
                Text(component = IGLang.Misc.confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(component = IGLang.Misc.cancel)
            }
        },
    )
}

