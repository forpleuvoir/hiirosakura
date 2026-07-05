package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object ScriptLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.script.$key", args = args)

    inline fun setConfigSuccess(key: String, value: String) = lang("set_config.success", key, value)

    inline fun setConfigFail(key: String, value: String, message: String?) = lang("set_config.fail", key, value, message ?: "unknown")

    inline fun setConfigFailNotFound(key: String) = lang("set_config.not_found", key)
}
