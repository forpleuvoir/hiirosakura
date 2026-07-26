package moe.forpleuvoir.hiirosakura.ui.configwrapper

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.hiirosakura.ui.editor.codeEditorShortcuts
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.JexlSyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightDefaults
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose.rememberSyntaxHighlightTransformation
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.EditNote
import moe.forpleuvoir.ibukigourd.ui.preset.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.nebula.config.Config

@Composable
fun CodeConfigWrapper(
    config: Config<String>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(InlineStyleText(config.translateText.plainText))
    },
    language: SyntaxLanguage = JexlSyntaxLanguage,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val textFieldState = rememberTextFieldState(config.getValue())

    ConfigRowWrapper(config, modifier, horizontalArrangement, verticalAlignment, onReset = {
        textFieldState.setTextAndPlaceCursorAtEnd(config.getValue())
    }) {
        var showDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.size(ConfigRowWrapper.entrySize),
            horizontalArrangement = Arrangement.spacedBy(ConfigRowWrapper.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            LaunchedEffect(textFieldState.text) {
                config.setValue(textFieldState.text.toString())
            }

            OutlinedTextField(
                state = textFieldState,
                labelPosition = TextFieldLabelPosition.Attached(true),
                label = { Text(language.id) },
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.EditNote, IGLang.Misc.edit.plainText)
            }
        }
        if (showDialog) {
            val state = rememberTextFieldState(config.getValue())
            FlexibleDialog(
                onDismissRequest = { showDialog = false },
                modifier = Modifier.padding(24.dp).heightIn(480.dp).fillMaxHeight(0.85f).widthIn(720.dp).fillMaxWidth(0.65f),
                title = editorDialogTitle,
                content = {
                    Box {
                        val scrollState = rememberScrollState()
                        OutlinedTextField(
                            state = state,
                            scrollState = scrollState,
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            ),
                            outputTransformation = rememberSyntaxHighlightTransformation(language, SyntaxHighlightDefaults.theme(), state.text.toString()),
                            modifier = Modifier.fillMaxSize()
                                .codeEditorShortcuts(state)
                        )
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(scrollState)
                        )
                    }
                },
                onConfirmRequest = {
                    config.setValue(state.text.toString())
                    textFieldState.setTextAndPlaceCursorAtEnd(state.text.toString())
                    true
                }
            )
        }
    }
}