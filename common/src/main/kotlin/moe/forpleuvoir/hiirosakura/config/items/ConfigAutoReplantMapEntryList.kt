package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerEditor
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.hiirosakura.ui.configwrapper.ListConfigWrapperDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.rememberKeyedList
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList

context(group: ConfigGroup)
fun configAutoReplantEntryList(name: String, defaultValue: List<AutoReplant.Entry>) = configList(name, defaultValue, AutoReplant.Entry)


//------------ UI Wrapper ------------\\

@Composable
fun AutoReplantEntryListConfigWrapper(
    config: ConfigList<AutoReplant.Entry>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(component = InlineStyleText(config.translateText.plainText))
    },
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier.padding(40.dp).size(1200.dp, 800.dp),
) = ListConfigWrapperDefaults.run {
    CompositionLocalProvider(
        ItemBrowserDefaults.LocalItemIconSize provides 32.dp
    ) {
        var showEditDialog by remember { mutableStateOf(false) }
        RowWrapper(
            config = config,
            modifier = modifier
        ) { showEditDialog = true }

        if (showEditDialog) {
            val editingValue = rememberKeyedList(config)
            EditDialog(
                config = config,
                editingValue = editingValue,
                modifier = dialogModifier,
                title = editorDialogTitle,
                onDismissRequest = { showEditDialog = false },
                onConfirmRequest = {
                    config.clear()
                    it.entries.forEach { (_, value) -> config.add(value) }
                    true
                }
            ) {
                EditDialogContent(
                    modifier = Modifier.fillMaxSize(),
                    header = {
                        EditDialogContentHeader(
                            contentHeader = {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current)) {
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(component = HSLang.AutoReplant.mapEntryTargetBlock)
                                    }

                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(component = HSLang.AutoReplant.mapEntryReplantItem)
                                    }

                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(component = HSLang.AutoReplant.mapEntryGroundBlock)
                                    }
                                }
                            }
                        )
                    },
                    addDialog = { onDismissRequest ->
                        var entry by remember { mutableStateOf(AutoReplant.Entry()) }
                        SimpleAlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(component = IGLang.Misc.add) },
                            onConfirmRequest = {
                                editingValue.add(entry)
                                true
                            },
                            content = {
                                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    BlockInfoMatcherDisplayerInnerEditor(
                                        entry.targetBlock,
                                        { entry = entry.copy(targetBlock = it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                            Text(component = HSLang.AutoReplant.mapEntryTargetBlock)
                                        }
                                    )
                                    ItemStackMatcherDisplayerEditor(
                                        entry.replantItem,
                                        { entry = entry.copy(replantItem = it) },
                                        modifier = Modifier.fillMaxWidth(),
//                                        leadingIcon = {
//                                            Text(component = HSLang.AutoReplant.mapEntryReplantItem)
//                                        }
                                    )
                                    BlockInfoMatcherDisplayerInnerEditor(
                                        entry.groundBlock,
                                        { entry = entry.copy(groundBlock = it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                            Text(component = HSLang.AutoReplant.mapEntryGroundBlock)
                                        }
                                    )
                                }
                            }
                        )
                    }
                ) { lazyListState ->
                    EditDialogContentList(
                        data = editingValue,
                        modifier = Modifier,
                        lazyListState = lazyListState
                    ) { value, onValueChange ->
                        Row(
                            Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //targetBlock
                            BlockInfoMatcherDisplayerInnerEditor(
                                value.targetBlock,
                                { onValueChange(value.copy(targetBlock = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //replantItem
                            ItemStackMatcherDisplayerEditor(
                                value.replantItem,
                                { onValueChange(value.copy(replantItem = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //groundBlock
                            BlockInfoMatcherDisplayerInnerEditor(
                                value.groundBlock,
                                { onValueChange(value.copy(groundBlock = it)) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}