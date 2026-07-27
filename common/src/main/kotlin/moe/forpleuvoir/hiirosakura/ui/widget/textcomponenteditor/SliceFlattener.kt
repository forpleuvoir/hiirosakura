package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

import androidx.compose.ui.text.TextRange

fun flatten(
    textLength: Int,
    base: List<TextSlice>,
    overlays: List<StyleOverlay>,
): List<TextSlice> {
    if (textLength == 0) return emptyList()

    val boundaries = sortedSetOf(0, textLength)
    for (slice in base) {
        val start = slice.range.start.coerceIn(0, textLength)
        val end = slice.range.end.coerceIn(0, textLength)
        if (start < end) {
            boundaries.add(start)
            boundaries.add(end)
        }
    }
    for (overlay in overlays) {
        val start = overlay.range.start.coerceIn(0, textLength)
        val end = overlay.range.end.coerceIn(0, textLength)
        if (start < end) {
            boundaries.add(start)
            boundaries.add(end)
        }
    }

    val boundariesList = boundaries.toList()
    val segments = mutableListOf<TextSlice>()

    for (i in 0 until boundariesList.size - 1) {
        val segStart = boundariesList[i]
        val segEnd = boundariesList[i + 1]
        if (segStart >= segEnd) continue

        var style = SliceStyle.EMPTY
        for (slice in base) {
            if (slice.range.start <= segStart && segEnd <= slice.range.end) {
                style = slice.style
                break
            }
        }
        for (overlay in overlays) {
            if (overlay.range.start <= segStart && segEnd <= overlay.range.end) {
                style = style.apply(overlay.patch)
            }
        }
        segments.add(TextSlice(TextRange(segStart, segEnd), style))
    }

    return segments.mergeAdjacent()
}

internal fun List<TextSlice>.mergeAdjacent(): List<TextSlice> {
    if (size <= 1) return this
    val merged = mutableListOf<TextSlice>()
    var current = this[0]
    for (i in 1 until size) {
        val next = this[i]
        if (current.style == next.style && current.range.end == next.range.start) {
            current = TextSlice(TextRange(current.range.start, next.range.end), current.style)
        } else {
            merged.add(current)
            current = next
        }
    }
    merged.add(current)
    return merged
}
