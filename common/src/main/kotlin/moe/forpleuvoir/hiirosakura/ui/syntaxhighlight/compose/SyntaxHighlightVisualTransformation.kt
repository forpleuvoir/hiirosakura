package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightSpan
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlighter
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightTheme
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxLanguage
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxToken

private const val VISIBLE_SPACE = '\u2423'

/**
 * [OutputTransformation] 实现，用于在可编辑的 [TextField]/[OutlinedTextField] 中应用语法高亮。
 *
 * 内部缓存：文本不变时不重复扫描语法。
 *
 * @param language 语法语言。
 * @param theme 高亮主题。
 */
class SyntaxHighlightOutputTransformation(
    private val language: SyntaxLanguage,
    private val theme: SyntaxHighlightTheme,
) : OutputTransformation {

    private var cachedText: String? = null
    private var cachedResult: List<SyntaxHighlightSpan>? = null

    override fun TextFieldBuffer.transformOutput() {
        val raw = toString()
        val spans = if (cachedText == raw) {
            cachedResult ?: return
        } else {
            val result = SyntaxHighlighter.highlight(raw, language)
            cachedText = raw
            cachedResult = result
            result
        }
        for (span in spans) {
            if (span.token == SyntaxToken.Error) {
                val s = span.range.start.coerceIn(0, raw.length)
                val ch = raw[s]
                if (ch.isWhitespace() && ch != '\n') {
                    replace(s, (s + 1).coerceAtMost(length), VISIBLE_SPACE.toString())
                }
            }
            addStyle(
                spanStyle = theme.styleOf(span.token),
                start = span.range.start,
                end = span.range.end,
            )
        }
    }
}

/**
 * 创建并缓存 [SyntaxHighlightOutputTransformation] 实例，适用于可编辑 TextField。
 *
 * 通过 [remember] 缓存 transformation 实例，切换语言或主题时自动重建。
 *
 * @param language 语法语言。
 * @param theme 高亮主题。
 * @return [OutputTransformation]，传给 [OutlinedTextField.outputTransformation] 或
 *   [BasicTextField] 的对应参数。
 */
@Composable
fun rememberSyntaxHighlightTransformation(
    language: SyntaxLanguage,
    theme: SyntaxHighlightTheme,
): OutputTransformation = remember(language.id, theme) {
    SyntaxHighlightOutputTransformation(language, theme)
}
