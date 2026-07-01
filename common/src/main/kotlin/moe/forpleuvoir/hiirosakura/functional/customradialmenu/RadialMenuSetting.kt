package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.color

class RadialMenuSetting(
    val innerColor: Color = Color.fromARGB(0x33000000),
    val outerColor: Color = Color.fromARGB(0x7F000000),
    val innerSelectedColor: Color = Color.fromARGB(0x33FF8899),
    val outerSelectedColor: Color = Color.fromARGB(0xFFFF8899),
    iconScale: Float = 1f,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 90f,
    gap: Float = 2f,
    pageSize: Int = 8
) {

    companion object : Codec<RadialMenuSetting> by Codec.create<RadialMenuSetting>()
        .field<Color>("inner_color").getter(RadialMenuSetting::innerColor).default(Color.fromARGB(0x33000000)).codec(Codec.color)
        .field<Color>("outer_color").getter(RadialMenuSetting::outerColor).default(Color.fromARGB(0x7F000000)).codec(Codec.color)
        .field<Color>("inner_selected_color").getter(RadialMenuSetting::innerSelectedColor).default(Color.fromARGB(0x33FF8899)).codec(Codec.color)
        .field<Color>("outer_selected_color").getter(RadialMenuSetting::outerSelectedColor).default(Color.fromARGB(0xFFFF8899)).codec(Codec.color)
        .field<Float>("icon_scale").getter(RadialMenuSetting::iconScale).default(1f).codec(Codec.float)
        .field<Float>("inner_radius").getter(RadialMenuSetting::innerRadius).default(60f).codec(Codec.float)
        .field<Float>("outer_radius").getter(RadialMenuSetting::outerRadius).default(120f).codec(Codec.float)
        .field<Float>("option_radius").getter(RadialMenuSetting::optionRadius).default(90f).codec(Codec.float)
        .field<Float>("gap").getter(RadialMenuSetting::gap).default(2f).codec(Codec.float)
        .field<Int>("page_size").getter(RadialMenuSetting::pageSize).default(8).codec(Codec.int)
        .build(::RadialMenuSetting)


    val iconScale: Float = iconScale.coerceIn(0.2f, 2f)
    val innerRadius: Float = innerRadius.coerceIn(40f, 100f)
    val outerRadius: Float = outerRadius.coerceIn(100f, 200f)
    val optionRadius: Float = optionRadius.coerceIn(50f, 190f)
    val gap: Float = gap.coerceIn(0f, 5f)
    val pageSize: Int = pageSize.coerceIn(4, 12)

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
        pageSize: Int = this.pageSize
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
        pageSize
    )

}