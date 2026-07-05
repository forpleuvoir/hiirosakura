package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object ItemStackMatcherLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.item_stack_matcher.$key", args = args)

    inline val title get() = Translatable("${HiiroSakura.MOD_ID}.item_stack_matcher")

    inline val handheldItem get() = lang("handheld_item")

    inline val testButton get() = lang("test_button")

    inline val testSuccess get() = lang("test_success")

    inline val testFailed get() = lang("test_failed")

    val Entry = EntryLang

    @Suppress("NOTHING_TO_INLINE")
    object EntryLang {

        @PublishedApi
        internal inline fun lang(key: String, vararg args: Any): MutableText =
            Translatable("${HiiroSakura.MOD_ID}.item_stack_matcher_entry.$key", args = args)

        inline val matcher get() = lang("matcher")

        inline val item get() = lang("item")

        inline val name get() = lang("name")

        inline val script get() = lang("script")

        inline val count get() = lang("count")

        inline val rarity get() = lang("rarity")

        inline val enchantment get() = lang("enchantment")

        inline val enchantmentID get() = lang("enchantment.id")

        inline val enchantmentLevelRange get() = lang("enchantment.level_range")

        inline val tag get() = lang("tag")

        inline val dataComponentType get() = lang("data_component_type")
    }
}
