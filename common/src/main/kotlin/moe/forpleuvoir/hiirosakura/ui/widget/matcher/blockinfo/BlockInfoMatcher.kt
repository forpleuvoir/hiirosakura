package moe.forpleuvoir.hiirosakura.ui.widget.matcher.blockinfo

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.ui.widget.FormatExportButton
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportButton
import moe.forpleuvoir.hiirosakura.ui.widget.matcher.*
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.util.mc

@Composable
fun BasicBlockInfoMatcherEditor(
    value: BlockInfoMatcher,
    onValueChange: (BlockInfoMatcher) -> Unit,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositeMatcherModeSelector(
                value.mode,
                { onValueChange(value.copy(mode = it)) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                mc.targetBlock?.let { targetBlock ->
                    TestButton(
                        HSLang.BlockInfoMatcher.testButton.plainText,
                        HSLang.BlockInfoMatcher.testSuccess.plainText,
                        HSLang.BlockInfoMatcher.testFailed.plainText
                    ) {
                        value.match(targetBlock)
                    }
                }
                FormatExportButton(IGLang.Misc.copySuccess(HSLang.BlockInfoMatcher.title).plainText) {
                    MinecraftClipboard.setClipboardText(it.encode(BlockInfoMatcher.serialization(value)))
                }
                FormatImportButton(HSLang.BlockInfoMatcher.title.plainText, HSLang.Common.success.plainText) {
                    BlockInfoMatcher.deserialization(it)
                        .getOrThrow()
                        .let { onValueChange(it) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxSize()) {
            val scrollState = rememberScrollState()

            Column(Modifier.verticalScroll(scrollState).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                value.entries.forEachIndexed { index, entry ->
                    BlockInfoMatchEntryRow(
                        modifier = Modifier.fillMaxWidth(),
                        entry = entry,
                        onChange = { newEntry ->
                            onValueChange(value.copy(entries = value.entries.map { if (it === entry) newEntry else it }))
                        },
                        onRemove = {
                            onValueChange(value.copy(entries = value.entries - entry))
                        }
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            FloatingEntryAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                scrollState = scrollState,
                addMenuOptions = if (isNested) nestedAddMenuOptions else addMenuOptions
            ) { newEntry ->
                onValueChange(value.copy(entries = value.entries + newEntry))
            }
        }
    }
}

@Composable
fun BlockInfoMatcherEditorDialog(
    onDismissRequest: () -> Unit,
    value: BlockInfoMatcher,
    onValueChange: (BlockInfoMatcher) -> Unit,
) {
    var editingMatcher by remember(value) { mutableStateOf(value) }
    FlexibleDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(HSLang.BlockInfoMatcher.title) },
        content = {
            BasicBlockInfoMatcherEditor(
                editingMatcher,
                { editingMatcher = it },
                modifier = Modifier.size(LocalMatcherDialogContentSize.current)
            )
        },
        onConfirmRequest = {
            onValueChange(editingMatcher)
            true
        }
    )
}

//region EntryRow

@Composable
private fun BlockInfoMatchEntryRow(
    entry: BlockInfoMatchEntry,
    onChange: (BlockInfoMatchEntry) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) = MatchEntryRow(
    title = entry.translateText,
    entry = entry,
    entryCopyWithMode = { e, m -> e.copyWithMode(m) },
    onChange = onChange,
    onRemove = onRemove,
    modifier = modifier
) {
    when (entry) {
        is BlockInfoMatchEntry.Matcher -> BlockInfoMatchEntryMatcherRow(entry, onChange)
        is BlockInfoMatchEntry.Block -> BlockInfoMatchEntryBlockRow(entry, onChange)
        is BlockInfoMatchEntry.Script -> BlockInfoMatchEntryScriptRow(entry, onChange)
        is BlockInfoMatchEntry.Pos -> BlockInfoMatchEntryPosRow(entry, onChange)
        is BlockInfoMatchEntry.Tag -> BlockInfoMatchEntryTagRow(entry, onChange)
        is BlockInfoMatchEntry.Property -> BlockInfoMatchEntryPropertyRow(entry, onChange)
    }
}

//endregion


//region Adder


private val nestedAddMenuOptions: List<AddMenuOption<BlockInfoMatchEntry>> = listOf(
    AddMenuOption(BlockInfoMatchEntry.Block.title) { addAction, onDismiss ->
        BlockInfoMatchEntryBlockEditorDialog(onDismiss, BlockInfoMatchEntry.Block.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Script.title) { addAction, onDismiss ->
        BlockInfoMatchEntryScriptEditorDialog(onDismiss, BlockInfoMatchEntry.Script.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Pos.title) { addAction, onDismiss ->
        BlockInfoMatchEntryPosEditorDialog(onDismiss, BlockInfoMatchEntry.Pos.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Tag.title) { addAction, onDismiss ->
        BlockInfoMatchEntryTagEditorDialog(onDismiss, BlockInfoMatchEntry.Tag.default, addAction)
    },
    AddMenuOption(BlockInfoMatchEntry.Property.title) { addAction, onDismiss ->
        BlockInfoMatchEntryPropertyEditorDialog(onDismiss, BlockInfoMatchEntry.Property.default, addAction)
    }
)

private val addMenuOptions: List<AddMenuOption<BlockInfoMatchEntry>> =
    nestedAddMenuOptions + AddMenuOption(BlockInfoMatchEntry.Matcher.title) { addAction, onDismiss ->
        BlockInfoMatchEntryMatcherEditorDialog(onDismiss, BlockInfoMatchEntry.Matcher.default, addAction)
    }
//endregion