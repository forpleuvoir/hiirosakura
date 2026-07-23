package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight

internal fun regionMatches(source: CharSequence, offset: Int, target: String): Boolean {
    if (offset + target.length > source.length) return false
    for (i in target.indices) {
        if (source[offset + i] != target[i]) return false
    }
    return true
}
