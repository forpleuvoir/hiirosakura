package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

interface SyntaxLanguage {
    val id: String
    fun createScanner(): SyntaxScanner
}
