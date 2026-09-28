package moe.forpleuvoir.hiirosakura.config.items.matcher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.ItemStackMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerEditor
import moe.forpleuvoir.ibukigourd.config.item.pair
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapConfigWrapperDefaults
import moe.forpleuvoir.hiirosakura.ui.configwrapper.MapEntry
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigMap
import moe.forpleuvoir.nebula.config.item.configMap
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.hiirosakura.ui.widget.LabelBox

typealias BlockInfoItemStackPair = Pair<BlockInfoMatcher, ItemStackMatcher>

val BlockInfoItemStackPairCodec = Codec.pair(BlockInfoMatcher, ItemStackMatcher)

val BlockInfoItemStackPair.block get() = first
val BlockInfoItemStackPair.item get() = second

context(group: ConfigGroup)
fun configBlockInfoItemStackMap(name: String, defaultValue: Map<String, BlockInfoItemStackPair>) =
    configMap(name, defaultValue, BlockInfoItemStackPairCodec)

//------------ UI Wrapper ------------\\

@Composable
fun BlockInfoItemStackPairMapWrapper(
    config: ConfigMap<BlockInfoItemStackPair>,
    editorDialogTitle: @Composable (() -> Unit)? = { Text(component = InlineStyleText(config.translateText.plainText)) },
    keyHeader: @Composable BoxScope.() -> Unit = { Text(component = IGLang.ConfigWrapper.mapKey) },
    keyEditorLabel: @Composable (isDuplicate: Boolean, newKey: String) -> Unit = { isDuplicate, newKey ->
        if (isDuplicate) Text(component = IGLang.ConfigWrapper.keyExists(newKey))
        else Text(component = IGLang.ConfigWrapper.mapKey)
    },
    reverseValueColumnOrder: Boolean = false,
    blockInfoColumnWeight: Float = 1f,
    itemStackColumnWeight: Float = 1f,
    blockInfoHeader: @Composable RowScope.() -> Unit = {
        Text(
            HSLang.BlockInfoMatcher.targetBlock,
            Modifier.weight(blockInfoColumnWeight),
        )
    },
    itemStackHeader: @Composable RowScope.() -> Unit = {
        Text(
            HSLang.ItemStackMatcher.handheldItem,
            Modifier.weight(itemStackColumnWeight),
        )
    },
    blockInfoEditorLabel: @Composable () -> Unit = {
        Text(component = HSLang.BlockInfoMatcher.targetBlock)
    },
    itemStackEditorLabel: @Composable () -> Unit = {
        Text(component = HSLang.ItemStackMatcher.handheldItem)
    },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(40.dp).size(1000.dp, 800.dp),
) = MapConfigWrapperDefaults.run {
    CompositionLocalProvider(
        LocalValueColumnWeight provides 8f,
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
                            valueHeader = {
                                Row {
                                    if (reverseValueColumnOrder) itemStackHeader()
                                    else blockInfoHeader()
                                    Spacer(Modifier.width(LocalColumnSpacing.current))
                                    if (reverseValueColumnOrder) blockInfoHeader()
                                    else itemStackHeader()
                                }
                            }
                        )
                    },
                    addDialog = { onDismissRequest ->
                        val newKey = rememberTextFieldState("")
                        val isDuplicate = remember(newKey.text.toString()) { data.entries.any { it.value.key == newKey.text.toString() } }

                        var newItem by remember { mutableStateOf(ItemStackMatcher.handheldItemMatcher) }

                        var newBlock by remember { mutableStateOf(BlockInfoMatcher.targetBlockMatcher) }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(component = IGLang.Misc.add) },
                            text = {
                                
                                    Column {
                                        LabelBox(label = { keyEditorLabel(isDuplicate, newKey.text.toString()) }) {
                                            TextField(
                                                state = newKey,
                                                lineLimits = TextFieldLineLimits.SingleLine,
                                                modifier = Modifier.fillMaxWidth(),
                                                isError = isDuplicate,
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        if (reverseValueColumnOrder) {
                                            // 反转顺序：先显示 ItemStack，再显示 BlockInfo
                                            ItemStackMatcherDisplayerEditor(
                                                value = newItem,
                                                onValueChange = { newItem = it },
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                            BlockInfoMatcherDisplayerInnerEditor(
                                                value = newBlock,
                                                onValueChange = { newBlock = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                leadingIcon = blockInfoEditorLabel
                                            )
                                        } else {
                                            // 默认顺序：先显示 BlockInfo，再显示 ItemStack
                                            BlockInfoMatcherDisplayerInnerEditor(
                                                value = newBlock,
                                                onValueChange = { newBlock = it },
                                                modifier = Modifier.fillMaxWidth(),
                                                leadingIcon = blockInfoEditorLabel
                                            )
                                            ItemStackMatcherDisplayerEditor(
                                                value = newItem,
                                                onValueChange = { newItem = it },
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                        }
                                    }
                                
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (!isDuplicate) {
                                            val value = newBlock to newItem
                                            data.add(MapEntry(newKey.text.toString(), value))
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(LocalValueColumnWeight.current)
                        ) {
                            Row {
                                if (reverseValueColumnOrder) {
                                    // 反转：ItemStack 在左，BlockInfo 在右
                                    ItemStackMatcherDisplayerEditor(
                                        value = value.item,
                                        onValueChange = { onValueChange(value.copy(second = it)) },
                                        modifier = Modifier.weight(itemStackColumnWeight),
                                    )
                                    Spacer(Modifier.width(LocalColumnSpacing.current))
                                    BlockInfoMatcherDisplayerInnerEditor(
                                        value = value.block,
                                        onValueChange = { onValueChange(value.copy(first = it)) },
                                        modifier = Modifier.weight(blockInfoColumnWeight),
                                    )
                                } else {
                                    // 默认：BlockInfo 在左，ItemStack 在右
                                    BlockInfoMatcherDisplayerInnerEditor(
                                        value = value.block,
                                        onValueChange = { onValueChange(value.copy(first = it)) },
                                        modifier = Modifier.weight(blockInfoColumnWeight),
                                    )
                                    Spacer(Modifier.width(LocalColumnSpacing.current))
                                    ItemStackMatcherDisplayerEditor(
                                        value = value.item,
                                        onValueChange = { onValueChange(value.copy(second = it)) },
                                        modifier = Modifier.weight(itemStackColumnWeight),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

