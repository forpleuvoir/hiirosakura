package forpleuvoir.hiirosakura.client.util

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.gui.JsTextField
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.StringVisitable
import net.minecraft.text.TranslatableText


/**
 * 字符串工具
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.util
 *
 * #class_name StringUtil
 *
 * #create_time 2021/6/13 3:03
 */
object StringUtil {
	private val FILTER_CHARS = charArrayOf('\r', '\u000c')
	private val minecraft = MinecraftClient.getInstance()

	/**
	 * 将字符串分割为等长的字符串
	 *
	 * @param str    原字符串
	 * @param length 每一份长度
	 * @return 字符串数组
	 */
	fun strSplit(str: String, length: Int): Array<String?> {
		val len = str.length
		val arr = arrayOfNulls<String>((len + length - 1) / length)
		var i = 0
		while (i < len) {
			var n = len - i
			if (n > length) n = length
			arr[i / length] = str.substring(i, i + n)
			i += length
		}
		return arr
	}

	/**
	 * 过长的字符串将被省略超过长度的部分
	 *
	 * @param originalStr  源字符串
	 * @param length       最大长度
	 * @param suffix       后缀 如...
	 * @param keepLastChar 保留最后一个字符
	 * @return 处理之后的字符串
	 */
	@JvmStatic
	fun tooLongOmitted(originalStr: String, length: Int, suffix: String = "...", keepLastChar: Boolean = true): String {
		var original = originalStr
		var s = suffix
		if (keepLastChar) {
			s += original.substring(original.length - 1)
		}
		if (original.length > length) {
			original = original.substring(0, length) + s
		}
		return original
	}

	/**
	 * 检查字符串是否为空
	 *
	 * @return 是否为空
	 */
	@JvmStatic
	fun String?.isEmptyString(): Boolean = this == null || this.isEmpty()

	@JvmStatic
	fun translatableText(key: String, vararg params: Any?): TranslatableText {
		return TranslatableText("${HiiroSakuraClient.MOD_ID}.${key}", *params)
	}

	@JvmStatic
	fun wrapToWidth(str: String, wrapWidth: Int): MutableList<StringVisitable> {
		val strings: MutableList<StringVisitable> = ArrayList()
		var temp = StringBuilder()
		for (element in str) {
			run {
				if (element != '\n') {
					val text = temp.toString()
					if (minecraft.textRenderer.getWidth(text + element) < wrapWidth) {
						return@run
					}
				}
				strings.add(LiteralText(temp.toString()))
				temp = StringBuilder()
			}
			if (element != '\n') {
				temp.append(element)
			}
		}
		strings.add(LiteralText(temp.toString()))
		return strings
	}

	@JvmStatic
	fun wrapToWidthWithIndication(str: String, wrapWidth: Int): List<JsTextField.WrappedString> {
		val strings: MutableList<JsTextField.WrappedString> = ArrayList()
		var temp = StringBuilder()
		var wrapped = false
		for (element in str) {
			if (element == '\n') {
				strings.add(JsTextField.WrappedString(temp.toString(), wrapped))
				temp = StringBuilder()
				wrapped = false
			} else {
				val var10001 = temp.toString()
				if (minecraft.textRenderer.getWidth(var10001 + element) >= wrapWidth) {
					strings.add(JsTextField.WrappedString(temp.toString(), wrapped))
					temp = StringBuilder()
					wrapped = true
				}
			}
			if (element != '\n') {
				temp.append(element)
			}
		}
		strings.add(JsTextField.WrappedString(temp.toString(), wrapped))
		return strings
	}

	@JvmStatic
	fun insertStringAt(insert: String, insertTo: String, pos: Int): String {
		return insertTo.substring(0, pos) + insert + insertTo.substring(pos)
	}

	@JvmStatic
	fun filter(s: String): String {
		var filtered = s.replace('\t'.toString(), "    ")
		for (c in FILTER_CHARS) {
			filtered = filtered.replace(c.toString(), "")
		}
		return filtered
	}
}