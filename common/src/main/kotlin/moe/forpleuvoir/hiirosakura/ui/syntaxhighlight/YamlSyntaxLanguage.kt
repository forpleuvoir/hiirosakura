package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange
import moe.forpleuvoir.nebula.serialization.yml.YamlDialect

object YamlSyntaxLanguage : SyntaxLanguage {

    override val id: String = "yaml"

    override fun createScanner(): SyntaxScanner = YamlScanner

    private object YamlScanner : SyntaxScanner {

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
                    source[pos] == '&' -> {
                        val anchorStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] == '_' || source[pos] == '-')) pos++
                        emit(SyntaxHighlightSpan(TextRange(anchorStart, pos), SyntaxToken.Property, priority = 40))
                    }
                    source[pos] == '*' -> {
                        val aliasStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] == '_' || source[pos] == '-')) pos++
                        emit(SyntaxHighlightSpan(TextRange(aliasStart, pos), SyntaxToken.Property, priority = 40))
                    }
                    pos + 1 < end && source[pos] == '!' && source[pos + 1] == '!' -> {
                        val tagStart = pos
                        pos += 2
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] in "_-:")) pos++
                        emit(SyntaxHighlightSpan(TextRange(tagStart, pos), SyntaxToken.Annotation, priority = 50))
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
                    source[pos] == '|' || source[pos] == '>' -> {
                        val blockStart = pos
                        pos++
                        if (pos < end && (source[pos] == '+' || source[pos] == '-' || source[pos].isDigit())) pos++
                        emit(SyntaxHighlightSpan(TextRange(blockStart, pos), SyntaxToken.Operator, priority = 10))
                    }
                    source[pos] == '-' && (pos + 1 >= end || source[pos + 1] == ' ') -> {
                        if (pos == start || source[pos - 1] == '\n') {
                            emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                            pos++
                        } else {
                            pos++
                        }
                    }
                    source[pos].isLetter() || source[pos] == '_' -> {
                        val idStart = pos
                        pos++
                        while (pos < end && (source[pos].isLetterOrDigit() || source[pos] in "_-")) pos++
                        val word = source.subSequence(idStart, pos).toString()
                        when (word.lowercase()) {
                            "true", "false", "yes", "no", "on", "off" ->
                                emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Boolean, priority = 60))
                            "null", "~" ->
                                emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Null, priority = 60))
                            else -> {
                                if (pos < end && source[pos] == ':') {
                                    emit(SyntaxHighlightSpan(TextRange(idStart, pos), SyntaxToken.Property, priority = 40))
                                } else {
                                    pos--
                                    pos = scanPlainScalar(source, pos, end, emit)
                                }
                            }
                        }
                    }
                    source[pos] == '-' || source[pos] == '+' || source[pos].isDigit() -> {
                        val numStart = pos
                        pos++
                        while (pos < end && (source[pos].isDigit() || source[pos] in ".eE+-xXoO_")) pos++
                        emit(SyntaxHighlightSpan(TextRange(numStart, pos), SyntaxToken.Number, priority = 70))
                    }
                    pos < end && source[pos] == '~' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Null, priority = 60))
                        pos++
                    }
                    source[pos] == ':' && (pos + 1 >= end || source[pos + 1] == ' ') -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                    }
                    source[pos] == '-' && pos + 1 < end && source[pos + 1] == ' ' -> {
                        emit(SyntaxHighlightSpan(TextRange(pos, pos + 1), SyntaxToken.Punctuation, priority = 5))
                        pos++
                    }
                    else -> pos++
                }
            }
            validateByDecode(source.subSequence(start, end), YamlDialect).forEach { span ->
            emit(
                SyntaxHighlightSpan(
                    range = TextRange(span.range.start + start, span.range.end + start),
                    token = span.token,
                    layer = span.layer,
                    priority = span.priority,
                )
            )
        }
        }

        private fun scanPlainScalar(source: CharSequence, startPos: Int, end: Int, emit: (SyntaxHighlightSpan) -> Unit): Int {
            var pos = startPos
            while (pos < end && source[pos] != '\n' && source[pos] != '#' && source[pos] != ':') pos++
            if (pos > startPos) {
                val value = source.subSequence(startPos, pos).toString().trim()
                if (value.isNotEmpty()) {
                    val trimmedStart = startPos + source.subSequence(startPos, pos).indexOf(value.first())
                    val trimmedEnd = trimmedStart + value.length
                    when (value.lowercase()) {
                        "true", "false", "yes", "no", "on", "off" ->
                            emit(SyntaxHighlightSpan(TextRange(trimmedStart, trimmedEnd), SyntaxToken.Boolean, priority = 60))
                        "null", "~" ->
                            emit(SyntaxHighlightSpan(TextRange(trimmedStart, trimmedEnd), SyntaxToken.Null, priority = 60))
                    }
                }
            }
            return pos
        }
    }
}
