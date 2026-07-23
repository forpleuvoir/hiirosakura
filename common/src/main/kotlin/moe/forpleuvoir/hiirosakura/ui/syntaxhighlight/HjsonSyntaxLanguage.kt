package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

object HjsonSyntaxLanguage : SyntaxLanguage {

    override val id: String = "hjson"

    override fun createScanner(): SyntaxScanner = HjsonScanner

    private object HjsonScanner : SyntaxScanner {

        override fun scan(source: CharSequence, range: TextRange, emit: (SyntaxHighlightSpan) -> Unit) {
            val start = range.start.coerceAtLeast(0)
            val end = range.end.coerceAtMost(source.length)
            var pos = start
            while (pos < end) {
                when {
                    pos + 1 < end && source[pos] == '/' && source[pos + 1] == '/' -> {
                        val commentStart = pos
                        pos += 2
                        while (pos < end && source[pos] != '\n') pos++
                        emit(SyntaxHighlightSpan(TextRange(commentStart, pos), SyntaxToken.Comment, priority = 100))
                    }
                    pos + 1 < end && source[pos] == '/' && source[pos + 1] == '*' -> {
                        val commentStart = pos
                        pos += 2
                        while (pos + 1 < end && !(source[pos] == '*' && source[pos + 1] == '/')) pos++
                        if (pos + 1 < end) pos += 2
                        emit(SyntaxHighlightSpan(TextRange(commentStart, pos), SyntaxToken.Comment, priority = 100))
                    }
                    source[pos] == '#' -> {
                        val commentStart = pos
                        pos++
                        while (pos < end && source[pos] != '\n') pos++
                        emit(SyntaxHighlightSpan(TextRange(commentStart, pos), SyntaxToken.Comment, priority = 100))
                    }
                    pos + 2 < end && source[pos] == '\'' && source[pos + 1] == '\'' && source[pos + 2] == '\'' -> {
                        val strStart = pos
                        pos += 3
                        while (pos + 2 < end && !(source[pos] == '\'' && source[pos + 1] == '\'' && source[pos + 2] == '\'')) pos++
                        if (pos + 2 < end) pos += 3
                        emit(SyntaxHighlightSpan(TextRange(strStart, pos), SyntaxToken.String, priority = 90))
                    }
                    source[pos] == '"' -> {
                        val strStart = pos
                        pos++
                        while (pos < end && source[pos] != '"') {
                            if (source[pos] == '\\' && pos + 1 < end) {
                                val escStart = pos
                                pos += 2
                                emit(SyntaxHighlightSpan(TextRange(escStart, pos), SyntaxToken.EscapeSequence, priority = 200))
                            } else {
                                pos++
                            }
                        }
                        if (pos < end) pos++
                        emit(SyntaxHighlightSpan(TextRange(strStart, pos), SyntaxToken.String, priority = 90))
                    }
                    source[pos] == '-' || source[pos] == '+' || source[pos].isDigit() -> {
                        val numStart = pos
                        pos++
                        while (pos < end && (source[pos].isDigit() || source[pos] in ".eE+-xXa-fA-F_")) pos++
                        emit(SyntaxHighlightSpan(TextRange(numStart, pos), SyntaxToken.Number, priority = 70))
                    }
                    source[pos] == 't' && regionMatches(source, pos, "true") -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 4), SyntaxToken.Boolean, priority = 60))
                        pos += 4
                    }
                    source[pos] == 'f' && regionMatches(source, pos, "false") -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 5), SyntaxToken.Boolean, priority = 60))
                        pos += 5
                    }
                    source[pos] == 'n' && regionMatches(source, pos, "null") -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 4), SyntaxToken.Null, priority = 60))
                        pos += 4
                    }
                    source[pos].isLetter() || source[pos] == '_' -> {
                        val idStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] in "_-")) pos++
                        val word = source.subSequence(idStart, pos).toString()
                        if (word == "true" || word == "false") {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Boolean, priority = 60))
                        } else if (word == "null") {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Null, priority = 60))
                        } else {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Property, priority = 30))
                        }
                    }
                    source[pos] in "{}[],:" -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                    }
                    else -> pos++
                }
            }
        }
    }
}
