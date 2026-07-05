package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.default.Download
import moe.forpleuvoir.hiirosakura.ui.icon.default.Upload
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.FormatSelector
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.FormatSelectorDialog
import moe.forpleuvoir.hiirosakura.ui.widget.FormatImportExportButtonDefaults.currentFormatDialect
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IGCompositionLocalProvider
import moe.forpleuvoir.ibukigourd.ui.preset.StringSelector
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.serialization.ast.SyntaxDialect
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import moe.forpleuvoir.nebula.serialization.toml.TomlDialect
import moe.forpleuvoir.nebula.serialization.yml.YamlDialect
import kotlin.time.Duration.Companion.milliseconds

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormatSelectorDialog(
        onDismissRequest: () -> Unit,
        onConfirmRequest: () -> Unit,
    ) {
        AlertDialog(
            onDismissRequest,
            title = { Text(HSLang.Common.exportAsFormat) },
            text = { FormatSelector(Modifier.width(280.dp)) },
            confirmButton = { TextButton(onClick = { onConfirmRequest() }) { Text(IGLang.Misc.confirm) } },
            dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text(IGLang.Misc.cancel) } }
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
    TipBox({ Text(HSLang.Common.exportAsFormat) }) {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(Icons.Upload, null)
        }
    }
    if (expanded) {
        val scope = rememberCoroutineScope()
        FormatSelectorDialog(
            { expanded = false }
        ) {
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
        }
    }
}


@Composable
fun FormatImportButton(
    title: String,
    successMsg: String,
    decode: suspend (SerializeElement) -> Unit,
) {
    TipBox({
        Text(HSLang.Common.importFromFormat)
    }) {
        var expanded by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        IconButton({
            if (InputHandler.wasKeyPressed(Keyboard.LEFT_ALT)) {
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
            Icon(Icons.Download, null)
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

                    }) { Text(IGLang.Misc.confirm) }
                },
                dismissButton = { TextButton(onClick = { expanded = false }) { Text(IGLang.Misc.cancel) } },
                title = { Text(title) },
                text = {
                    IGCompositionLocalProvider {
                        Column {
                            FormatSelector(Modifier.fillMaxWidth())
                            Box {
                                var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                                val scrollState = rememberScrollState()
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
                                OutlinedTextField(
                                    state,
                                    scrollState = scrollState,
                                    label = {
                                        Text(cursorInfo)
                                    },
                                    onTextLayout = {
                                        it()?.let { textLayoutResult ->
                                            layoutResult = textLayoutResult
                                        }
                                    },
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(480.dp)
                                )
                                VerticalScrollbar(
                                    modifier = Modifier.align(Alignment.CenterEnd).padding(top = 14.dp, bottom = 6.dp, end = 4.dp).height(460.dp),
                                    adapter = rememberScrollbarAdapter(scrollState)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}


private fun String.truncateLines(maxLines: Int = 50): String {
    val lines = split("\n")
    return if (lines.size <= maxLines) this
    else lines.take(maxLines).joinToString("\n") + "\n... (total lines: ${lines.size})"
}