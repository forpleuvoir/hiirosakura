package forpleuvoir.hiirosakura.client.config

import com.google.common.collect.ImmutableList
import forpleuvoir.hiirosakura.client.feature.cameraentity.SwitchCameraEntity
import forpleuvoir.hiirosakura.client.feature.timertask.gui.qtte.QtteScreen
import forpleuvoir.hiirosakura.client.gui.HiiroSakuraScreen
import forpleuvoir.ibuki_gourd.config.options.ConfigHotkey
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase
import forpleuvoir.ibuki_gourd.keyboard.KeyBind
import net.minecraft.client.util.InputUtil

/**
 * 热键配置
 *
 * @author forpleuvoir
 *
 * 项目名 hiirosakura
 *
 * 包名 forpleuvoir.hiirosakura.client.config
 *
 * 文件名 HotKeys
 *
 * 创建时间 2021/6/15 23:28
 */
object HotKeys {
	private val OPEN_CONFIG_GUI =
		ConfigHotkey(name = translationKey("openConfig"), defaultValue = KeyBind(InputUtil.GLFW_KEY_H, InputUtil.GLFW_KEY_S) {
			HiiroSakuraScreen.openScreen(ScreenBase.current)
		})

	private val OPEN_QTTE =
		ConfigHotkey(name = translationKey("openQtte"), defaultValue = KeyBind() {
			ScreenBase.openScreen(QtteScreen(ScreenBase.current))
		})

	private val SWITCH_CAMERA_ENTITY =
		ConfigHotkey(name = translationKey("switchCameraEntity"), defaultValue = KeyBind() {
			SwitchCameraEntity.switchEntity()
		})

	@JvmField
	val HOTKEY_LIST: ImmutableList<ConfigHotkey> = ImmutableList.of(
		OPEN_CONFIG_GUI, OPEN_QTTE, SWITCH_CAMERA_ENTITY
	)

	private fun translationKey(key: String): String {
		return Configs.translationKey("hotkeys", key)
	}

}