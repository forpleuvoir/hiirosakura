package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.MapConfigWrapperDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.MapEntry
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap

context(group: ConfigGroup)
fun configItemStackMatcherMap(name: String, defaultValue: Map<String, ItemStackMatcher>) =
    configMap(name, defaultValue, ItemStackMatcher)

//------------ UI Wrapper ------------\\

@Composable
fun ItemStackMatcherMapConfigWrapper(
    config: ConfigMap<ItemStackMatcher>,
    editorDialogTitle: @Composable (() -> Unit)? = { Text(InlineStyleText(config.translateText.plainText)) },
    keyHeader: @Composable BoxScope.() -> Unit = { Text(IGLang.ConfigWrapper.mapKey) },
    addKeyLabel: @Composable TextFieldLabelScope.(isDuplicate: Boolean, newKey: String) -> Unit = { isDuplicate, newKey ->
        if (isDuplicate) Text(IGLang.ConfigWrapper.keyExists(newKey))
        else Text(IGLang.ConfigWrapper.mapKey)
    },
    valueHeader: @Composable BoxScope.() -> Unit = { Text(HSLang.ItemStackMatcher.title) },
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
                var nextKey by remember { mutableLongStateOf(data.size.toLong()) }
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
                        val isDuplicate = remember(newKey.text.toString()) { data.any { it.value.key == newKey.text.toString() } }
                        var newValue by remember { mutableStateOf(ItemStackMatcher.handheldItemMatcher) }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(IGLang.Misc.add) },
                            text = {
                                IGCompositionLocalProvider {
                                    Column {
                                        OutlinedTextField(
                                            state = newKey,
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = isDuplicate,
                                            label = { addKeyLabel(isDuplicate, newKey.text.toString()) },
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        ItemStackMatcherDisplayerInnerEditor(
                                            value = newValue,
                                            onValueChange = { newValue = it },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (!isDuplicate) {
                                            data.add(Keyed(nextKey++, MapEntry(newKey.text.toString(), newValue)))
                                            onDismissRequest()
                                        }
                                    },
                                    enabled = !isDuplicate
                                ) {
                                    Text(IGLang.Misc.confirm)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = onDismissRequest) {
                                    Text(IGLang.Misc.cancel)
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
                        ItemStackMatcherDisplayerInnerEditor(
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


