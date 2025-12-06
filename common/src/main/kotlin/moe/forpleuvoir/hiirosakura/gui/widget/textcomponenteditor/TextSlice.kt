package moe.forpleuvoir.hiirosakura.gui.widget.textcomponenteditor

import moe.forpleuvoir.hiirosakura.gui.widget.textcomponenteditor.TextSlice.SliceStyle.Companion.EMPTY
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.RGBColor
import net.minecraft.network.chat.*

data class TextSlice(
    val range: IntRange,
    var style: SliceStyle
) {
    init {
        check(range.first > 0 && range.last > range.first) {
            "Invalid range: $range"
        }
    }

    data class SliceStyle(
        val color: RGBColor? = null,
        val shadowColor: ARGBColor? = null,
        val bold: Boolean? = null,
        val italic: Boolean? = null,
        val underlined: Boolean? = null,
        val strikethrough: Boolean? = null,
        val obfuscated: Boolean? = null,
        val clickEvent: ClickEvent? = null,
        val hoverEvent: HoverEvent? = null,
        val insertion: String? = null,
        val font: FontDescription? = null
    ) {

        companion object {
            val EMPTY = SliceStyle()
        }

        val mcStyle: Style
            get() = Style(
                color?.rgb?.let { TextColor.fromRgb(it) },
                shadowColor?.argb,
                bold,
                italic,
                underlined,
                strikethrough,
                obfuscated,
                clickEvent,
                hoverEvent,
                insertion,
                font
            )

        fun merge(other: SliceStyle) = SliceStyle(
            other.color ?: color,
            other.shadowColor ?: shadowColor,
            other.bold ?: bold,
            other.italic ?: italic,
            other.underlined ?: underlined,
            other.strikethrough ?: strikethrough,
            other.obfuscated ?: obfuscated,
            other.clickEvent ?: clickEvent,
            other.hoverEvent ?: hoverEvent,
            other.insertion ?: insertion,
            other.font ?: font
        )
    }

    val mcStyle: Style
        get() = style.mcStyle


    companion object {

        fun List<TextSlice>.compose(text: String): Text {
            var firstText: Text? = null
            this.forEach { slice ->
                Literal(text.substring(slice.range)).setStyle(slice.mcStyle).let {
                    if (firstText == null) {
                        firstText = it
                    } else {
                        firstText.append(it)
                    }
                }
            }
            return firstText ?: Literal(text)
        }

        fun MutableList<TextSlice>.insert(index: Int, length: Int) {
            this.map { slice ->
                TextSlice(
                    range = (slice.range.first + if (slice.range.first > index) length else 0).coerceAtLeast(0)
                            ..(slice.range.last + if (slice.range.last > index) length else 0).coerceAtLeast(0),
                    style = slice.style
                )
            }.let {
                this.clear()
                this.addAll(it)
            }
        }

        fun MutableList<TextSlice>.attach(textSlice: TextSlice) {
            if (textSlice.range.first < 0 || textSlice.range.last < 0 || textSlice.range.last - textSlice.range.first < 1) return

            this.find { it.range == textSlice.range }?.let {
                it.style = it.style.merge(textSlice.style)
                return
            }

            // 提取所有关键点（起点和终点）
            val points = mutableSetOf<Int>()
            for (range in this) {
                points.add(range.range.first)
                points.add(range.range.last + 1) // 使用 last + 1 表示开区间
            }
            points.add(textSlice.range.first)
            points.add(textSlice.range.last + 1)

            // 去重并排序
            val sortedPoints = points.sorted()

            // 构建结果列表
            val result = mutableListOf<TextSlice>()

            for (i in 0 until sortedPoints.size - 1) {
                val start = sortedPoints[i]
                val end = sortedPoints[i + 1] - 1 // 转换回闭区间

                if (start > end) continue // 跳过无效范围

                // 判断当前范围属于哪个数据
                val currentRange = start..end
                val overlappingExisting = this.find { it.range.contains(currentRange.first) }
                val isOverlappingNew = textSlice.range.contains(currentRange.first)

                val style: SliceStyle = when {
                    isOverlappingNew            -> {
                        textSlice.style.merge(overlappingExisting?.style ?: EMPTY)
                    }

                    overlappingExisting != null -> overlappingExisting.style
                    else                        -> EMPTY
                }

                result.add(TextSlice(currentRange, style))
            }

            this.clear()
            this.addAll(result)
        }
    }
}