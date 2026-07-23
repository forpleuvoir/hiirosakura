package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

fun interface SyntaxHighlightRule {
    fun findMatches(
        source: CharSequence,
        range: TextRange,
        emit: (SyntaxHighlightSpan) -> Unit,
    )
}
