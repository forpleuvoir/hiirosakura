package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object CommonLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.$key", args = args)

    inline fun cantBeEmpty(text: Text): MutableText = lang("cant_be_empty", text)

    inline val loadFromDisk get() = lang("load_from_disk")

    inline fun pressToCopy(text: Text): MutableText = lang("press_to_copy", text)

    inline val name get() = lang("name")

    inline val enable get() = lang("enable")

    inline val copySuccess get() = lang("copy_success")

    inline val success get() = lang("success")

    inline val message get() = lang("message")

    inline val send get() = lang("send")

    inline fun deleteConfirm(someThing: Any): MutableText = lang("delete_confirm", someThing)

    inline val tag get() = lang("tag")

    inline val itemComponent get() = lang("item_component")

    inline val getFromHandItem get() = lang("get_from_handheld_item")

    inline val getFromRegistry get() = lang("get_from_registry")

    inline val getFromTargetBlock get() = lang("get_from_target_block")

    inline val blockProperty get() = lang("block_property")

    inline val soundEffect get() = lang("sound_effect")

    inline val chatBubblePreviewLock get() = lang("chat_bubble.preview.lock")

    inline val itemInitFailure get() = lang("item_init_failure")

    inline val exportAsFormat get() = MatcherLang.lang("export_as_format")

    inline val importFromFormat get() = MatcherLang.lang("import_from_format")

}
