package forpleuvoir.hiirosakura.client.util

import java.awt.Color

/**
 * 颜色
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name Colors
 *
 * #create_time 2021/6/24 20:17
 */
enum class Colors(private val color: Int) {
	FORPLEUVOIR(-0x8100), DHWUIA(-0xe81a1b), YUYUKOSAMA(-0x14a852);

	/**
	 * 这段代码属实烂
	 * @param alpha 透明度 0-255
	 * @return 颜色
	 */
	fun getColor(alpha: Int): Int {
		var a = alpha
		a = 0.coerceAtLeast(a)
		a = 255.coerceAtMost(a)
		val c = Color(color)
		val color = Color(c.red, c.blue, c.green, a)
		return color.rgb
	}
}