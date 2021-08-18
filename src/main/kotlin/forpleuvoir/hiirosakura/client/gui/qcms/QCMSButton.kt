package forpleuvoir.hiirosakura.client.gui.qcms

import fi.dy.masa.malilib.gui.button.ButtonGeneric
import net.minecraft.text.Text
import java.util.*

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui.qcms
 *
 * #class_name QCMSButton
 *
 * #create_time 2021/7/17 1:00
 */
class QCMSButton(x: Int, y: Int, width: Int, height: Int, text: String?, vararg hoverText: Text?) :
	ButtonGeneric(x, y, width, height, text, "") {
	private val hoverText: LinkedList<Text> = LinkedList()
	fun getHoverText(): List<Text> {
		return hoverText
	}

	override fun hasHoverText(): Boolean {
		return hoverText.isNotEmpty()
	}

	init {
		hoverText.let { it ->
			if (hoverText.isNotEmpty()) {
				it.forEach { this.hoverText.add(it!!) }
			}
		}
	}
}

