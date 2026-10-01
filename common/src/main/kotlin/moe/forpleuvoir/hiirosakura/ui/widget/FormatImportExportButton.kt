package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.*
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.FormatSelector
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.FormatSelectorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.currentFormatDialect
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.format
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.lastUsedFormat
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentDialogTitle
import moe.forpleuvoir.ibukigourd.lang.IGLang

import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.util.isQuickAction
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.network.chat.Component
import moe.forpleuvoir.nebula.serialization.ast.SyntaxDialect
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import moe.forpleuvoir.nebula.serialization.toml.TomlDialect
import moe.forpleuvoir.nebula.serialization.yaml.YamlDialect
import kotlin.time.Duration.Companion.milliseconds
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.AlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons

object FormatImportExportButtonDefaults {

    val format = listOf(
        "Json" to JsonDialect,
        "Hjson" to HJsonDialect,
        "Yaml" to YamlDialect,
        "Toml" to TomlDialect
    )

    var lastUsedFormat by mutableIntStateOf(0)
        private set

    fun setCurrentUsedFormat(index: Int) {
        lastUsedFormat = index.coerceIn(0, format.lastIndex)
    }


    val currentFormatType get() = format[lastUsedFormat].first
    val currentFormatDialect get() = format[lastUsedFormat].second

    fun cycleSelectedFormat(down: Boolean = true) {
        if (down) {
            lastUsedFormat += 1
        } else
            lastUsedFormat -= 1
        if (lastUsedFormat > format.lastIndex) lastUsedFormat = 0
        if (lastUsedFormat < 0) lastUsedFormat = format.lastIndex
    }

    @Composable
    fun FormatSelectorDialog(
        onDismissRequest: () -> Unit,
        onConfirmRequest: () -> Unit,
    ) {
        AlertDialog(
            onDismissRequest,
            title = { DataComponentDialogTitle(component = HSLang.Common.exportAsFormat) },
            text = { FormatSelector(Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { onConfirmRequest() }) { Text(component = IGLang.Misc.confirm) } },
            dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text(component = IGLang.Misc.cancel) } }
        )
    }

    @Composable
    fun FormatSelector(modifier: Modifier = Modifier) {
        StringSelector(
            currentFormatType,
            { s ->
                setCurrentUsedFormat(format.indexOfFirst { it.first == s })
            },
            items = format.map { it.first },
            modifier = modifier
        )
    }
}


@Composable
fun FormatExportButton(
    successMsg: String,
    encode: suspend (SyntaxDialect) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            if (isQuickAction) {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            encode(currentFormatDialect)
                        }.onFailure {
                            showErrorToast(it.localizedMessage)
                        }.onSuccess {
                            showSuccessToast(successMsg)
                            expanded = false
                        }
                    }
                }
            } else expanded = true
        },
        modifier = Modifier.tooltip { Text(component = HSLang.Common.exportAsFormat) },
    ) {
        Icon(Icons.Export)
    }

    if (expanded) {
        FormatSelectorDialog(
            onDismissRequest = { expanded = false },
            onConfirmRequest = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            encode(currentFormatDialect)
                        }.onFailure {
                            showErrorToast(it.localizedMessage)
                        }.onSuccess {
                            showSuccessToast(successMsg)
                            expanded = false
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun FormatImportButton(
    title: String,
    successMsg: String,
    decode: suspend (SerializeElement) -> Unit,
) {
    Box(Modifier.tooltip {
        Text(component = HSLang.Common.importFromFormat)
    }) {
        var expanded by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()
        IconButton({
            if (isQuickAction) {
                scope.launch {
                    runCatching {
                        decode(JsonDialect.decode(mc.keyboardHandler.clipboard).getOrThrow())
                    }.onSuccess {
                        showSuccessToast(successMsg)
                    }.onFailure { error ->
                        showErrorToast(error.stackTraceToString().truncateLines(5))
                    }
                }
            } else {
                expanded = true
            }
        }) {
            Icon(Icons.Import)
        }

        if (expanded) {
            val state = rememberTextFieldState("")
            AlertDialog(
                { expanded = false },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            runCatching {
                                decode(currentFormatDialect.decode(state.text.toString()).getOrThrow())
                            }.onSuccess {
                                showSuccessToast(successMsg)
                                expanded = false
                            }.onFailure { error ->
                                showErrorToast(error.stackTraceToString().truncateLines(5))
                            }
                        }

                    }) { Text(component = IGLang.Misc.confirm) }
                },
                dismissButton = { TextButton(onClick = { expanded = false }) { Text(component = IGLang.Misc.cancel) } },
                title = { DataComponentDialogTitle(Component.literal(title)) },
                text = {

                    Column {
                        FormatSelector(Modifier.fillMaxWidth())
                        Box {
                            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

                            val scrollState = rememberScrollState()
                            // 文本区与其右侧滚动条共用的高度：滚动条要与文本区等高
                            val editorHeight = 480.dp
                            var cursorInfo by remember { mutableStateOf("") }
                            LaunchedEffect(Unit) {
                                while (isActive) {
                                    cursorInfo = layoutResult?.let { layout ->
                                        val start = state.selection.start
                                        val end = state.selection.end
                                        val startLine = layout.getLineForOffset(start)
                                        val startColumn = start - layout.getLineStart(startLine)
                                        if (start != end) {
                                            val endLine = layout.getLineForOffset(end)
                                            val endColumn = end - layout.getLineStart(endLine)
                                            "${startLine + 1}:${startColumn + 1} .. ${endLine + 1}:${endColumn + 1}"
                                        } else "${startLine + 1}:${startColumn + 1}"
                                    } ?: "1:1"
                                    delay(50.milliseconds)
                                }
                            }

                            val transformation = rememberSyntaxHighlightTransformation(
                                language = when (format[lastUsedFormat].first) {
                                    "Json" -> JsonSyntaxLanguage
                                    "Hjson" -> HjsonSyntaxLanguage
                                    "Yaml" -> YamlSyntaxLanguage
                                    "Toml" -> TomlSyntaxLanguage
                                    else -> throw NotImplementedError()
                                },
                                theme = SyntaxHighlightDefaults.theme(),
                                text = state.text.toString(),
                            )
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(Modifier.padding(start = 12.dp)) { Text(cursorInfo) }
                                    Spacer(Modifier.height(2.dp))
                                    TextField(
                                        state,

                                        modifier = Modifier.fillMaxWidth().height(editorHeight).codeEditorShortcuts(state),
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                VerticalFlatScroller(
                                    modifier = Modifier.height(editorHeight),
                                    adapter = rememberScrollerAdapter(scrollState)
                                )
                            }
                        }
                    }

                }
            )
        }
    }
}


fun String.truncateLines(maxLines: Int = 50): String {
    val lines = split("\n")
    return if (lines.size <= maxLines) this
    else lines.take(maxLines).joinToString("\n") + "\n... (total lines: ${lines.size})"
}