package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.hiirosakura.ui.widget.radialmenu.RadialMenuDefaults
import moe.forpleuvoir.ibukigourd.ui.util.toNebulaColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.color

class RadialMenuSetting(
    val innerColor: Color = RadialMenuDefaults.IdleInnerColor.toNebulaColor,
    val outerColor: Color = RadialMenuDefaults.IdleOuterColor.toNebulaColor,
    val innerSelectedColor: Color = RadialMenuDefaults.SelectedInnerColor.toNebulaColor,
    val outerSelectedColor: Color = RadialMenuDefaults.SelectedOuterColor.toNebulaColor,
    iconScale: Float = 1f,
    innerRadius: Float = RadialMenuDefaults.InnerRadius.value,
    outerRadius: Float = RadialMenuDefaults.OuterRadius.value,
    optionRadius: Float = RadialMenuDefaults.OptionRadius.value,
    gap: Float = RadialMenuDefaults.GAP,
    pageSize: Int = RadialMenuDefaults.OPTIONS_PRE_PAGE,
    val borderColor: Color = RadialMenuDefaults.IdleBorderColor.toNebulaColor,
    val selectedBorderColor: Color = RadialMenuDefaults.SelectedBorderColor.toNebulaColor,
    cornerRadius: Float = RadialMenuDefaults.CornerRadius.value,
    borderWidth: Float = RadialMenuDefaults.BorderWidth.value,
) {

    companion object : Codec<RadialMenuSetting> by Codec.create<RadialMenuSetting>()
        .field<Color>("inner_color").getter(RadialMenuSetting::innerColor).default(RadialMenuDefaults.IdleInnerColor.toNebulaColor).codec(Codec.color)
        .field<Color>("outer_color").getter(RadialMenuSetting::outerColor).default(RadialMenuDefaults.IdleOuterColor.toNebulaColor).codec(Codec.color)
        .field<Color>("inner_selected_color").getter(RadialMenuSetting::innerSelectedColor).default(RadialMenuDefaults.SelectedInnerColor.toNebulaColor).codec(Codec.color)
        .field<Color>("outer_selected_color").getter(RadialMenuSetting::outerSelectedColor).default(RadialMenuDefaults.SelectedOuterColor.toNebulaColor).codec(Codec.color)
        .field<Float>("icon_scale").getter(RadialMenuSetting::iconScale).default(1f).codec(Codec.float)
        .field<Float>("inner_radius").getter(RadialMenuSetting::innerRadius).default(RadialMenuDefaults.InnerRadius.value).codec(Codec.float)
        .field<Float>("outer_radius").getter(RadialMenuSetting::outerRadius).default(RadialMenuDefaults.OuterRadius.value).codec(Codec.float)
        .field<Float>("option_radius").getter(RadialMenuSetting::optionRadius).default(RadialMenuDefaults.OptionRadius.value).codec(Codec.float)
        .field<Float>("gap").getter(RadialMenuSetting::gap).default(RadialMenuDefaults.GAP).codec(Codec.float)
        .field<Int>("page_size").getter(RadialMenuSetting::pageSize).default(RadialMenuDefaults.OPTIONS_PRE_PAGE).codec(Codec.int)
        .field<Color>("border_color").getter(RadialMenuSetting::borderColor).default(RadialMenuDefaults.IdleBorderColor.toNebulaColor).codec(Codec.color)
        .field<Color>("selected_border_color").getter(RadialMenuSetting::selectedBorderColor).default(RadialMenuDefaults.SelectedBorderColor.toNebulaColor).codec(Codec.color)
        .field<Float>("corner_radius").getter(RadialMenuSetting::cornerRadius).default(RadialMenuDefaults.CornerRadius.value).codec(Codec.float)
        .field<Float>("border_width").getter(RadialMenuSetting::borderWidth).default(RadialMenuDefaults.BorderWidth.value).codec(Codec.float)
        .build(::RadialMenuSetting)


    val iconScale: Float = iconScale.coerceIn(0.2f, 2f)
    val innerRadius: Float = innerRadius.coerceIn(80f, 250f)
    val outerRadius: Float = outerRadius.coerceIn(200f, 500f)
    val optionRadius: Float = optionRadius.coerceIn(150f, 400f)
    val gap: Float = gap.coerceIn(0f, 30f)
    val pageSize: Int = pageSize.coerceIn(4, 12)
    val cornerRadius: Float = cornerRadius.coerceIn(0f, 20f)
    val borderWidth: Float = borderWidth.coerceIn(0f, 10f)

    fun copy(
        innerColor: Color = this.innerColor,
        outerColor: Color = this.outerColor,
        innerSelectedColor: Color = this.innerSelectedColor,
        outerSelectedColor: Color = this.outerSelectedColor,
        iconScale: Float = this.iconScale,
        innerRadius: Float = this.innerRadius,
        outerRadius: Float = this.outerRadius,
        optionRadius: Float = this.optionRadius,
        gap: Float = this.gap,
        pageSize: Int = this.pageSize,
        borderColor: Color = this.borderColor,
        selectedBorderColor: Color = this.selectedBorderColor,
        cornerRadius: Float = this.cornerRadius,
        borderWidth: Float = this.borderWidth,
    ): RadialMenuSetting = RadialMenuSetting(
        innerColor,
        outerColor,
        innerSelectedColor,
        outerSelectedColor,
        iconScale,
        innerRadius,
        outerRadius,
        optionRadius,
        gap,
        pageSize,
        borderColor,
        selectedBorderColor,
        cornerRadius,
        borderWidth,
    )

}