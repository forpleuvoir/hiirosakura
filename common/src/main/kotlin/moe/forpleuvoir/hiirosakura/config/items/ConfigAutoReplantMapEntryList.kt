package moe.forpleuvoir.hiirosakura.config.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.AutoReplant
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.hiirosakura.ui.widget.ItemBrowserDefaults
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo.BlockInfoMatcherDisplayerInnerEditor
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.itemstack.ItemStackMatcherDisplayerInnerEditor
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ListConfigWrapperDefaults
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.ConfigList
import moe.forpleuvoir.nebula.config.item.configList
import kotlin.collections.forEach

context(group: ConfigGroup)
fun configAutoReplantEntryList(name: String, defaultValue: List<AutoReplant.Entry>) = configList(name, defaultValue, AutoReplant.Entry)


//------------ UI Wrapper ------------\\

@Composable
fun AutoReplantEntryListConfigWrapper(
    config: ConfigList<AutoReplant.Entry>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(InlineStyleText(config.translateText.plainText))
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
            val editingValue = remember {
                config.mapIndexed { index, value -> index.toLong() to value }.toMutableStateList()
            }
            var nextKey by remember { mutableLongStateOf(editingValue.size.toLong()) }
            EditDialog(
                config = config,
                editingValue = editingValue,
                modifier = dialogModifier,
                title = editorDialogTitle,
                onDismissRequest = { showEditDialog = false },
                onConfirmRequest = {
                    config.clear()
                    it.forEach { (_, value) -> config.add(value) }
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
                                        Text(HSLang.AutoReplant.mapEntryTargetBlock)
                                    }
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(HSLang.AutoReplant.mapEntryReplantItem)
                                    }
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text(HSLang.AutoReplant.mapEntryGroundBlock)
                                    }
                                }
                            }
                        )
                    },
                    addDialog = { onDismissRequest ->
                        var entry by remember { mutableStateOf(AutoReplant.Entry()) }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(IGLang.Misc.add) },
                            text = {
                                IGCompositionLocalProvider {
                                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        BlockInfoMatcherDisplayerInnerEditor(
                                            entry.targetBlock,
                                            { entry = entry.copy(targetBlock = it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Text(HSLang.AutoReplant.mapEntryTargetBlock)
                                            }
                                        )
                                        ItemStackMatcherDisplayerInnerEditor(
                                            entry.replantItem,
                                            { entry = entry.copy(replantItem = it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Text(HSLang.AutoReplant.mapEntryReplantItem)
                                            }
                                        )
                                        BlockInfoMatcherDisplayerInnerEditor(
                                            entry.groundBlock,
                                            { entry = entry.copy(groundBlock = it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Text(HSLang.AutoReplant.mapEntryGroundBlock)
                                            }
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        editingValue.add(nextKey++ to entry)
                                        onDismissRequest()
                                    }
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
                        data = editingValue,
                        key = { it.first },
                        modifier = Modifier,
                        lazyListState = lazyListState
                    ) { (key, value), onValueChange ->
                        Row(
                            Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(LocalColumnSpacing.current),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //targetBlock
                            BlockInfoMatcherDisplayerInnerEditor(
                                value.targetBlock,
                                { onValueChange(key to value.copy(targetBlock = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //replantItem
                            ItemStackMatcherDisplayerInnerEditor(
                                value.replantItem,
                                { onValueChange(key to value.copy(replantItem = it)) },
                                modifier = Modifier.weight(1f),
                            )
                            //groundBlock
                            BlockInfoMatcherDisplayerInnerEditor(
                                value.groundBlock,
                                { onValueChange(key to value.copy(groundBlock = it)) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}