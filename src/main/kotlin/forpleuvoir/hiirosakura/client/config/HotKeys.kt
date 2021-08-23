package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import fi.dy.masa.malilib.config.options.ConfigHotkey
import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.hotkeys.IKeybind
import fi.dy.masa.malilib.hotkeys.KeyAction
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity
import forpleuvoir.hiirosakura.client.gui.GuiConfig
import forpleuvoir.hiirosakura.client.gui.qcms.QCMSScreen

/**
 * 热键配置
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config
 *
 * #class_name HotKeys
 *
 * #create_time 2021/6/15 23:28
 */
object HotKeys {
	private val OPEN_CONFIG_GUI = ConfigHotkey(
		translationKey("openConfig"), "H,S",
		translationKey("openConfig.comment")
	)
	private val OPEN_QCMS = ConfigHotkey(
		translationKey("openQcms"), "",
		translationKey("openQcms.comment")
	)
	private val SWITCH_CAMERA_ENTITY = ConfigHotkey(
		translationKey("switchCameraEntity"), "",
		translationKey("switchCameraEntity.comment")
	)

	@JvmField
	val HOTKEY_LIST: ImmutableList<ConfigHotkey> = ImmutableList.of(
		OPEN_CONFIG_GUI, OPEN_QCMS, SWITCH_CAMERA_ENTITY
	)

	private fun translationKey(key: String?): String {
		return Configs.translationKey("hotkeys", key)
	}

	fun initCallback(hs: HiiroSakuraClient) {
		OPEN_CONFIG_GUI.keybind.setCallback { _: KeyAction?, _: IKeybind? ->
			GuiBase.openGui(GuiConfig())
			true
		}
		OPEN_QCMS.keybind.setCallback { _: KeyAction?, _: IKeybind? ->
			if (Configs.Toggles.ENABLE_QCMS_GUI.booleanValue) {
				GuiBase.openGui(QCMSScreen())
			} else {
				hs.showMessage(HiiroSakuraData.QUICK_CHAT_MESSAGE_SEND.asText)
			}
			true
		}
		SWITCH_CAMERA_ENTITY.keybind.setCallback { _: KeyAction?, _: IKeybind? ->
			SwitchCameraEntity.switchEntity()
			true
		}
	}
}