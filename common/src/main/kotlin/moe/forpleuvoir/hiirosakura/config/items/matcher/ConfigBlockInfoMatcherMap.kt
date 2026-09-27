package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapConfigWrapperDefaults
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapEntry
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox

context(group: ConfigGroup)
fun configBlockInfoMatcherMap(name: String, defaultValue: Map<String, BlockInfoMatcher>) =
    configMap(name, defaultValue, BlockInfoMatcher)


//------------ UI Wrapper ------------\\

@Composable
fun BlockInfoMatcherMapConfigWrapper(
    config: ConfigMap<BlockInfoMatcher>,
    editorDialogTitle: @Composable (() -> Unit)? = { Text(component = InlineStyleText(config.translateText.plainText)) },
    keyHeader: @Composable BoxScope.() -> Unit = { Text(component = IGLang.ConfigWrapper.mapKey) },
    addKeyLabel: @Composable (isDuplicate: Boolean, newKey: String) -> Unit = { isDuplicate, newKey ->
        if (isDuplicate) Text(component = IGLang.ConfigWrapper.keyExists(newKey))
        else Text(component = IGLang.ConfigWrapper.mapKey)
    },
    valueHeader: @Composable BoxScope.() -> Unit = { Text(component = HSLang.BlockInfoMatcher.title) },
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

                        var newValue by remember { mutableStateOf(BlockInfoMatcher.targetBlockMatcher) }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(component = IGLang.Misc.add) },
                            text = {
                                
                                    Column {
                                        LabelBox(label = { addKeyLabel(isDuplicate, newKey.text.toString()) }) {
                                            TextField(
                                                state = newKey,
                                                lineLimits = TextFieldLineLimits.SingleLine,
                                                modifier = Modifier.fillMaxWidth(),
                                                isError = isDuplicate,
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        BlockInfoMatcherDisplayerInnerEditor(
                                            value = newValue,
                                            onValueChange = { newValue = it },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (!isDuplicate) {
                                            data.add(MapEntry(newKey.text.toString(), newValue))
                                            onDismissRequest()
                                        }
                                    },
                                    enabled = !isDuplicate
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
                ) { lazyListState ->
                    EditDialogContentList(
                        data = data,
                        modifier = Modifier,
                        lazyListState = lazyListState,
                    ) { value, onValueChange ->
                        BlockInfoMatcherDisplayerInnerEditor(
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


