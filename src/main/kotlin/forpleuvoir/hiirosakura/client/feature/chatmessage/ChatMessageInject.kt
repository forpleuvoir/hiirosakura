package forpleuvoir.hiirosakura.client.feature.chatmessage

import forpleuvoir.hiirosakura.client.config.Configs.Toggles.CHAT_MESSAGE_INJECT
import forpleuvoir.hiirosakura.client.config.Configs.Toggles.ENABLE_CHAT_MESSAGE_INJECT_REGEX
import forpleuvoir.hiirosakura.client.config.Configs.Toggles.REVERSE_CHAT_MESSAGE_INJECT_REGEX
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_INJECT_EXP
import forpleuvoir.hiirosakura.client.config.Configs.Values.CHAT_MESSAGE_INJECT_FILTER
import java.util.regex.Pattern

/**
 * 聊天消息注入
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.chatmessage
 *
 * 文件名 ChatMessageInject
 *
 * 创建时间 2021/6/24 22:16
 */
object ChatMessageInject {
	private const val originMsg = "#{message}"

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
		if (ENABLE_CHAT_MESSAGE_INJECT_REGEX.getValue()) {
			var matched = false
			for (regex in CHAT_MESSAGE_INJECT_FILTER.getValue()) {
				if (Pattern.matches(regex, message)) {
					matched = true
				}
			}
			if (REVERSE_CHAT_MESSAGE_INJECT_REGEX.getValue()) matched = !matched
			returnValue = !matched
		}
		return returnValue
	}

	private fun setFix(message: String): String {
		return if (CHAT_MESSAGE_INJECT.getValue()) {
			CHAT_MESSAGE_INJECT_EXP.getValue().replace(originMsg, message)
		} else message
	}

}