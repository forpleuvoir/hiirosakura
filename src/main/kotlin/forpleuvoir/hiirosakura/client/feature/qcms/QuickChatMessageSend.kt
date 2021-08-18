package forpleuvoir.hiirosakura.client.feature.qcms

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.StringUtil.translatableText
import net.minecraft.text.*
import java.util.*
import java.util.function.Consumer

/**
 * 快捷发送聊天消息
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.qcms
 *
 * #class_name QuickChatMessageSend
 *
 * #create_time 2021/6/16 22:12
 */
class QuickChatMessageSend : AbstractHiiroSakuraData("quick_chat_message_send") {
	private val data: MutableMap<String, String?> = HashMap()
	fun add(remark: String, messageStr: String?): Boolean {
		return if (data.containsKey(remark)) false else put(remark, messageStr)
	}

	val keySet: Set<String>
		get() {
			val set: MutableSet<String> = HashSet()
			data.keys.forEach { key: String? ->
				set.add("\"$key\"")
			}
			return set
		}

	fun put(remark: String, messageStr: String?): Boolean {
		if (getKeyLength(remark) < 1) return false
		data[remark] = messageStr
		onValueChanged()
		return true
	}

	fun remove(remark: String) {
		if (data.remove(remark) != null) onValueChanged()
	}

	fun reset(oldRemark: String, newRemark: String, newValue: String) {
		if (data.containsKey(oldRemark)) {
			if (oldRemark != newRemark) {
				rename(oldRemark, newRemark)
			}
			put(newRemark, newValue)
		}
	}

	fun rename(oldRemark: String, newRemark: String) {
		if (data.containsKey(oldRemark)) {
			val value = data[oldRemark]
			data.remove(oldRemark)
			data[newRemark] = value
			onValueChanged()
		}
	}

	private val textData: LinkedList<QuickChatMessage>
		get() {
			val sortedData = HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.getSortedData()
			HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.unSortedData.forEach(Consumer {
				sortedData.addLast(it)
			})
			return sortedData
		}

	fun getData(): Map<String, String?> {
		return data
	}

	val asText: Text
		get() {
			if (data.isEmpty()) {
				return translatableText("%s.feature.qcms.data.empty")
			}
			val text: MutableText = LiteralText("")
			val iterator: Iterator<QuickChatMessage> = textData.iterator()
			while (iterator.hasNext()) {
				val next = iterator.next()
				text.append(
					Texts.bracketed(
						LiteralText("")
							.append(next.remark.replace("&", "§"))
							.styled { style: Style ->
								style.withClickEvent(
									ClickEvent(
										ClickEvent.Action.RUN_COMMAND, next.message
									)
								)
									.withHoverEvent(
										HoverEvent(
											HoverEvent.Action.SHOW_TEXT,
											LiteralText(next.message)
										)
									)
							}
					)
				)
				if (iterator.hasNext()) text.append(LiteralText(",  "))
			}
			return text
		}

	override fun setValueFromJsonElement(element: JsonElement?) {
		try {
			if (element!!.isJsonObject) {
				val `object` = element.asJsonObject
				val data = JsonUtil.gson.fromJson<Map<String, String?>>(
					`object`,
					object : TypeToken<Map<String?, String?>?>() {}.type
				)
				this.data.clear()
				this.data.putAll(data)
			} else {
				log.warn("{}无法从JsonElement{}中读取数据", name, element)
			}
		} catch (e: Exception) {
			log.warn("{}无法从JsonElement{}中读取数据", name, element, e)
		}
	}

	override val asJsonElement: JsonElement
		get() = JsonUtil.gson.toJsonTree(data)

	companion object {
		@Transient
		private val log = getLogger(QuickChatMessageSend::class.java)

		@JvmStatic
		fun getKeyLength(str: String): Int {
			return str.replace("(&.)".toRegex(), "").length
		}
	}
}