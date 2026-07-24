package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig

object SyntaxHighlightDefaults {

    val lightTheme: SyntaxHighlightTheme = SyntaxHighlightTheme(
        plain = SpanStyle(color = Color(0xFF24292F)),
        styles = mapOf(
            SyntaxToken.Keyword to SpanStyle(color = Color(0xFFCF222E)),
            SyntaxToken.Identifier to SpanStyle(color = Color(0xFF953800)),
            SyntaxToken.Type to SpanStyle(color = Color(0xFF8250DF)),
            SyntaxToken.Function to SpanStyle(color = Color(0xFF8250DF)),
            SyntaxToken.Property to SpanStyle(color = Color(0xFF0550AE)),
            SyntaxToken.Number to SpanStyle(color = Color(0xFF0550AE)),
            SyntaxToken.String to SpanStyle(color = Color(0xFF0A3069)),
            SyntaxToken.Character to SpanStyle(color = Color(0xFF0A3069)),
            SyntaxToken.EscapeSequence to SpanStyle(color = Color(0xFF116329)),
            SyntaxToken.Comment to SpanStyle(color = Color(0xFF57606A)),
            SyntaxToken.Documentation to SpanStyle(color = Color(0xFF57606A)),
            SyntaxToken.Annotation to SpanStyle(color = Color(0xFF8250DF)),
            SyntaxToken.Boolean to SpanStyle(color = Color(0xFF0550AE)),
            SyntaxToken.Null to SpanStyle(color = Color(0xFF0550AE)),
            SyntaxToken.Operator to SpanStyle(color = Color(0xFF24292F)),
            SyntaxToken.Punctuation to SpanStyle(color = Color(0xFF24292F)),
            SyntaxToken.Error to SpanStyle(
                color = Color(0xFFCF222E),
                textDecoration = TextDecoration.Underline,
            ),
        ),
    )

    val darkTheme: SyntaxHighlightTheme = SyntaxHighlightTheme(
        plain = SpanStyle(color = Color(0xFFC9D1D9)),
        styles = mapOf(
            SyntaxToken.Keyword to SpanStyle(color = Color(0xFFFF7B72)),
            SyntaxToken.Identifier to SpanStyle(color = Color(0xFFFFA657)),
            SyntaxToken.Type to SpanStyle(color = Color(0xFFD2A8FF)),
            SyntaxToken.Function to SpanStyle(color = Color(0xFFD2A8FF)),
            SyntaxToken.Property to SpanStyle(color = Color(0xFF79C0FF)),
            SyntaxToken.Number to SpanStyle(color = Color(0xFF79C0FF)),
            SyntaxToken.String to SpanStyle(color = Color(0xFFA5D6FF)),
            SyntaxToken.Character to SpanStyle(color = Color(0xFFA5D6FF)),
            SyntaxToken.EscapeSequence to SpanStyle(color = Color(0xFF7EE787)),
            SyntaxToken.Comment to SpanStyle(color = Color(0xFF8B949E)),
            SyntaxToken.Documentation to SpanStyle(color = Color(0xFF8B949E)),
            SyntaxToken.Annotation to SpanStyle(color = Color(0xFFD2A8FF)),
            SyntaxToken.Boolean to SpanStyle(color = Color(0xFF79C0FF)),
            SyntaxToken.Null to SpanStyle(color = Color(0xFF79C0FF)),
            SyntaxToken.Operator to SpanStyle(color = Color(0xFFC9D1D9)),
            SyntaxToken.Punctuation to SpanStyle(color = Color(0xFFC9D1D9)),
            SyntaxToken.Error to SpanStyle(
                color = Color(0xFFFF7B72),
                textDecoration = TextDecoration.Underline,
            ),
        ),
    )

    val colorSchemes: SyntaxHighlightColorSchemes
        get() = SyntaxHighlightColorSchemes(lightTheme, darkTheme)

    @Composable
    fun theme(
        darkTheme: Boolean = !IGConfig.Gui.Theme.lightMode,
        colorSchemes: SyntaxHighlightColorSchemes = this.colorSchemes,
    ): SyntaxHighlightTheme =
        if (darkTheme) colorSchemes.dark else colorSchemes.light
}

data class SyntaxHighlightColorSchemes(
    val light: SyntaxHighlightTheme,
    val dark: SyntaxHighlightTheme,
)
