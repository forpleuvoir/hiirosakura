package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble.ChatBubbleServerConfig
import moe.forpleuvoir.hiirosakura.lang.ChatLang
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.rememberTextFieldStateBinding
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Texts
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapConfigWrapperDefaults
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapEntry
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.HorizontalDivider
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Switch
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButton

context(group: ConfigGroup)
fun configChatBubbleServerConfigMap(name: String, defaultValue: Map<String, ChatBubbleServerConfig>) =
    configMap(name, defaultValue, ChatBubbleServerConfig)

//------------ UI Wrapper ------------\\

@Composable
fun ChatBubbleServerConfigDisplayer(
    value: ChatBubbleServerConfig,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    FlatButton(
        onClick = {},
        modifier = modifier.tooltip {
            Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(component = ChatLang.bubbleServerConfig, modifier = Modifier.fillMaxWidth())
                HorizontalDivider()
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(component = ChatLang.bubbleServerConfigEnableUUID)
                    Text(component = IGLang.Misc.coloredSwitch(value.enableUUID))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(component = ChatLang.bubbleServerConfigEnableProfile)
                    Text(component = IGLang.Misc.coloredSwitch(value.enableProfile))
                }
            }
        },
    ) {
(leadingIcon)?.invoke()
Text(
    Texts.literal(value.regex),
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
)
(trailingIcon)?.invoke()
    }}

@Composable
fun ChatBubbleServerConfigForm(
    value: ChatBubbleServerConfig,
    onValueChange: (ChatBubbleServerConfig) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val regexState = rememberTextFieldStateBinding(value.regex) { onValueChange(value.copy(regex = it)) }
        LabelBox(label = { Text(component = ChatLang.bubbleServerConfigRegex) }) {
            TextField(
                state = regexState,
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(component = ChatLang.bubbleServerConfigEnableUUID)
            Switch(
                checked = value.enableUUID,
                onCheckedChange = { onValueChange(value.copy(enableUUID = it)) },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(component = ChatLang.bubbleServerConfigEnableProfile)
            Switch(
                checked = value.enableProfile,
                onCheckedChange = { onValueChange(value.copy(enableProfile = it)) },
            )
        }
    }
}

@Composable
fun ChatBubbleServerConfigEditorDialog(
    onDismissRequest: () -> Unit,
    value: ChatBubbleServerConfig,
    onValueChange: (ChatBubbleServerConfig) -> Unit,
) {
    var editingValue by remember(value) { mutableStateOf(value) }
    SimpleAlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(component = ChatLang.bubbleServerConfig) },
        onConfirmRequest = {
            onValueChange(editingValue)
            true
        },
        content = {
            ChatBubbleServerConfigForm(
                value = editingValue,
                onValueChange = { editingValue = it },
            )
        },
    )
}

@Composable
fun ChatBubbleServerConfigInnerEditor(
    value: ChatBubbleServerConfig,
    onValueChange: (ChatBubbleServerConfig) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        ChatBubbleServerConfigDisplayer(
            value = value,
            modifier = Modifier.weight(1f),
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Edit)
                }
            },
        )
    }

    if (showDialog) {
        ChatBubbleServerConfigEditorDialog(
            onDismissRequest = { showDialog = false },
            value = value,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun ChatBubbleServerConfigMapConfigWrapper(
    config: ConfigMap<ChatBubbleServerConfig>,
    editorDialogTitle: @Composable (() -> Unit)? = { Text(config.translateText) },
    keyHeader: @Composable BoxScope.() -> Unit = { Text(component = ChatLang.bubbleServerName) },
    valueHeader: @Composable BoxScope.() -> Unit = { Text(component = ChatLang.bubbleServerConfig) },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(40.dp).size(1000.dp, 800.dp),
) = MapConfigWrapperDefaults.run {
    CompositionLocalProvider(
        ItemBrowserDefaults.LocalItemIconSize provides 32.dp
    ) {
        var showEditDialog by remember { mutableStateOf(false) }
        RowWrapper(
            config = config,
            modifier = modifier,
        ) { showEditDialog = true }


        if (showEditDialog) {
            EditDialog(
                config = config,
                modifier = dialogModifier,
                title = editorDialogTitle,
                onDismissRequest = { showEditDialog = false },
            ) { data ->
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        EditDialogContentHeader(
                            keyHeader = keyHeader,
                            valueHeader = valueHeader
                        )
                    },
                    addDialog = { onDismissRequest ->
                        val newKey = rememberTextFieldState("")
                        val isDuplicate = remember(newKey.text.toString()) { data.entries.any { it.value.key == newKey.text.toString() } }

                        var newValue by remember { mutableStateOf(ChatBubbleServerConfig()) }
                        SimpleAlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(component = IGLang.Misc.add) },
                            onConfirmRequest = {
                                if (!isDuplicate) {
                                    data.add(MapEntry(newKey.text.toString(), newValue))
                                }
                                true
                            },
                            content = {
                                Column {
                                    LabelBox(label = {
                                            if (isDuplicate) Text(component = IGLang.ConfigWrapper.keyExists(newKey.text.toString()))
                                            else Text(component = ChatLang.bubbleServerName)
                                        }) {
                                        TextField(
                                            state = newKey,
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = isDuplicate,
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    ChatBubbleServerConfigForm(
                                        value = newValue,
                                        onValueChange = { newValue = it },
                                    )
                                }
                            },
                        )
                    }
                ) { lazyListState ->
                    EditDialogContentList(
                        data = data,
                        modifier = Modifier,
                        lazyListState = lazyListState,
                    ) { value, onValueChange ->
                        ChatBubbleServerConfigInnerEditor(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier.weight(LocalValueColumnWeight.current),
                        )
                    }
                }
            }
        }
    }
}
