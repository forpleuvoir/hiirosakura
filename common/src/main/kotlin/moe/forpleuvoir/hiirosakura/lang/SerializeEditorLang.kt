package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object SerializeEditorLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.serialize_editor.$key", args = args)

    val objectType get() = lang("object")

    val arrayType get() = lang("array")

    val emptyObject get() = lang("empty_object")

    val emptyArray get() = lang("empty_array")

    val addElement get() = lang("add_element")

    val renameKey get() = lang("rename_key")

    val key get() = lang("key")

    val type get() = lang("type")

    val keyCannotBeEmpty get() = lang("key_cannot_be_empty")

    val keyAlreadyExists get() = lang("key_already_exists")
}
