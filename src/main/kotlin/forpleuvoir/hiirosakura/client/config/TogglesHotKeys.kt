package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import fi.dy.masa.malilib.config.options.ConfigBoolean
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.base.ToggleHotkey
import java.util.*

/**
 * 开关热键
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config
 *
 * #class_name TogglesHotKeys
 *
 * #create_time 2021-07-27 16:17
 */
object TogglesHotKeys {
	@JvmStatic
	val HOTKEY_LIST: ImmutableList<ToggleHotkey>

	init {
		val list = LinkedList<ToggleHotkey>()
		Configs.Toggles.OPTIONS.forEach {
			list.add(ToggleHotkey(it as ConfigBoolean))
		}
		HOTKEY_LIST = ImmutableList.copyOf(list)
	}

	fun initCallback(hs: HiiroSakuraClient?) {
		for (hotkey in HOTKEY_LIST) {
			hotkey.initCallback(hs!!)
		}
	}

}