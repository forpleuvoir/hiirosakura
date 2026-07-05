package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object AutoReplantLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.auto_replant.$key", args = args)

    inline val mapEntryTargetBlock get() = lang("map_entry.target_block")

    inline val mapEntryTargetBlockComment get() = lang("map_entry.target_block.comment")

    inline val mapEntryReplantItem get() = lang("map_entry.replant_item")

    inline val mapEntryReplantItemComment get() = lang("map_entry.replant_item.comment")

    inline val mapEntryGroundBlock get() = lang("map_entry.ground_block")

    inline val mapEntryGroundBlockComment get() = lang("map_entry.ground_block.comment")
}
