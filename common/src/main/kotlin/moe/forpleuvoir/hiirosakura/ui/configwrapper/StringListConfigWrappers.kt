package moe.forpleuvoir.hiirosakura.ui.configwrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox
import moe.forpleuvoir.hiirosakura.ui.widget.ValueTextField
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.config.item.ConfigList

/*
 * 旧版 IbukiGourd 的字符串列表配置浮层（`StringListConfigWrapper` / `StringPairListConfigWrapper`）。
 *
 * 新版上游只保留内建元素控件的配置浮层骨架，字符串列表编辑器没有对应组件，因此在这里按旧签名重建：
 * 复用本包兼容层的行/浮层骨架 [ListConfigWrapperDefaults]，元素控件换成 sokitsu 输入框。
 */

/** 字符串列表（如聊天过滤表达式）的配置行与编辑浮层。 */
@Composable
fun StringListConfigWrapper(
    config: ConfigList<String>,
    modifier: Modifier = Modifier,
    addContentLabel: @Composable () -> Unit = {},
    contentHeader: @Composable () -> Unit = { Text(component = config.translateText) },
) = ListConfigWrapperDefaults.run {
    var showEditDialog by remember { mutableStateOf(false) }
    RowWrapper(config = config, modifier = modifier) { showEditDialog = true }
    if (!showEditDialog) return@run
    EditDialog(
        config = config,
        onDismissRequest = { showEditDialog = false },
        title = { Text(component = config.translateText) },
    ) { data ->
        EditDialogContent(
            modifier = Modifier.fillMaxSize(),
            header = {
                EditDialogContentHeader(contentHeader = { contentHeader() })
            },
            addDialog = { onDismissRequest ->
                var text by remember { mutableStateOf("") }
                SimpleAlertDialog(
                    onDismissRequest = onDismissRequest,
                    onConfirmRequest = {
                        data.add(text)
                        true
                    },
                    title = addContentLabel,
                    content = {
                        LabelBox(label = addContentLabel) {
                            ValueTextField(text, { text = it }, Modifier.fillMaxWidth())
                        }
                    },
                )
            },
        ) { lazyListState ->
            EditDialogContentList(
                data = data,
                lazyListState = lazyListState,
            ) { entry, onValueChange ->
                ValueTextField(entry, onValueChange, Modifier.weight(1f))
            }
        }
    }
}

/** 字符串对列表（如聊天注入的 表达式 → 替换内容）的配置行与编辑浮层。 */
@Composable
fun StringPairListConfigWrapper(
    config: ConfigList<Pair<String, String>>,
    modifier: Modifier = Modifier,
    firstHead: @Composable () -> Unit,
    addFirstLabel: @Composable () -> Unit,
    secondHead: @Composable () -> Unit,
    addSecondLabel: @Composable () -> Unit,
) = ListConfigWrapperDefaults.run {
    var showEditDialog by remember { mutableStateOf(false) }
    RowWrapper(config = config, modifier = modifier) { showEditDialog = true }
    if (!showEditDialog) return@run
    EditDialog(
        config = config,
        onDismissRequest = { showEditDialog = false },
        title = { Text(component = config.translateText) },
    ) { data ->
        EditDialogContent(
            modifier = Modifier.fillMaxSize(),
            header = {
                EditDialogContentHeader(contentHeader = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.weight(1f)) { firstHead() }
                        Box(Modifier.weight(1f)) { secondHead() }
                    }
                })
            },
            addDialog = { onDismissRequest ->
                var first by remember { mutableStateOf("") }
                var second by remember { mutableStateOf("") }
                SimpleAlertDialog(
                    onDismissRequest = onDismissRequest,
                    onConfirmRequest = {
                        data.add(first to second)
                        true
                    },
                    title = firstHead,
                    content = {
                        Column {
                            LabelBox(label = addFirstLabel) {
                                ValueTextField(first, { first = it }, Modifier.fillMaxWidth())
                            }
                            Spacer(Modifier.height(8.dp))
                            LabelBox(label = addSecondLabel) {
                                ValueTextField(second, { second = it }, Modifier.fillMaxWidth())
                            }
                        }
                    },
                )
            },
        ) { lazyListState ->
            EditDialogContentList(
                data = data,
                lazyListState = lazyListState,
            ) { entry, onValueChange ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ValueTextField(entry.first, { onValueChange(it to entry.second) }, Modifier.weight(1f))
                    ValueTextField(entry.second, { onValueChange(entry.first to it) }, Modifier.weight(1f))
                }
            }
        }
    }
}
