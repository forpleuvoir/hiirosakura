package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

fun interface SyntaxScanner {
    fun scan(
        source: CharSequence,
        range: TextRange,
        emit: (SyntaxHighlightSpan) -> Unit,
    )
}
