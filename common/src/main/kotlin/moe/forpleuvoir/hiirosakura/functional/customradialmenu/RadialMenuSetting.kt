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
    val innerColor: ARGBColor = Color.ofARGB(0x33000000),
    val outerColor: ARGBColor = Color.ofARGB(0x7F000000),
    val innerSelectedColor: ARGBColor = Color.ofARGB(0x33FF8899),
    val outerSelectedColor: ARGBColor = Color.ofARGB(0xFFFF8899),
    iconScale: Float = 1f,
    innerRadius: Float = 60f,
    outerRadius: Float = 120f,
    optionRadius: Float = 90f,
    gap: Float = 2f,
    pageSize: Int = 8
) : Serializable {

    companion object : Deserializer<RadialMenuSetting> {
        override fun deserialization(serializeElement: SerializeElement): RadialMenuSetting =
            serializeElement.checkType<SerializeObject, RadialMenuSetting> {
                RadialMenuSetting(
                    innerColor = it["inner_color"]?.let { element -> Color.deserialization(element) } ?: Color.ofARGB(0x33000000),
                    outerColor = it["outer_color"]?.let { element -> Color.deserialization(element) } ?: Color.ofARGB(0x7F000000),
                    innerSelectedColor = it["inner_selected_color"]?.let { element -> Color.deserialization(element) } ?: Color.ofARGB(0x33FF8899),
                    outerSelectedColor = it["outer_selected_color"]?.let { element -> Color.deserialization(element) } ?: Color.ofARGB(0xFFFF8899),
                    iconScale = it["icon_scale"]?.asFloat ?: 1f,
                    innerRadius = it["inner_radius"]?.asFloat ?: 60f,
                    outerRadius = it["outer_radius"]?.asFloat ?: 120f,
                    optionRadius = it["option_radius"]?.asFloat ?: 90f,
                    gap = it["gap"]?.asFloat ?: 2f,
                    pageSize = it["page_size"]?.asInt ?: 8
                )
            }.getOrThrow()
    }

    val iconScale: Float = iconScale.coerceIn(0.2f, 2f)
    val innerRadius: Float = innerRadius.coerceIn(40f, 100f)
    val outerRadius: Float = outerRadius.coerceIn(100f, 200f)
    val optionRadius: Float = optionRadius.coerceIn(50f, 190f)
    val gap: Float = gap.coerceIn(0f, 5f)
    val pageSize: Int = pageSize.coerceIn(4, 12)

    fun copy(
        innerColor: ARGBColor = this.innerColor,
        outerColor: ARGBColor = this.outerColor,
        innerSelectedColor: ARGBColor = this.innerSelectedColor,
        outerSelectedColor: ARGBColor = this.outerSelectedColor,
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

    override fun serialization(): SerializeElement = serializeObject {
        "inner_color" to innerColor.serialization()
        "outer_color" to outerColor.serialization()
        "inner_selected_color" to innerSelectedColor.serialization()
        "outer_selected_color" to outerSelectedColor.serialization()
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
        if (innerColor != other.innerColor) return false
        if (outerColor != other.outerColor) return false
        if (innerSelectedColor != other.innerSelectedColor) return false
        if (outerSelectedColor != other.outerSelectedColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconScale.hashCode()
        result = 31 * result + innerRadius.hashCode()
        result = 31 * result + outerRadius.hashCode()
        result = 31 * result + optionRadius.hashCode()
        result = 31 * result + gap.hashCode()
        result = 31 * result + pageSize
        result = 31 * result + innerColor.hashCode()
        result = 31 * result + outerColor.hashCode()
        result = 31 * result + innerSelectedColor.hashCode()
        result = 31 * result + outerSelectedColor.hashCode()
        return result
    }

    override fun toString(): String {
        return "RadialMenuSetting(innerColor=$innerColor, outerColor=$outerColor, innerSelectedColor=$innerSelectedColor, outerSelectedColor=$outerSelectedColor, iconScale=$iconScale, innerRadius=$innerRadius, outerRadius=$outerRadius, optionRadius=$optionRadius, gap=$gap, pageSize=$pageSize)"
    }


}