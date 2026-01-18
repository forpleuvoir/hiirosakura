package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class RadialMenuSetting(
    val color: ARGBColor = Color.ofARGB(0x80000000),
    val selectedColor: ARGBColor = Color.ofRGB(0xD4FF00),
    iconScale: Float = 1f,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 85f,
    gap: Float = 2f,
    pageSize: Int = 8
) : Serializable {

    companion object: Deserializer<RadialMenuSetting> {
        override fun deserialization(serializeElement: SerializeElement): RadialMenuSetting=
            serializeElement.checkType<SerializeObject, RadialMenuSetting> {
                RadialMenuSetting(
                    color = it["color"]?.let { Color.deserialization(it) } ?: Color.ofARGB(0x80000000),
                    selectedColor = it["selected_color"]?.let { Color.deserialization(it) } ?: Color.ofRGB(0xD4FF00),
                    iconScale = it["icon_scale"]?.asFloat ?: 1f,
                    innerRadius = it["inner_radius"]?.asFloat ?: 60f,
                    outerRadius = it["outer_radius"]?.asFloat ?: 120f,
                    optionRadius = it["option_radius"]?.asFloat ?: 85f,
                    gap = it["gap"]?.asFloat ?: 2f,
                    pageSize = it["page_size"]?.asInt ?: 8
                )
            }.getOrThrow()
    }

    val iconScale: Float = iconScale.coerceIn(0.2f, 2f)
    val innerRadius: Float = innerRadius.coerceIn(40f, 100f)
    val outerRadius: Float = outerRadius.coerceIn(100f, 200f)
    val optionRadius: Float = optionRadius.coerceIn(50f, 190f)
    val gap: Float = gap.coerceIn(0.5f, 5f)
    val pageSize: Int = pageSize.coerceIn(4, 12)

    fun copy(
        color: ARGBColor = this.color,
        selectedColor: ARGBColor = this.selectedColor,
        iconScale: Float = this.iconScale,
        innerRadius: Float = this.innerRadius,
        outerRadius: Float = this.outerRadius,
        optionRadius: Float = this.optionRadius,
        gap: Float = this.gap,
        pageSize: Int = this.pageSize
    ): RadialMenuSetting = RadialMenuSetting(
        color,
        selectedColor,
        iconScale,
        innerRadius,
        outerRadius,
        optionRadius,
        gap,
        pageSize
    )

    override fun serialization(): SerializeElement = serializeObject {
        "color" to  color.serialization()
        "selected_color" to selectedColor.serialization()
        "icon_scale" to iconScale
        "inner_radius" to innerRadius
        "outer_radius" to outerRadius
        "option_radius" to optionRadius
        "gap" to gap
        "page_size" to pageSize
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RadialMenuSetting

        if (iconScale != other.iconScale) return false
        if (innerRadius != other.innerRadius) return false
        if (outerRadius != other.outerRadius) return false
        if (optionRadius != other.optionRadius) return false
        if (gap != other.gap) return false
        if (pageSize != other.pageSize) return false
        if (color != other.color) return false
        if (selectedColor != other.selectedColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconScale.hashCode()
        result = 31 * result + innerRadius.hashCode()
        result = 31 * result + outerRadius.hashCode()
        result = 31 * result + optionRadius.hashCode()
        result = 31 * result + gap.hashCode()
        result = 31 * result + pageSize
        result = 31 * result + color.hashCode()
        result = 31 * result + selectedColor.hashCode()
        return result
    }

    override fun toString(): String {
        return "RadialMenuSetting(color=$color, selectedColor=$selectedColor, iconScale=$iconScale, innerRadius=$innerRadius, outerRadius=$outerRadius, optionRadius=$optionRadius, gap=$gap, singlePageMaxCount=$pageSize)"
    }

}