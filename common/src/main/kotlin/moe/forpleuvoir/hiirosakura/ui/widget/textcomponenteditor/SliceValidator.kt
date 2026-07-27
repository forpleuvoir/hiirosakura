package moe.forpleuvoir.hiirosakura.ui.widget.textcomponenteditor

fun validateSlices(textLength: Int, slices: List<TextSlice>) {
    require(textLength >= 0) { "textLength must be >= 0, was $textLength" }
    if (textLength == 0) {
        require(slices.isEmpty()) { "Empty text must have no slices, got ${slices.size}" }
        return
    }
    require(slices.isNotEmpty()) { "Non-empty text must have at least one slice" }

    for ((i, slice) in slices.withIndex()) {
        val r = slice.range
        require(r.start >= 0 && r.start < r.end && r.end <= textLength) {
            "Slice $i has invalid range [${r.start}, ${r.end}) for textLength=$textLength"
        }
    }
    require(slices.first().range.start == 0) {
        "First slice must start at 0, starts at ${slices.first().range.start}"
    }
    require(slices.last().range.end == textLength) {
        "Last slice must end at $textLength, ends at ${slices.last().range.end}"
    }

    for (i in 0 until slices.size - 1) {
        val left = slices[i]
        val right = slices[i + 1]
        require(left.range.end == right.range.start) {
            "Gap or overlap between slice $i [${left.range.start}, ${left.range.end}) and slice ${i + 1} [${right.range.start}, ${right.range.end})"
        }
        require(left.style != right.style) {
            "Adjacent slices $i and ${i + 1} have identical styles but are not merged"
        }
    }
}
