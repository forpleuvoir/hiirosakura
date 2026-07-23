package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

interface SyntaxSpanPolicy {
    fun canNest(outer: SyntaxHighlightSpan, inner: SyntaxHighlightSpan): Boolean
}

object DefaultSyntaxSpanPolicy : SyntaxSpanPolicy {

    override fun canNest(outer: SyntaxHighlightSpan, inner: SyntaxHighlightSpan): Boolean {
        if (inner.token == SyntaxToken.EscapeSequence &&
            (outer.token == SyntaxToken.String || outer.token == SyntaxToken.Character)
        ) return true
        if (inner.layer.ordinal > outer.layer.ordinal) return true
        return false
    }
}

object SyntaxHighlightResolver {

    fun resolve(
        sourceLength: Int,
        spans: List<SyntaxHighlightSpan>,
        policy: SyntaxSpanPolicy = DefaultSyntaxSpanPolicy,
    ): List<SyntaxHighlightSpan> {
        if (sourceLength <= 0 || spans.isEmpty()) return emptyList()

        val valid = spans.filter { s ->
            s.range.start >= 0 && s.range.end <= sourceLength && s.range.start < s.range.end
        }
        if (valid.isEmpty()) return emptyList()

        val events = mutableListOf<Event>()
        valid.forEachIndexed { index, span ->
            events.add(Event(span.range.start, EventType.Open, span, index))
            events.add(Event(span.range.end, EventType.Close, span, index))
        }
        events.sortWith(
            compareBy({ it.offset }, { it.type.order }, { it.originalIndex })
        )

        val active = mutableListOf<IndexedSpan>()
        val result = mutableListOf<SyntaxHighlightSpan>()
        var lastPos = 0

        for (event in events) {
            if (event.offset > lastPos && active.isNotEmpty()) {
                val winner = selectWinner(active, policy)
                result.add(
                    SyntaxHighlightSpan(
                        range = TextRange(lastPos, event.offset),
                        token = winner.token,
                        layer = winner.layer,
                        priority = winner.priority,
                    )
                )
            }

            if (event.type == EventType.Close) {
                active.removeAll { it.originalIndex == event.originalIndex }
            } else {
                active.add(IndexedSpan(event.span, event.originalIndex))
            }

            lastPos = event.offset
        }

        return result
    }

    private fun selectWinner(
        active: List<IndexedSpan>,
        policy: SyntaxSpanPolicy,
    ): IndexedSpan {
        return active.maxWith(
            compareBy<IndexedSpan> { it.layer.ordinal }
                .thenByDescending { it.priority }
                .thenByDescending { it.range.length }
        )
    }

    private data class IndexedSpan(
        val span: SyntaxHighlightSpan,
        val originalIndex: Int,
    ) {
        val token get() = span.token
        val layer get() = span.layer
        val priority get() = span.priority
        val range get() = span.range
    }

    private enum class EventType(val order: Int) {
        Close(0),
        Open(1),
    }

    private data class Event(
        val offset: Int,
        val type: EventType,
        val span: SyntaxHighlightSpan,
        val originalIndex: Int,
    )
}
