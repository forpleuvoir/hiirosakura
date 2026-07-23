package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange

object SyntaxHighlighter {

    fun highlight(
        source: CharSequence,
        language: SyntaxLanguage,
        range: TextRange = TextRange(0, source.length),
    ): List<SyntaxHighlightSpan> {
        val scanStart = range.start.coerceIn(0, source.length)
        val scanEnd = range.end.coerceIn(0, source.length)
        val scanRange = TextRange(scanStart, scanEnd)
        if (scanStart >= scanEnd) return emptyList()

        val scanner = language.createScanner()
        val collected = mutableListOf<SyntaxHighlightSpan>()
        scanner.scan(source, scanRange) { span -> collected.add(span) }

        return SyntaxHighlightResolver.resolve(source.length, collected)
    }

    fun buildSyntaxHighlightedText(
        text: String,
        spans: List<SyntaxHighlightSpan>,
        theme: SyntaxHighlightTheme,
    ): AnnotatedString {
        if (text.isEmpty()) return AnnotatedString("")

        val builder = AnnotatedString.Builder(text)

        builder.addStyle(
            SpanStyle(color = theme.plain.color),
            0,
            text.length,
        )

        for (span in spans) {
            val style = theme.styleOf(span.token)
            val start = span.range.start.coerceIn(0, text.length)
            val end = span.range.end.coerceIn(0, text.length)
            if (start < end) {
                builder.addStyle(style, start, end)
            }
        }

        return builder.toAnnotatedString()
    }

    fun syntaxHighlightedText(
        text: String,
        language: SyntaxLanguage,
        theme: SyntaxHighlightTheme,
    ): AnnotatedString {
        val spans = highlight(text, language)
        return buildSyntaxHighlightedText(text, spans, theme)
    }
}
