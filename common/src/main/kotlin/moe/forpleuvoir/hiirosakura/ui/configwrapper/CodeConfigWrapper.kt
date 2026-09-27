package moe.forpleuvoir.hiirosakura.ui.configwrapper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.forpleuvoir.compose_minecraft.platform.ui.text.MinecraftFonts
import moe.forpleuvoir.compose_minecraft.platform.ui.text.withDefaultFont
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlexibleDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.nebula.config.Config
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextField
import moe.forpleuvoir.hiirosakura.ui.widget.OutlinedLabelBox
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalScroller
import moe.forpleuvoir.ibukigourd.ui.sokitsu.rememberScrollerAdapter
import moe.forpleuvoir.hiirosakura.ui.configwrapper.ConfigRowWrapperCompat
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightTheme
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlBlock
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigDialogTitle
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringEditDialog
import moe.forpleuvoir.ibukigourd.ui.configwrapper.StringValueField
import moe.forpleuvoir.ibukigourd.ui.configwrapper.asState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.LocalTextStyle
import moe.forpleuvoir.ibukigourd.ui.sokitsu.VerticalFlatScroller
import net.minecraft.network.chat.Style

@Composable
fun CodeConfigWrapper(
    config: Config<String>,
    editorDialogTitle: @Composable (() -> Unit)? = {
        Text(component = InlineStyleText(config.translateText.plainText))
    },
    language: SyntaxLanguage = JexlSyntaxLanguage,
    modifier: Modifier = Modifier,
) {
    val value by config.asState()
    var editing by remember(config) { mutableStateOf(false) }

    ConfigRowWrapper(config, modifier) {
        ConfigControlBlock(
            action = {
                IconButton(
                    onClick = { editing = true },
                    contentPadding = ConfigControlDefaults.IconButtonPadding,
                ) {
                    Icon(Icons.Edit, scale = configIconScale())
                }
            },
        ) {
            StringValueField(
                value = value,
                onValueChange = { config.setValue(it) },
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (editing) {
        val state = rememberTextFieldState(config.getValue())
        FlexibleDialog(
            onDismissRequest = { editing = false },
            modifier = Modifier.padding(24.dp).heightIn(480.dp).fillMaxHeight(0.85f).widthIn(720.dp).fillMaxWidth(0.65f),
            title = editorDialogTitle,
            content = {
                val scrollState = rememberScrollState()
                TextField(
                    state = state,
                    lineLimits = TextFieldLineLimits.Default,
                    textStyle = Style.EMPTY.withFont(MinecraftFonts.FusionPixelMono),
                    //TODO 有bug不生效
                    outputTransformation = rememberSyntaxHighlightTransformation(language, SyntaxHighlightDefaults.theme()),
                    modifier = Modifier.fillMaxSize().codeEditorShortcuts(state),
                    trailingIcon = {
                        VerticalFlatScroller(
                            modifier = Modifier,
                            adapter = rememberScrollerAdapter(scrollState),
                            autoHide = true,
                            autoFade = true,
                        )
                    }
                )
            },
            onConfirmRequest = {
                config.setValue(state.text.toString())
                true
            }
        )
    }
}
