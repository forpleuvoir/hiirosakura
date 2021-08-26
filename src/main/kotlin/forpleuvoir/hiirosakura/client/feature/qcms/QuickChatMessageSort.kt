package forpleuvoir.hiirosakura.client.feature.qcms

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import java.util.*

/**
 * 快捷聊天消息发送排序
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.qcms
 *
 * #class_name QuickChatMessageSort
 *
 * #create_time 2021/7/16 23:36
 */
class QuickChatMessageSort : AbstractHiiroSakuraData("quickChatMessageSort") {
	private val data: MutableMap<String, Int?> = HashMap()
	fun setSort(remark: String, level: Int) {
		data[remark] = level
		onValueChanged()
	}

	fun remove(key: String) {
		if (data.remove(key) != null) onValueChanged()
	}

	/**
	 * 获取排序等级
	 *
	 * @param remark 对应备注
	 * @return 排序等级 未被排序时返回null
	 */
	fun getLevel(remark: String): Int? {
		return data.getOrDefault(remark, null)
	}

	fun resetLevel(oldRemark: String?, newRemark: String?, level: Int?) {
		if (oldRemark == null || newRemark == null) return
		if (data.containsKey(oldRemark)) {
			data.remove(oldRemark)
			if (level != null) setSort(newRemark, level) else onValueChanged()
		} else {
			if (level != null) setSort(newRemark, level)
		}
	}


	/**
	 * 获取排序后的QCMS数据
	 *
	 * @param isReversed 是由有小到大排序
	 * @return LinkedList<QuickChatMessage>
	 */
	fun getSortedData(isReversed: Boolean = true): LinkedList<QuickChatMessage> {
		val list = LinkedList<QuickChatMessage>()
		val data = HiiroSakuraData.QUICK_CHAT_MESSAGE_SEND.getData()
		if (data.isNotEmpty()) {
			val values: Collection<Int?> = this.data.values
			val sorted = values.stream().sorted()
			if (!values.isEmpty()) {
				sorted.forEach { value: Int? ->
					this.data.forEach { (k: String, v: Int?) ->
						if (v == value) {
							val quickChatMessage = QuickChatMessage(k, data[k]!!, this.data[k]!!)
							if (!list.contains(quickChatMessage)) {
								if (isReversed) list.addLast(quickChatMessage) else list.addFirst(quickChatMessage)
							}
						}
					}
				}
			}
		}
		return list
	}

	/**
	 * 获取未排序的数据
	 *
	 * @return [<]
	 */
	val unSortedData: LinkedList<QuickChatMessage>
		get() {
			val list = LinkedList<QuickChatMessage>()
			val data = HiiroSakuraData.QUICK_CHAT_MESSAGE_SEND.getData()
			val keySet: Set<String> = this.data.keys
			val set = data.keys
			set.stream().filter { !keySet.contains(it) }
				.forEach { key: String -> list.addLast(QuickChatMessage(key, data[key]!!, null)) }
			return list
		}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonObject) {
				val obj = element.asJsonObject
				val data = JsonUtil.gson.fromJson<Map<String, Int?>>(
					obj,
					object : TypeToken<Map<String?, Int?>?>() {}.type
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
		private val log = getLogger(QuickChatMessageSort::class.java)
	}
}