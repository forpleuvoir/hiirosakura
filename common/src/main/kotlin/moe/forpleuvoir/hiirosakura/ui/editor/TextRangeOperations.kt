package moe.forpleuvoir.hiirosakura.ui.editor

import androidx.compose.ui.text.TextRange

internal fun Char.isWordChar(): Boolean = isLetterOrDigit() || this == '_'

internal fun CharSequence.lineStartAt(offset: Int): Int {
    var pos = offset.coerceIn(0, length)
    while (pos > 0 && this[pos - 1] != '\n') pos--
    return pos
}

internal fun CharSequence.lineContentEndAt(offset: Int): Int {
    var pos = offset.coerceIn(0, length)
    while (pos < length && this[pos] != '\n') pos++
    return pos
}

internal fun CharSequence.lineEndIncludingBreakAt(offset: Int): Int {
    var pos = offset.coerceIn(0, length)
    while (pos < length && this[pos] != '\n') pos++
    if (pos < length) pos++
    return pos
}

internal fun CharSequence.lineBlockFor(selection: TextRange): TextRange {
    val min = minOf(selection.start, selection.end)
    val max = maxOf(selection.start, selection.end)
    val effectiveEnd = if (max > min) max - 1 else max
    val blockStart = lineStartAt(min)
    val blockEnd = lineEndIncludingBreakAt(effectiveEnd)
    return TextRange(blockStart, blockEnd)
}

internal fun CharSequence.wordRangeAt(offset: Int): TextRange? {
    val clamped = offset.coerceIn(0, length)
    if (clamped == 0 && isEmpty()) return null

    if (clamped < length && this[clamped].isWordChar()) {
        var start = clamped
        var end = clamped + 1
        while (start > 0 && this[start - 1].isWordChar()) start--
        while (end < length && this[end].isWordChar()) end++
        return TextRange(start, end)
    }

    if (clamped > 0 && this[clamped - 1].isWordChar()) {
        var start = clamped - 1
        while (start > 0 && this[start - 1].isWordChar()) start--
        return TextRange(start, clamped)
    }

    return null
}

internal fun CharSequence.firstNonWhitespaceOffset(lineStart: Int, lineContentEnd: Int): Int {
    val start = lineStart.coerceIn(0, length)
    val end = lineContentEnd.coerceIn(start, length)
    var pos = start
    while (pos < end && this[pos].isWhitespace()) pos++
    return pos
}

internal fun CharSequence.nonWhitespaceRangeAt(offset: Int): TextRange? {
    val clamped = offset.coerceIn(0, length)
    if (clamped >= length || this[clamped].isWhitespace()) return null

    var start = clamped
    var end = clamped + 1
    while (start > 0 && this[start - 1] != '\n' && !this[start - 1].isWhitespace()) start--
    while (end < length && this[end] != '\n' && !this[end].isWhitespace()) end++
    return TextRange(start, end)
}

internal fun CharSequence.leadingWhitespaceAt(offset: Int): String {
    val start = lineStartAt(offset)
    val contentEnd = lineContentEndAt(start)
    var pos = start
    while (pos < contentEnd && this[pos].isWhitespace()) pos++
    return substring(start, pos)
}

internal fun CharSequence.expandSelection(currentSelection: TextRange): TextRange? {
    val min = minOf(currentSelection.start, currentSelection.end)
    val max = maxOf(currentSelection.start, currentSelection.end)

    fun isStrictlyLarger(candidate: TextRange): Boolean {
        val cMin = minOf(candidate.start, candidate.end)
        val cMax = maxOf(candidate.start, candidate.end)
        return cMin != cMax && (cMin <= min && cMax >= max) && (cMin < min || cMax > max)
    }

    if (currentSelection.collapsed) {
        val word = wordRangeAt(min)
        if (word != null && isStrictlyLarger(word)) return word
    }

    if (min < length && !this[min].isWhitespace() && this[min] != '\n') {
        val nws = nonWhitespaceRangeAt(min)
        if (nws != null && isStrictlyLarger(nws)) return nws
    }

    val lineStart = lineStartAt(min)
    val lineContentEnd = lineContentEndAt(max)
    val lineRange = TextRange(lineStart, lineContentEnd)
    if (isStrictlyLarger(lineRange)) return lineRange

    val fullLineEnd = lineEndIncludingBreakAt(max)
    val fullLineRange = TextRange(lineStart, fullLineEnd)
    if (isStrictlyLarger(fullLineRange)) return fullLineRange

    val allRange = TextRange(0, length)
    if (isStrictlyLarger(allRange)) return allRange

    return null
}
