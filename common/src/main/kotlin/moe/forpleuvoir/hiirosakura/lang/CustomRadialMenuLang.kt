package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.style

@Suppress("NOTHING_TO_INLINE")
object CustomRadialMenuLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.custom_radial_menu.$key", args = args)

    inline val title get() = Translatable("${HiiroSakura.MOD_ID}.custom_radial_menu")

    inline val add get() = lang("add")

    inline val setting get() = lang("setting")

    inline val editName get() = lang("edit_name")

    inline fun exists(name: String) = lang("exists", name)

    inline val settingInnerColor
        get() = lang("setting.inner_color").style {
            hover(lang("setting.inner_color.comment"))
        }

    inline val settingOuterColor
        get() = lang("setting.outer_color").style {
            hover(lang("setting.outer_color.comment"))
        }

    inline val settingInnerSelectedColor
        get() = lang("setting.inner_selected_color").style {
            hover(lang("setting.inner_selected_color.comment"))
        }

    inline val settingOuterSelectedColor
        get() = lang("setting.outer_selected_color").style {
            hover(lang("setting.outer_selected_color.comment"))
        }

    inline val settingIconScale
        get() = lang("setting.icon_scale").style {
            hover(lang("setting.icon_scale.comment"))
        }

    inline val settingInnerRadius
        get() = lang("setting.inner_radius").style {
            hover(lang("setting.inner_radius.comment"))
        }

    inline val settingOuterRadius
        get() = lang("setting.outer_radius").style {
            hover(lang("setting.outer_radius.comment"))
        }

    inline val settingOptionRadius
        get() = lang("setting.option_radius").style {
            hover(lang("setting.option_radius.comment"))
        }

    inline val settingGap
        get() = lang("setting.gap").style {
            hover(lang("setting.gap.comment"))
        }

    inline val settingPageSize
        get() = lang("setting.page_size").style {
            hover(lang("setting.page_size.comment"))
        }
}
