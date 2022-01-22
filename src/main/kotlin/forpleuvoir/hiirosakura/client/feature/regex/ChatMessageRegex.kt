package forpleuvoir.hiirosakura.client.feature.regex

import forpleuvoir.hiirosakura.client.config.Configs
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.regex.Pattern

/**
 * 聊天消息正则匹配
 *
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.feature.regex
 *
 * 文件名 ChatMessageRegex
 *
 * 创建时间 2021/6/23 23:36
 *
 *  @author forpleuvoir
 *
 */
class ChatMessageRegex private constructor(text: Text, regex: String) {
	var playerName: String? = null
	var message: String? = null

	private fun preString(text: Text): String {
		return text.string.replace(Regex("(§.)"), "")
	}

	companion object {
		private const val defaultRegex: String = "(<(?<name>(.*))>)\\s(?<message>.*)"
		private val currentServerAddress: String? get() = MinecraftClient.getInstance().currentServerEntry?.address

		@JvmStatic
		fun getInstance(text: Text): ChatMessageRegex {
			currentServerAddress?.let { address ->
				Configs.Values.CHAT_BUBBLE_REGEX[address]?.let {
					return ChatMessageRegex(text, it)
				}
			}
			return ChatMessageRegex(text, defaultRegex)
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