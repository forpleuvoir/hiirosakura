package forpleuvoir.hiirosakura.client.feature.chatmessage

import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_FILTER_REGEX
import net.minecraft.text.Text
import java.util.regex.Pattern

/**
 * 聊天消息过滤
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.chatmessage
 *
 * 文件名 ChatMessageFilter
 *
 * 创建时间 2021/7/17 12:13
 */
object ChatMessageFilter {
	@JvmStatic
	fun needToFilter(message: Text): Boolean {
		var matched = false
		for (regex in CHAT_MESSAGE_FILTER_REGEX.getValue()) {
			if (Pattern.matches(regex, message.string)) {
				matched = true
			}
		}
		return matched
	}
}