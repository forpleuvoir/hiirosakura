package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

import androidx.compose.ui.text.TextRange

class RegexSyntaxLanguage(
    override val id: String,
    private val rules: List<SyntaxHighlightRule>,
) : SyntaxLanguage {

    override fun createScanner(): SyntaxScanner = RegexSyntaxScanner(rules)
}

private class RegexSyntaxScanner(
    private val rules: List<SyntaxHighlightRule>,
) : SyntaxScanner {

    override fun scan(source: CharSequence, range: TextRange, emit: (SyntaxHighlightSpan) -> Unit) {
        for (rule in rules) {
            rule.findMatches(source, range, emit)
        }
    }
}
