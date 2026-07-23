package moe.forpleuvoir.hiirosakura.editor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange

internal data class TextEdit(
    val start: Int,
    val deleteLength: Int,
    val insertText: String,
) {
    val netChange: Int get() = insertText.length - deleteLength
}

internal fun mapOffsetAfterEdits(
    originalOffset: Int,
    edits: List<TextEdit>,
): Int {
    var offset = originalOffset
    for (edit in edits.sortedByDescending { it.start }) {
        val deleteEnd = edit.start + edit.deleteLength
        when {
            offset >= deleteEnd -> {
                offset += edit.netChange
            }
            offset > edit.start -> {
                offset = edit.start
            }
        }
    }
    return offset
}

private fun CharSequence.hasPrefixAt(prefix: String, start: Int): Boolean {
    if (start + prefix.length > length) return false
    for (i in prefix.indices) {
        if (this[start + i] != prefix[i]) return false
    }
    return true
}

internal data class CutLinePlan(
    val text: String,
    val deletion: TextRange,
    val resultingCursor: Int,
)

internal fun TextFieldState.buildCutLinePlan(): CutLinePlan? {
    val text = this.text.toString()
    val sel = this.selection
    if (!sel.collapsed) return null

    val cursor = sel.start
    val lineStart = text.lineStartAt(cursor)
    val lineEndInc = text.lineEndIncludingBreakAt(cursor)

    val cutText: String
    val deleteRange: TextRange
    val newCursor: Int

    if (lineStart == 0 && lineEndInc == text.length) {
        cutText = text
        deleteRange = TextRange(0, text.length)
        newCursor = 0
    } else if (lineEndInc == text.length && lineStart > 0) {
        val prevLineContentEnd = text.lineContentEndAt(lineStart - 1)
        cutText = text.substring(lineStart, lineEndInc)
        deleteRange = TextRange(prevLineContentEnd + 1, lineEndInc)
        newCursor = prevLineContentEnd + 1
    } else {
        cutText = text.substring(lineStart, lineEndInc)
        deleteRange = TextRange(lineStart, lineEndInc)
        newCursor = lineStart
    }

    return CutLinePlan(
        text = cutText,
        deletion = deleteRange,
        resultingCursor = newCursor.coerceAtMost(text.length - (lineEndInc - lineStart)),
    )
}

internal fun TextFieldState.duplicateSelectionOrLine(): Boolean {
    val text = this.text.toString()
    val sel = this.selection

    if (sel.collapsed) {
        val cursor = sel.start
        val lineStart = text.lineStartAt(cursor)
        val lineEndInc = text.lineEndIncludingBreakAt(cursor)
        val lineContent = text.substring(lineStart, lineEndInc)

        edit {
            when (lineEndInc) {
                text.length if lineEndInc > lineStart && text[lineEndInc - 1] == '\n' -> {
                    replace(lineEndInc - 1, lineEndInc, "\n$lineContent")
                    selection = TextRange(lineEndInc + lineContent.length, lineEndInc + lineContent.length)
                }
                text.length                                                           -> {
                    replace(lineEndInc, lineEndInc, "\n$lineContent")
                    selection = TextRange(
                        lineEndInc + 1 + (cursor - lineStart),
                        lineEndInc + 1 + (cursor - lineStart),
                    )
                }
                else                                                                  -> {
                    replace(lineEndInc, lineEndInc, lineContent)
                    selection = TextRange(
                        lineEndInc + (cursor - lineStart),
                        lineEndInc + (cursor - lineStart),
                    )
                }
            }
        }
        return true
    }

    val min = minOf(sel.start, sel.end)
    val max = maxOf(sel.start, sel.end)
    val selectedText = text.substring(min, max)

    edit {
        replace(min, max, "$selectedText$selectedText")
        selection = TextRange(min + selectedText.length, min + selectedText.length * 2)
    }
    return true
}

internal fun TextFieldState.indent(indent: String): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val min = minOf(sel.start, sel.end)
    val max = maxOf(sel.start, sel.end)

    if (min == max) {
        edit {
            replace(min, min, indent)
            selection = TextRange(min + indent.length, min + indent.length)
        }
        return true
    }

    val effectiveEnd = if (max > min) max - 1 else max
    val blockStart = text.lineStartAt(min)
    val blockEnd = text.lineEndIncludingBreakAt(effectiveEnd)

    val targetLines = mutableListOf<Int>()
    var pos = blockStart
    while (pos < blockEnd) {
        targetLines.add(pos)
        pos = text.lineEndIncludingBreakAt(pos)
    }

    val edits = targetLines.map { TextEdit(it, 0, indent) }
    val newStart = mapOffsetAfterEdits(sel.start, edits)
    val newEnd = mapOffsetAfterEdits(sel.end, edits)

    edit {
        for ((start, _, insertText) in edits.sortedByDescending { it.start }) {
            replace(start, start, insertText)
        }
        val reversed = sel.start > sel.end
        selection = if (reversed) TextRange(newEnd, newStart) else TextRange(newStart, newEnd)
    }
    return true
}

internal fun TextFieldState.unindent(indent: String): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val min = minOf(sel.start, sel.end)
    val max = maxOf(sel.start, sel.end)

    val effectiveEnd = if (max > min) max - 1 else max
    val blockStart = text.lineStartAt(min)
    val blockEnd = text.lineEndIncludingBreakAt(effectiveEnd)

    val targetStarts = mutableListOf<Int>()
    var pos = blockStart
    while (pos < blockEnd) {
        targetStarts.add(pos)
        pos = text.lineEndIncludingBreakAt(pos)
    }

    val edits = mutableListOf<TextEdit>()
    for (lineStart in targetStarts) {
        val contentEnd = text.lineContentEndAt(lineStart)
        val deleteLen = countLeadingIndent(text, lineStart, contentEnd, indent)
        if (deleteLen > 0) {
            edits.add(TextEdit(lineStart, deleteLen, ""))
        }
    }

    if (edits.isEmpty()) return false

    val newStart = mapOffsetAfterEdits(sel.start, edits)
    val newEnd = mapOffsetAfterEdits(sel.end, edits)

    edit {
        for ((start, deleteLength, insertText) in edits.sortedByDescending { it.start }) {
            replace(start, start + deleteLength, insertText)
        }
        val reversed = sel.start > sel.end
        selection = if (reversed) TextRange(newEnd, newStart) else TextRange(newStart, newEnd)
    }
    return true
}

private fun countLeadingIndent(
    text: CharSequence,
    lineStart: Int,
    contentEnd: Int,
    indent: String,
): Int {
    val available = contentEnd - lineStart
    if (available <= 0) return 0

    if (text.hasPrefixAt(indent, lineStart)) return indent.length

    var count = 0
    val maxDelete = minOf(indent.length, available)
    while (count < maxDelete && text[lineStart + count] == ' ') {
        count++
    }
    return count
}

internal fun TextFieldState.toggleLineComment(prefix: String): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val min = minOf(sel.start, sel.end)
    val max = maxOf(sel.start, sel.end)

    val effectiveEnd = if (max > min) max - 1 else max
    val blockStart = text.lineStartAt(min)
    val blockEnd = text.lineEndIncludingBreakAt(effectiveEnd)

    val targetLines = mutableListOf<Int>()
    var pos = blockStart
    while (pos < blockEnd) {
        targetLines.add(pos)
        pos = text.lineEndIncludingBreakAt(pos)
    }

    val nonEmptyLines = targetLines.filter { lineStart ->
        text.lineContentEndAt(lineStart) > lineStart
    }

    if (nonEmptyLines.isEmpty()) return false

    val allCommented = nonEmptyLines.all { lineStart ->
        val contentEnd = text.lineContentEndAt(lineStart)
        val firstNonWS = text.firstNonWhitespaceOffset(lineStart, contentEnd)
        firstNonWS + prefix.length <= contentEnd && text.hasPrefixAt(prefix, firstNonWS)
    }

    val edits = if (allCommented) {
        targetLines.mapNotNull { lineStart ->
            val contentEnd = text.lineContentEndAt(lineStart)
            val firstNonWS = text.firstNonWhitespaceOffset(lineStart, contentEnd)
            if (firstNonWS + prefix.length <= contentEnd && text.hasPrefixAt(prefix, firstNonWS)) {
                TextEdit(firstNonWS, prefix.length, "")
            } else null
        }
    } else {
        targetLines.mapNotNull { lineStart ->
            val contentEnd = text.lineContentEndAt(lineStart)
            if (contentEnd > lineStart) {
                val firstNonWS = text.firstNonWhitespaceOffset(lineStart, contentEnd)
                TextEdit(firstNonWS, 0, prefix)
            } else null
        }
    }

    if (edits.isEmpty()) return false

    val newStart = mapOffsetAfterEdits(sel.start, edits)
    val newEnd = mapOffsetAfterEdits(sel.end, edits)

    edit {
        for ((start, deleteLength, insertText) in edits.sortedByDescending { it.start }) {
            replace(start, start + deleteLength, insertText)
        }
        val reversed = sel.start > sel.end
        selection = if (reversed) TextRange(newEnd, newStart) else TextRange(newStart, newEnd)
    }
    return true
}

internal fun TextFieldState.moveSelectedLinesUp(): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val block = text.lineBlockFor(sel)
    val blockStart = minOf(block.start, block.end)
    val blockEnd = maxOf(block.start, block.end)

    if (blockStart <= 0) return false

    val prevLineStart = text.lineStartAt(blockStart - 1)

    val prevLineText = text.substring(prevLineStart, blockStart)
    val blockText = text.substring(blockStart, blockEnd)

    val newStart = prevLineStart + (sel.start - blockStart)
    val newEnd = prevLineStart + (sel.end - blockStart)

    edit {
        replace(prevLineStart, blockEnd, blockText + prevLineText)
        val reversed = sel.start > sel.end
        selection = if (reversed) TextRange(newEnd, newStart) else TextRange(newStart, newEnd)
    }
    return true
}

internal fun TextFieldState.moveSelectedLinesDown(): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val block = text.lineBlockFor(sel)
    val blockStart = minOf(block.start, block.end)
    val blockEnd = maxOf(block.start, block.end)

    if (blockEnd >= text.length) return false

    val nextLineEnd = text.lineEndIncludingBreakAt(blockEnd)

    val blockText = text.substring(blockStart, blockEnd)
    val nextLineText = text.substring(blockEnd, nextLineEnd)

    val nextLineLen = nextLineEnd - blockEnd
    val newStart = sel.start + nextLineLen
    val newEnd = sel.end + nextLineLen

    edit {
        replace(blockStart, nextLineEnd, nextLineText + blockText)
        val reversed = sel.start > sel.end
        selection = if (reversed) TextRange(newEnd, newStart) else TextRange(newStart, newEnd)
    }
    return true
}

internal fun TextFieldState.insertLineAbove(): Boolean {
    val text = this.text.toString()
    val cursor = this.selection.start
    val lineStart = text.lineStartAt(cursor)
    val leading = text.leadingWhitespaceAt(cursor)

    edit {
        replace(lineStart, lineStart, "$leading\n")
        selection = TextRange(lineStart, lineStart)
    }
    return true
}

internal fun TextFieldState.insertLineBelow(): Boolean {
    val text = this.text.toString()
    val cursor = this.selection.start
    val lineEndInc = text.lineEndIncludingBreakAt(cursor)
    val leading = text.leadingWhitespaceAt(cursor)

    val insertText: String
    val newCursorPos: Int

    if (lineEndInc == text.length) {
        insertText = "\n$leading"
        newCursorPos = lineEndInc + 1 + leading.length
    } else {
        insertText = "$leading\n"
        newCursorPos = lineEndInc + leading.length
    }

    edit {
        replace(lineEndInc, lineEndInc, insertText)
        selection = TextRange(newCursorPos, newCursorPos)
    }
    return true
}

internal fun TextFieldState.insertIndentedNewLine(): Boolean {
    val text = this.text.toString()
    val sel = this.selection
    val min = minOf(sel.start, sel.end)

    val activeEnd = sel.start
    val leading = text.leadingWhitespaceAt(activeEnd)

    if (!sel.collapsed) {
        val max = maxOf(sel.start, sel.end)
        edit {
            replace(min, max, "\n$leading")
            selection = TextRange(min + 1 + leading.length, min + 1 + leading.length)
        }
        return true
    }

    edit {
        replace(min, min, "\n$leading")
        selection = TextRange(min + 1 + leading.length, min + 1 + leading.length)
    }
    return true
}

internal fun TextFieldState.smartHome(extendSelection: Boolean): Boolean {
    val text = this.text.toString()
    val cursor = this.selection.start
    val lineStart = text.lineStartAt(cursor)
    val lineContentEnd = text.lineContentEndAt(lineStart)

    val firstNonWS = text.firstNonWhitespaceOffset(lineStart, lineContentEnd)

    val target = when {
        firstNonWS < lineContentEnd && cursor == firstNonWS -> lineStart
        firstNonWS < lineContentEnd -> firstNonWS
        else -> lineStart
    }

    edit {
        if (extendSelection) {
            val anchor = this.selection.end
            selection = TextRange(target, anchor)
        } else {
            selection = TextRange(target, target)
        }
    }
    return true
}
