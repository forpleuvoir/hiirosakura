package forpleuvoir.hiirosakura.client.feature.regex

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.StringUtil.isEmptyString
import forpleuvoir.hiirosakura.client.feature.regex.ChatMessageRegex.Companion.getInstance
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.JsonUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

/**
 * 聊天消息正则匹配
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.regex
 *
 * #class_name ServerChatMessageRegex
 *
 * #create_time 2021/6/23 23:53
 */
class ServerChatMessageRegex : AbstractHiiroSakuraData("serverChatMessageRegex") {
	private val regex: MutableMap<String?, String> = HashMap()
	fun put(regexString: String): Boolean {
		if (address == null) {
			log.warn("{}获取服务器地址失败", name)
			return false
		}
		regex[address] = regexString
		onValueChanged()
		return true
	}

	fun remove(): Boolean {
		if (address == null) {
			log.warn("{}获取服务器地址失败", name)
			return false
		}
		regex.remove(address)
		return true
	}

	private fun getRegex(): String? {
		var regex: String?
		regex = this.regex[address]
		if (regex.isEmptyString()) {
			regex = defRegex
		}
		return regex
	}

	fun chatMessageRegex(text: Text?): ChatMessageRegex? {
		return getInstance(text, getRegex())
	}

	override fun setValueFromJsonElement(element: JsonElement?) {
		try {
			if (element!!.isJsonObject) {
				val `object` = element.asJsonObject
				val data = JsonUtil.gson.fromJson<Map<String?, String>>(
					`object`,
					object : TypeToken<Map<String?, String?>?>() {}.type
				)
				regex.clear()
				regex.putAll(data)
			} else {
				log.warn("{}无法从JsonElement{}中读取数据", name, element)
			}
		} catch (e: Exception) {
			log.warn("{}无法从JsonElement{}中读取数据", name, element, e)
		}
	}

	override val asJsonElement: JsonElement
		get() = JsonUtil.gson.toJsonTree(regex)

	companion object {
		@Transient
		private val log = getLogger(ServerChatMessageRegex::class.java)
		val defRegex: String = Configs.Values.CHAT_MESSAGE_DEFAULT_REGEX.stringValue
		val address: String?
			get() = try {
				MinecraftClient.getInstance().currentServerEntry?.address
			} catch (ignored: NullPointerException) {
				null
			}
	}
}