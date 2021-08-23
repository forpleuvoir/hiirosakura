package forpleuvoir.hiirosakura.client.feature.tooltip

import com.google.common.collect.Lists
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import forpleuvoir.hiirosakura.client.config.Configs
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData
import forpleuvoir.hiirosakura.client.util.HSLogger.Companion.getLogger
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.PlayerHeadUtil
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import java.util.*

/**
 * 工具提示
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.tooltip
 *
 * #class_name Tooltip
 *
 * #create_time 2021/6/17 0:55
 */
class Tooltip : AbstractHiiroSakuraData("tooltip") {
	val data: MutableMap<String, MutableList<String>> = HashMap()
	private fun add(key: String, vararg tooltip: String) {
		if (tooltip.isEmpty()) return
		val tooltips: MutableList<String> = Lists.newArrayList()
		Arrays.stream(tooltip).forEach { s: String -> tooltips.add(s.replace("&", "§")) }
		if (data.containsKey(key)) {
			data[key]!!.addAll(tooltips)
		} else {
			data[key] = tooltips
		}
		onValueChanged()
	}

	fun add(item: ItemStack, vararg tooltip: String) {
		add(getKey(item), *tooltip)
	}

	fun remove(item: ItemStack, index: Int): String? {
		return remove(getKey(item), index)
	}

	private fun remove(key: String, index: Int): String? {
		if (data.containsKey(key)) {
			var s = StringBuilder()
			if (index < 0) {
				for (s1 in data[key]!!) {
					s.append(s1)
					s.append(",")
				}
				s.deleteCharAt(s.length - 1)
				data.remove(key)
			} else {
				try {
					s = StringBuilder(data[key]!![index])
					data[key]!!.removeAt(index)
					if (data[key]!!.size == 0) {
						data.remove(key)
					}
				} catch (e: IndexOutOfBoundsException) {
					return null
				}
			}
			onValueChanged()
			return s.toString()
		}
		return null
	}

	fun getTooltip(stack: ItemStack): List<Text> {
		val list = ArrayList<Text>()
		if (Configs.Toggles.SHOW_TOOLTIP.booleanValue) {
			val key = getKey(stack)
			if (data.containsKey(key)) {
				val tips: List<String> = data[key]!!
				tips.forEach { list.add(LiteralText(it)) }
			}
		}
		return list
	}

	override fun setValueFromJsonElement(element: JsonElement) {
		try {
			if (element.isJsonObject) {
				val obj = element.asJsonObject
				val data = JsonUtil.gson
					.fromJson<Map<String, MutableList<String>>>(
						obj,
						object : TypeToken<Map<String?, List<String?>?>?>() {}.type
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
		private val log = getLogger(Tooltip::class.java)
		fun getKey(stack: ItemStack): String {
			var value = ""
			val item = stack.item
			val name = item.getName(stack).string
			val cName = stack.name.string
			if (stack.item == Items.PLAYER_HEAD) {
				value = ":" + PlayerHeadUtil.getSkullOwner(stack)
			} else if (name != cName) {
				value = "#$cName"
			}
			return item.getTranslationKey(stack) + value
		}
	}
}