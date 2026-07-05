package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.style

@Suppress("NOTHING_TO_INLINE")
object ChatLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.chat.$key", args = args)

    inline val injectRegex
        get() = lang("inject.regex").style {
            hover(lang("inject.regex.comment"))
        }

    inline val injectExp
        get() = lang("inject.exp").style {
            hover(lang("inject.exp.comment"))
        }

    inline val filterExp
        get() = lang("filter.regex").style {
            hover(lang("filter.regex.comment"))
        }

    inline val bubbleServerName
        get() = lang("bubble.server_name").style {
            hover(lang("bubble.server_name.comment"))
        }

    inline val bubbleServerConfig
        get() = lang("bubble.server_config").style {
            hover(lang("bubble.server_config.comment"))
        }

    inline val bubbleServerConfigRegex
        get() = lang("bubble.server_config.regex").style {
            hover(lang("bubble.server_config.regex.comment"))
        }

    inline val bubbleServerConfigEnableUUID
        get() = lang("bubble.server_config.enable_uuid").style {
            hover(lang("bubble.server_config.enable_uuid.comment"))
        }

    inline val bubbleServerConfigEnableProfile
        get() = lang("bubble.server_config.enable_profile").style {
            hover(lang("bubble.server_config.enable_profile.comment"))
        }
}
