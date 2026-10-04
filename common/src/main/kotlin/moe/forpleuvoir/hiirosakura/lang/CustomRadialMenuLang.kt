package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

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

    inline val nameEmpty get() = lang("name_empty")

    inline val nameInvalid get() = lang("name_invalid")

    inline val settingInnerColor get() = lang("setting.inner_color")

    inline val settingOuterColor get() = lang("setting.outer_color")

    inline val settingInnerSelectedColor get() = lang("setting.inner_selected_color")

    inline val settingOuterSelectedColor get() = lang("setting.outer_selected_color")

    inline val settingIconScale get() = lang("setting.icon_scale")

    inline val settingInnerRadius get() = lang("setting.inner_radius")

    inline val settingOuterRadius get() = lang("setting.outer_radius")

    inline val settingOptionRadius get() = lang("setting.option_radius")

    inline val settingGap get() = lang("setting.gap")

    inline val settingPageSize get() = lang("setting.page_size")

    inline val settingBorderColor get() = lang("setting.border_color")

    inline val settingSelectedBorderColor get() = lang("setting.selected_border_color")

    inline val settingCornerRadius get() = lang("setting.corner_radius")

    inline val settingBorderWidth get() = lang("setting.border_width")

    inline val settingTiltDegree get() = lang("setting.tilt_degree")

    inline val settingInnerColorComment get() = lang("setting.inner_color.comment")

    inline val settingOuterColorComment get() = lang("setting.outer_color.comment")

    inline val settingInnerSelectedColorComment get() = lang("setting.inner_selected_color.comment")

    inline val settingOuterSelectedColorComment get() = lang("setting.outer_selected_color.comment")

    inline val settingIconScaleComment get() = lang("setting.icon_scale.comment")

    inline val settingInnerRadiusComment get() = lang("setting.inner_radius.comment")

    inline val settingOuterRadiusComment get() = lang("setting.outer_radius.comment")

    inline val settingOptionRadiusComment get() = lang("setting.option_radius.comment")

    inline val settingGapComment get() = lang("setting.gap.comment")

    inline val settingPageSizeComment get() = lang("setting.page_size.comment")

    inline val settingBorderColorComment get() = lang("setting.border_color.comment")

    inline val settingSelectedBorderColorComment get() = lang("setting.selected_border_color.comment")

    inline val settingCornerRadiusComment get() = lang("setting.corner_radius.comment")

    inline val settingBorderWidthComment get() = lang("setting.border_width.comment")

    inline val settingTiltDegreeComment get() = lang("setting.tilt_degree.comment")

    inline val delete get() = lang("delete")
    inline val deleteConfirm get() = lang("delete_confirm")
    inline val importTasks get() = lang("import_tasks")
}
