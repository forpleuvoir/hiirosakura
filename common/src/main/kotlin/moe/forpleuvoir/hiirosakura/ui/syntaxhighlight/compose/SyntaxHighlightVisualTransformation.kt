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

    override fun TextFieldBuffer.transformOutput() {
        val raw = toString()
        val spans = SyntaxHighlighter.highlight(raw, language)
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
 * 传入 [text] 会在文本变化时重新创建实例，确保输入法组合输入时高亮也能正确更新。
 *
 * @param language 语法语言。
 * @param theme 高亮主题。
 * @param text 当前文本，用于在文本变化时重新创建实例（如输入法组合输入场景）。
 * @return [OutputTransformation]，传给 [OutlinedTextField.outputTransformation] 或
 *   [BasicTextField] 的对应参数。
 */
@Composable
fun rememberSyntaxHighlightTransformation(
    language: SyntaxLanguage,
    theme: SyntaxHighlightTheme,
    text: String = "",
): OutputTransformation = remember(language.id, theme, text) {
    SyntaxHighlightOutputTransformation(language, theme)
}
