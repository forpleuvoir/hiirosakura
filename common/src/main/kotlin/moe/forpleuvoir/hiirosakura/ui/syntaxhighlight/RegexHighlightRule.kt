package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

class RegexHighlightRule(
    val pattern: Regex,
    val token: SyntaxToken,
    val group: Int = 0,
    val layer: HighlightLayer = HighlightLayer.Syntax,
    val priority: Int = 0,
    val predicate: (source: CharSequence, match: MatchResult) -> Boolean = { _, _ -> true },
) : SyntaxHighlightRule {

    override fun findMatches(
        source: CharSequence,
        range: TextRange,
        emit: (SyntaxHighlightSpan) -> Unit,
    ) {
        val start = range.start.coerceAtLeast(0)
        val end = range.end.coerceAtMost(source.length)
        if (start >= end) return

        val sub = source.subSequence(start, end)
        for (match in pattern.findAll(sub)) {
            val groupRange = match.groups[group] ?: continue
            if (!predicate(source, match)) continue
            val actualStart = start + groupRange.range.first
            val actualEnd = start + groupRange.range.last + 1
            emit(
                SyntaxHighlightSpan(
                    range = TextRange(actualStart, actualEnd),
                    token = token,
                    layer = layer,
                    priority = priority,
                )
            )
        }
    }
}
