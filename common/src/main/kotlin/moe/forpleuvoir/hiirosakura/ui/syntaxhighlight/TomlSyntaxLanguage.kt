package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

object TomlSyntaxLanguage : SyntaxLanguage {

    override val id: String = "toml"

    override fun createScanner(): SyntaxScanner = TomlScanner

    private object TomlScanner : SyntaxScanner {

        override fun scan(source: CharSequence, range: TextRange, emit: (SyntaxHighlightSpan) -> Unit) {
            val start = range.start.coerceAtLeast(0)
            val end = range.end.coerceAtMost(source.length)
            var pos = start
            while (pos < end) {
                when {
                    source[pos] == '#' -> {
                        val commentStart = pos
                        pos++
                        while (pos < end && source[pos] != '\n') pos++
                        emit(SyntaxHighlightSpan(TextRange(commentStart, pos), SyntaxToken.Comment, priority = 100))
                    }
                    pos + 2 < end && source[pos] == '"' && source[pos + 1] == '"' && source[pos + 2] == '"' -> {
                        val strStart = pos
                        pos += 3
                        while (pos < end) {
                            if (source[pos] == '"' && pos + 2 < end && source[pos + 1] == '"' && source[pos + 2] == '"') {
                                pos += 3
                                break
                            }
                            if (source[pos] == '\\' && pos + 1 < end) {
                                val escStart = pos
                                pos += 2
                                emit(SyntaxHighlightSpan(TextRange(escStart, pos), SyntaxToken.EscapeSequence, priority = 200))
                            } else {
                                pos++
                            }
                        }
                        emit(SyntaxHighlightSpan(TextRange(strStart, pos), SyntaxToken.String, priority = 90))
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
                    source[pos] == '\'' -> {
                        val strStart = pos
                        pos++
                        while (pos < end && source[pos] != '\'') pos++
                        if (pos < end) pos++
                        emit(SyntaxHighlightSpan(TextRange(strStart, pos), SyntaxToken.String, priority = 90))
                    }
                    source[pos] == '+' || source[pos] == '-' || source[pos].isDigit() -> {
                        val numStart = pos
                        pos++
                        while (pos < end && (source[pos].isDigit() || source[pos] in ".eE+-_xXoObBa-fA-F")) pos++
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
                    pos + 1 < end && source[pos] == '[' && source[pos + 1] == '[' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 2), SyntaxToken.Punctuation, priority = 5))
                        pos += 2
                        val keyStart = pos
                        while (pos < end && source[pos] != ']') {
                            if (source[pos] == '"') {
                                pos++
                                while (pos < end && source[pos] != '"') { if (source[pos] == '\\') pos++ else pos++ }
                                if (pos < end) pos++
                            } else if (source[pos] == '\'') {
                                pos++
                                while (pos < end && source[pos] != '\'') { pos++ }
                                if (pos < end) pos++
                            } else {
                                pos++
                            }
                        }
                        val keyPart = source.subSequence(keyStart, pos)
                        if (keyPart.isNotEmpty()) {
                            emit(SyntaxHighlightSpan(TextRange(keyStart, pos), SyntaxToken.Property, priority = 40))
                        }
                        if (pos + 1 < end && source[pos] == ']' && source[pos + 1] == ']') {
                            emit(SyntaxHighlightSpan(TextRange(pos, pos + 2), SyntaxToken.Punctuation, priority = 5))
                            pos += 2
                        } else if (pos < end && source[pos] == ']') {
                            emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                            pos++
                        }
                    }
                    source[pos] == '[' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                        val keyStart = pos
                        while (pos < end && source[pos] != ']' && source[pos] != '\n') {
                            if (source[pos] == '"') {
                                pos++
                                while (pos < end && source[pos] != '"') { if (source[pos] == '\\') pos++ else pos++ }
                                if (pos < end) pos++
                            } else if (source[pos] == '\'') {
                                pos++
                                while (pos < end && source[pos] != '\'') { pos++ }
                                if (pos < end) pos++
                            } else {
                                pos++
                            }
                        }
                        val keyPart = source.subSequence(keyStart, pos)
                        if (keyPart.isNotEmpty()) {
                            emit(SyntaxHighlightSpan(TextRange(keyStart, pos), SyntaxToken.Property, priority = 40))
                        }
                        if (pos < end && source[pos] == ']') {
                            emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                            pos++
                        }
                    }
                    source[pos].isLetter() || source[pos] == '_' -> {
                        val idStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] == '_' || source[pos] == '-')) pos++
                        val word = source.subSequence(idStart, pos).toString()
                        if (word == "true" || word == "false") {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Boolean, priority = 60))
                        } else if (word == "nan" || word == "inf") {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Number, priority = 70))
                        } else {
                            emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Property, priority = 30))
                        }
                    }
                    source[pos] == '=' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Operator, priority = 10))
                        pos++
                    }
                    source[pos] == '.' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                    }
                    else -> pos++
                }
            }
        }
    }
}
