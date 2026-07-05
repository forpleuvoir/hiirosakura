package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object BlockInfoMatcherLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.block_info_matcher.$key", args = args)

    inline val title get() = Translatable("${HiiroSakura.MOD_ID}.block_info_matcher")

    inline val targetBlock get() = lang("target_block")

    inline val testButton get() = lang("test_button")

    inline val testSuccess get() = lang("test_success")

    inline val testFailed get() = lang("test_failed")

    val Entry = EntryLang

    @Suppress("NOTHING_TO_INLINE")
    object EntryLang {

        @PublishedApi
        internal inline fun lang(key: String, vararg args: Any): MutableText =
            Translatable("${HiiroSakura.MOD_ID}.block_info_matcher_entry.$key", args = args)

        inline val matcher get() = lang("matcher")

        inline val block get() = lang("block")

        inline val script get() = lang("script")

        inline val pos get() = lang("pos")

        inline val tag get() = lang("tag")

        inline val property get() = lang("property")
    }
}
