package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

enum class HighlightLayer {
    Base,
    Syntax,
    Embedded,
    Diagnostic,
}

data class SyntaxHighlightSpan(
    val range: TextRange,
    val token: SyntaxToken,
    val layer: HighlightLayer = HighlightLayer.Syntax,
    val priority: Int = 0,
)
