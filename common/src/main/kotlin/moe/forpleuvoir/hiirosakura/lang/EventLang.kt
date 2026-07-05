package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object EventLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.event.$key", args = args)

    inline fun enableEvent(name: String, enabled: Boolean) = lang("enable.success", name).append(IGLang.Misc.coloredSwitch(enabled))

    inline fun enableEventNotFound(name: String) = lang("enable.not_found", name)

    inline val subscriberManager get() = lang("subscriber.manager")

    inline val subscriberEditor get() = lang("subscriber.editor")

    inline val subscriberName get() = lang("subscriber.name")
}
