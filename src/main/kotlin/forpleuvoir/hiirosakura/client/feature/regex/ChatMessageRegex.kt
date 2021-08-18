package forpleuvoir.hiirosakura.client.feature.regex

import net.minecraft.text.Text
import java.lang.Exception
import java.util.regex.Pattern

/**
 * 聊天消息正则匹配
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.regex
 *
 * #class_name ChatMessageRegex
 *
 * #create_time 2021/6/23 23:36
 */
class ChatMessageRegex private constructor(text: Text, regex: String) {
	var playerName: String? = null
	var message: String? = null
	private fun preString(text: Text): String {
		return text.string.replace("(§.)".toRegex(), "")
	}

	companion object {

		@JvmStatic
		fun getInstance(text: Text?, regex: String?): ChatMessageRegex? {
			return if (text != null && regex != null) ChatMessageRegex(text, regex) else null
		}

		private const val NAME = "name"
		private const val MESSAGE = "message"
	}

	init {
		val content = preString(text)
		val pattern = Pattern.compile(regex)
		val matcher = pattern.matcher(content)
		if (matcher.find()) {
			try {
				playerName = matcher.group(NAME)
				message = matcher.group(MESSAGE)
			} catch (ignore: Exception) {
			}
		}
	}
}