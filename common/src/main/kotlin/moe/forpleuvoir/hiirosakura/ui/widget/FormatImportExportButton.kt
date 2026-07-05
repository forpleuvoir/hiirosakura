package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.coroutines.*
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.ui.icon.default.Download
import moe.forpleuvoir.hiirosakura.ui.icon.default.Upload
import moe.forpleuvoir.hiirosakura.ui.util.showErrorToast
import moe.forpleuvoir.hiirosakura.ui.util.showSuccessToast
import moe.forpleuvoir.ibukigourd.event.events.client.input.MouseEvent
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.Keyboard
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.platformcontext.MinecraftClipboard
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.TipBox
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.event.invoke
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

    var lastUsedFormat by mutableStateOf(0)

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
    fun interactionSource(): MutableInteractionSource {
        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()
        val registration = MouseEvent.Scrolling.register {
            if (hovered) cycleSelectedFormat(it.yoffset < 0)
        }
        DisposableEffect(Unit) {
            onDispose { registration() }
        }
        return interactionSource
    }

    @Composable
    fun formats() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            format.fastForEachIndexed { i, pair ->
                Text(
                    pair.first,
                    style = LocalTextStyle.current.copy(
                        background = if (i == lastUsedFormat)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else LocalTextStyle.current.background
                    )
                )
            }
        }
    }
}


@Composable
fun FormatExportButton(
    successMsg: String,
    encode: suspend (SyntaxDialect) -> String,
) {
    TipBox({ FormatImportExportButtonDefaults.formats() }) {
        val scope = rememberCoroutineScope()
        IconButton(interactionSource = FormatImportExportButtonDefaults.interactionSource(), onClick = {
            scope.launch {
                withContext(Dispatchers.IO) {
                    runCatching {
                        encode(FormatImportExportButtonDefaults.currentFormatDialect).let {
                            MinecraftClipboard.setClipboardText(it)
                            showSuccessToast(IGLang.Misc.copySuccess(successMsg).plainText)
                        }
                    }.onFailure {
                        showErrorToast(it.localizedMessage)
                    }
                }
            }
        }) {
            Icon(Icons.Upload, null)
        }
    }
}


@Composable
fun FormatImportButton(
    title: String,
    successMsg: String,
    decode: (SerializeElement) -> Unit,
) {
    TipBox({
        moe.forpleuvoir.ibukigourd.ui.preset.Text(HSLang.Common.importFromJson)
    }) {
        var expanded by remember { mutableStateOf(false) }
        IconButton({
            if (InputHandler.wasKeyPressed(Keyboard.LEFT_ALT)) {
                runCatching {
                    decode(JsonDialect.decode(mc.keyboardHandler.clipboard).getOrThrow())
                }.onSuccess {
                    showSuccessToast(successMsg)
                }.onFailure { error ->
                    showErrorToast(error.stackTraceToString().truncateLines(5))
                }
            } else {
                expanded = true
            }
        }) {
            Icon(Icons.Download, null)
        }
        if (expanded) {
            val state = rememberTextFieldState("")
            SimpleAlertDialog(
                { expanded = false },
                {
                    val result = runCatching {
                        decode(FormatImportExportButtonDefaults.currentFormatDialect.decode(state.text.toString()).getOrThrow())
                    }.onSuccess {
                        showSuccessToast(successMsg)
                    }.onFailure { error ->
                        showErrorToast(error.stackTraceToString().truncateLines(5))
                    }
                    result.isSuccess
                },
                title = { Text(title) },
                content = {
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
                                TipBox({ FormatImportExportButtonDefaults.formats() }) {
                                    Text(
                                        "f: ${FormatImportExportButtonDefaults.currentFormatType} ,c: $cursorInfo",
                                        modifier = Modifier.hoverable(FormatImportExportButtonDefaults.interactionSource())
                                    )
                                }
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
            )
        }
    }
}


private fun String.truncateLines(maxLines: Int = 50): String {
    val lines = split("\n")
    return if (lines.size <= maxLines) this
    else lines.take(maxLines).joinToString("\n") + "\n... (total lines: ${lines.size})"
}