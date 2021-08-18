package forpleuvoir.hiirosakura.client.feature.qcms

import java.util.*

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.qcms
 *
 * #class_name QuickChatMessage
 *
 * #create_time 2021/7/16 23:59
 */
class QuickChatMessage(val remark: String, val message: String, val level: Int?) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val that = other as QuickChatMessage
		return remark == that.remark && message == that.message && level == that.level
	}

	override fun hashCode(): Int {
		return Objects.hash(remark, message, level)
	}
}