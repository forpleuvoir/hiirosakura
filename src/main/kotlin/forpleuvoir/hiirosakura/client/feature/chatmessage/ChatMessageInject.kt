package forpleuvoir.hiirosakura.client.feature.chatmessage

import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.Configs.Toggles.ENABLE_CHAT_MESSAGE_INJECT_REGEX
import forpleuvoir.hiirosakura.client.config.Configs.Toggles.REVERSE_CHAT_MESSAGE_INJECT_REGEX
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_INJECT_PREFIX
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_INJECT_REGEX
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_INJECT_SUFFIX
import java.util.regex.Pattern

/**
 * 聊天消息注入
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.chatmessage
 *
 * #class_name ChatMessageInject
 *
 * #create_time 2021/6/24 22:16
 */
object ChatMessageInject {
	@JvmStatic
	fun handlerMessage(message: String): String {
		var msg = message
		if (msg.startsWith("/")) return msg
		//添加前缀以及后缀
		if (needInject(msg)) msg = setFix(msg)
		return msg
	}

	private fun needInject(message: String): Boolean {
		var returnValue = true
		if (ENABLE_CHAT_MESSAGE_INJECT_REGEX.booleanValue) {
			var matched = false
			for (regex in CHAT_MESSAGE_INJECT_REGEX.strings) {
				if (Pattern.matches(regex, message)) {
					matched = true
				}
			}
			if (REVERSE_CHAT_MESSAGE_INJECT_REGEX.booleanValue) matched = !matched
			returnValue = !matched
		}
		return returnValue
	}

	private fun setFix(message: String): String {
		return if (Configs.Toggles.CHAT_MESSAGE_INJECT.booleanValue) String.format(
			"%s%s%s", CHAT_MESSAGE_INJECT_PREFIX.stringValue, message,
			CHAT_MESSAGE_INJECT_SUFFIX.stringValue
		) else message
	}

}