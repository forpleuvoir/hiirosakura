package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import forpleuvoir.ibuki_gourd.config.options.ConfigBoolean
import forpleuvoir.ibuki_gourd.config.options.ConfigBooleanHotkey
import forpleuvoir.ibuki_gourd.keyboard.KeyBind
import java.util.*

/**
 * 开关热键
 *
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.config
 *
 * 文件名 TogglesHotKeys
 *
 * 创建时间 2021-07-27 16:17
 *
 *  @author forpleuvoir
 *
 */
object TogglesHotKeys {
	@JvmStatic
	val HOTKEY_LIST: ImmutableList<ConfigBooleanHotkey>

	init {
		val list = LinkedList<ConfigBooleanHotkey>()
		Configs.Toggles.CONFIGS.forEach {
			list.add(ConfigBooleanHotkey(defaultValue = KeyBind(), it as ConfigBoolean))
		}
		HOTKEY_LIST = ImmutableList.copyOf(list)
	}


}