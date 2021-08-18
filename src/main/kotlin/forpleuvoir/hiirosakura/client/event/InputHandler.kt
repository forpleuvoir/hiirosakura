package forpleuvoir.hiirosakura.client.event

import fi.dy.masa.malilib.hotkeys.IKeybindManager
import fi.dy.masa.malilib.hotkeys.IKeybindProvider
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HotKeys
import forpleuvoir.hiirosakura.client.config.TogglesHotKeys

/**
 * 输入处理程序
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.event
 *
 * #class_name InputHandler
 *
 * #create_time 2021/6/15 23:52
 */
object InputHandler : IKeybindProvider {
	override fun addKeysToMap(manager: IKeybindManager) {
		for (hotkey in HotKeys.HOTKEY_LIST) {
			manager.addKeybindToMap(hotkey.keybind)
		}
		for (togglesHotkey in TogglesHotKeys.HOTKEY_LIST) {
			manager.addKeybindToMap(togglesHotkey.keybind)
		}
	}

	override fun addHotkeys(manager: IKeybindManager) {
		manager.addHotkeysForCategory(
			HiiroSakuraClient.MOD_NAME,
			"${HiiroSakuraClient.MOD_ID}.hotkeys",
			HotKeys.HOTKEY_LIST
		)
		manager.addHotkeysForCategory(
			HiiroSakuraClient.MOD_NAME,
			"${HiiroSakuraClient.MOD_ID}.hotkeys.toggles",
			TogglesHotKeys.HOTKEY_LIST
		)
	}

}