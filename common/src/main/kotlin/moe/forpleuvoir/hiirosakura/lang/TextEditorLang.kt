package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object TextEditorLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.text_editor.$key", args = args)

    val preview get() = lang("preview")
    val bold get() = lang("bold")
    val italic get() = lang("italic")
    val underlined get() = lang("underlined")
    val strikethrough get() = lang("strikethrough")
    val obfuscated get() = lang("obfuscated")
    val textColor get() = lang("text_color")
    val shadowColor get() = lang("shadow_color")
    val clearStyle get() = lang("clear_style")
    val helpTip get() = lang("help_tip")
}