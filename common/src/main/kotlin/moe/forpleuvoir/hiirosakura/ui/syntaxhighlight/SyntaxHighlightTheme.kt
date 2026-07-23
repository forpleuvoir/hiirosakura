package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.SpanStyle

class SyntaxHighlightTheme(
    val plain: SpanStyle,
    styles: Map<SyntaxToken, SpanStyle>,
) {
    private val styles = styles.toMap()

    fun styleOf(token: SyntaxToken): SpanStyle = styles[token] ?: plain

    fun withStyles(vararg styles: Pair<SyntaxToken, SpanStyle>): SyntaxHighlightTheme {
        val newStyles = this.styles.toMutableMap()
        for ((token, style) in styles) {
            newStyles[token] = style
        }
        return SyntaxHighlightTheme(plain, newStyles)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SyntaxHighlightTheme) return false
        return plain == other.plain && styles == other.styles
    }

    override fun hashCode(): Int = 31 * plain.hashCode() + styles.hashCode()

    override fun toString(): String = "SyntaxHighlightTheme(plain=$plain, styles=$styles)"
}
