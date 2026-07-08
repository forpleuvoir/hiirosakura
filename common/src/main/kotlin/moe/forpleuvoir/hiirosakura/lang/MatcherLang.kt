package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object MatcherLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.matcher.$key", args = args)

    inline fun entries(size: Int) = lang("entries", size)

}