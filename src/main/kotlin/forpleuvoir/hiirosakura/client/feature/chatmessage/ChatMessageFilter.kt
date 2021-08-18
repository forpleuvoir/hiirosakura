package forpleuvoir.hiirosakura.client.feature.chatmessage

import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_FILTER_REGEX
import net.minecraft.text.Text
import java.util.regex.Pattern

/**
 * 聊天消息过滤
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.chatmessage
 *
 * #class_name ChatMessageFilter
 *
 * #create_time 2021/7/17 12:13
 */
object ChatMessageFilter {
	fun needToFilter(message: Text): Boolean {
		var matched = false
		for (regex in CHAT_MESSAGE_FILTER_REGEX.strings) {
			if (Pattern.matches(regex, message.string)) {
				matched = true
			}
		}
		return matched
	}
}