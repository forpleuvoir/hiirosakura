package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

object JexlSyntaxLanguage : SyntaxLanguage {

    override val id: String = "jexl"

    override fun createScanner(): SyntaxScanner = JexlScanner

    private fun isIdentPart(source: CharSequence, pos: Int, end: Int): Boolean =
        pos < end && (source[pos].isLetterOrDigit() || source[pos] == '_')

    private val jexlOperators = setOf(
        "==", "!=", "===", "!==", ">=", "<=", "&&", "||", "=~",
        "+", "-", "*", "/", "%", ">", "<", "!", "?:", "?",
    )

    private object JexlScanner : SyntaxScanner {

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
                    source[pos] == '\'' -> {
                        val strStart = pos
                        pos++
                        while (pos < end && source[pos] != '\'') {
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
                    pos + 1 < end && source[pos] == '0' && source[pos + 1] == 'x' -> {
                        val numStart = pos
                        pos += 2
                        while (pos < end && (source[pos].isDigit() || source[pos] in "a-fA-F")) pos++
                        emit(SyntaxHighlightSpan(TextRange(numStart, pos), SyntaxToken.Number, priority = 70))
                    }
                    pos + 1 < end && source[pos] == '0' && source[pos + 1] == 'o' -> {
                        val numStart = pos
                        pos += 2
                        while (pos < end && source[pos] in '0'..'7') pos++
                        emit(SyntaxHighlightSpan(TextRange(numStart, pos), SyntaxToken.Number, priority = 70))
                    }
                    source[pos] == '-' || source[pos] == '+' || source[pos].isDigit() -> {
                        if (!source[pos].isDigit() && (pos + 1 >= end || !source[pos + 1].isDigit())) {
                            emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Operator, priority = 10))
                            pos++
                        } else {
                            val numStart = pos
                            pos++
                            while (pos < end && (source[pos].isDigit() || source[pos] in ".eE_")) pos++
                            emit(SyntaxHighlightSpan(TextRange(numStart, pos), SyntaxToken.Number, priority = 70))
                        }
                    }
                    source[pos] == 't' && regionMatches(source, pos, "true") && !isIdentPart(source, pos + 4, end) -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 4), SyntaxToken.Boolean, priority = 60))
                        pos += 4
                    }
                    source[pos] == 'f' && regionMatches(source, pos, "false") && !isIdentPart(source, pos + 5, end) -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 5), SyntaxToken.Boolean, priority = 60))
                        pos += 5
                    }
                    source[pos] == 'n' && regionMatches(source, pos, "null") && !isIdentPart(source, pos + 4, end) -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 4), SyntaxToken.Null, priority = 60))
                        pos += 4
                    }
                    source[pos].isLetter() || source[pos] == '_' -> {
                        val idStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] == '_')) pos++
                        val word = source.subSequence(idStart, pos).toString()
                        when (word) {
                            "true" -> emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Boolean, priority = 60))
                            "false" -> emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Boolean, priority = 60))
                            "null" -> emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Null, priority = 60))
                            "var", "function", "if", "else", "for", "while", "return", "break", "continue", "throw", "empty", "size" ->
                                emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Keyword, priority = 60))
                            else -> emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Identifier, priority = 30))
                        }
                    }
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
                    pos + 1 < end && source[pos] == '=' && source[pos + 1] == '~' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 2), SyntaxToken.Operator, priority = 10))
                        pos += 2
                    }
                    pos + 1 < end && (
                        (source[pos] == '=' && source[pos + 1] == '=') ||
                        (source[pos] == '!' && source[pos + 1] == '=') ||
                        (source[pos] == '>' && source[pos + 1] == '=') ||
                        (source[pos] == '<' && source[pos + 1] == '=') ||
                        (source[pos] == '&' && source[pos + 1] == '&') ||
                        (source[pos] == '|' && source[pos + 1] == '|')
                    ) -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 2), SyntaxToken.Operator, priority = 10))
                        pos += 2
                    }
                    source[pos] == '?' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Operator, priority = 10))
                        pos++
                    }
                    source[pos] in "+-*/%><=!:&|^~" -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Operator, priority = 10))
                        pos++
                    }
                    source[pos] in "(){}[],;." -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                    }
                    else -> pos++
                }
            }
        }
    }
}
