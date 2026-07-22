package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.hiirosakura.functional.task.QuickTickTaskExecuteScreen as Defaults
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.color

class RadialMenuSetting(
    val innerColor: Color = Defaults.innerColor,
    val outerColor: Color = Defaults.outerColor,
    val innerSelectedColor: Color = Defaults.innerSelectedColor,
    val outerSelectedColor: Color = Defaults.outerSelectedColor,
    iconScale: Float = Defaults.iconScale,
    innerRadius: Float = Defaults.innerRadius,
    outerRadius: Float = Defaults.outerRadius,
    optionRadius: Float = Defaults.optionRadius,
    gap: Float = Defaults.gapDistance,
    pageSize: Int = Defaults.singlePageMaxCount,
    val borderColor: Color = Defaults.idleBorderColor,
    val selectedBorderColor: Color = Defaults.selectedBorderColor,
    cornerRadius: Float = Defaults.cornerRadius,
    borderWidth: Float = Defaults.borderWidth,
) {

    companion object : Codec<RadialMenuSetting> by Codec.create<RadialMenuSetting>()
        .field<Color>("inner_color").getter({ it.innerColor }).default(Defaults.innerColor).skipDefault().codec(Codec.color)
        .field<Color>("outer_color").getter({ it.outerColor }).default(Defaults.outerColor).skipDefault().codec(Codec.color)
        .field<Color>("inner_selected_color").getter({ it.innerSelectedColor }).default(Defaults.innerSelectedColor).skipDefault().codec(Codec.color)
        .field<Color>("outer_selected_color").getter({ it.outerSelectedColor }).default(Defaults.outerSelectedColor).skipDefault().codec(Codec.color)
        .field<Float>("icon_scale").getter(RadialMenuSetting::iconScale).default(Defaults.iconScale).skipDefault().codec(Codec.float)
        .field<Float>("inner_radius").getter({ it.innerRadius }).default(Defaults.innerRadius).skipDefault().codec(Codec.float)
        .field<Float>("outer_radius").getter({ it.outerRadius }).default(Defaults.outerRadius).skipDefault().codec(Codec.float)
        .field<Float>("option_radius").getter({ it.optionRadius }).default(Defaults.optionRadius).skipDefault().codec(Codec.float)
        .field<Float>("gap").getter({ it.gap }).default(Defaults.gapDistance).skipDefault().codec(Codec.float)
        .field<Int>("page_size").getter({ it.pageSize }).default(Defaults.singlePageMaxCount).skipDefault().codec(Codec.int)
        .field<Color>("border_color").getter({ it.borderColor }).default(Defaults.idleBorderColor).skipDefault().codec(Codec.color)
        .field<Color>("selected_border_color").getter({ it.selectedBorderColor }).default(Defaults.selectedBorderColor).skipDefault().codec(Codec.color)
        .field<Float>("corner_radius").getter({ it.cornerRadius }).default(Defaults.cornerRadius).skipDefault().codec(Codec.float)
        .field<Float>("border_width").getter({ it.borderWidth }).default(Defaults.borderWidth).skipDefault().codec(Codec.float)
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