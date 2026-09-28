package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object ChatLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.chat.$key", args = args)

    inline val injectRegex get() = lang("inject.regex")

    inline val injectExp get() = lang("inject.exp")

    inline val filterExp get() = lang("filter.regex")

    inline val bubbleServerName get() = lang("bubble.server_name")

    inline val bubbleServerConfig get() = lang("bubble.server_config")

    inline val bubbleServerConfigRegex get() = lang("bubble.server_config.regex")

    inline val bubbleServerConfigEnableUUID get() = lang("bubble.server_config.enable_uuid")

    inline val bubbleServerConfigEnableProfile get() = lang("bubble.server_config.enable_profile")

    inline val injectRegexComment get() = lang("inject.regex.comment")

    inline val injectExpComment get() = lang("inject.exp.comment")

    inline val filterExpComment get() = lang("filter.regex.comment")

    inline val bubbleServerNameComment get() = lang("bubble.server_name.comment")

    inline val bubbleServerConfigComment get() = lang("bubble.server_config.comment")

    inline val bubbleServerConfigRegexComment get() = lang("bubble.server_config.regex.comment")

    inline val bubbleServerConfigEnableUUIDComment get() = lang("bubble.server_config.enable_uuid.comment")

    inline val bubbleServerConfigEnableProfileComment get() = lang("bubble.server_config.enable_profile.comment")
}
