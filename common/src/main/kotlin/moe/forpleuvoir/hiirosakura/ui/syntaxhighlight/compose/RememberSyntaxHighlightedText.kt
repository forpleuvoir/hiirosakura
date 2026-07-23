package moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightSpan
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlightTheme
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxHighlighter
import moe.forpleuvoir.hiirosakura.ui.syntaxhighlight.SyntaxLanguage

/**
 * 对文本进行语法高亮并返回带样式的 [AnnotatedString]，适用于只读 [Text]。
 *
 * 使用两级缓存：
 * - 文本或语言变化时重新扫描语法。
 * - 仅主题变化时只重新映射样式，不重复语言扫描。
 *
 * @param text 源文本。
 * @param language 语法语言，如 [JsonSyntaxLanguage]、[TomlSyntaxLanguage]。
 * @param theme 高亮主题，可通过 [SyntaxHighlightDefaults.theme] 获取。
 * @return 带语法高亮样式的 [AnnotatedString]。
 */
@Composable
fun rememberSyntaxHighlightedText(
    text: CharSequence,
    language: SyntaxLanguage,
    theme: SyntaxHighlightTheme,
): AnnotatedString {
    val textSnapshot = text.toString()

    val spans: List<SyntaxHighlightSpan> = remember(textSnapshot, language.id) {
        SyntaxHighlighter.highlight(textSnapshot, language)
    }

    return remember(textSnapshot, spans, theme) {
        SyntaxHighlighter.buildSyntaxHighlightedText(textSnapshot, spans, theme)
    }
}
