package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.nebula.serialization.ast.SyntaxDialect
import moe.forpleuvoir.nebula.serialization.ast.SyntaxReadException

internal fun regionMatches(source: CharSequence, offset: Int, target: String): Boolean {
    if (offset + target.length > source.length) return false
    for (i in target.indices) {
        if (source[offset + i] != target[i]) return false
    }
    return true
}

internal fun validateByDecode(
    source: CharSequence,
    dialect: SyntaxDialect,
): List<SyntaxHighlightSpan> {
    val error = dialect.decode(source.toString()).exceptionOrNull() ?: return emptyList()
    val raw = if (error is SyntaxReadException) error.pos.offset else -1
    if (raw !in 0 until source.length) return emptyList()
    return listOf(
        SyntaxHighlightSpan(
            range = TextRange(raw, (raw + 1).coerceAtMost(source.length)),
            token = SyntaxToken.Error,
            layer = HighlightLayer.Diagnostic,
            priority = 1000,
        )
    )
}
