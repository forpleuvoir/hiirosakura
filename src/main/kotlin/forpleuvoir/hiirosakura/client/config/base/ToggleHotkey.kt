package forpleuvoir.hiirosakura.client.config.base

import fi.dy.masa.malilib.config.options.ConfigBoolean
import fi.dy.masa.malilib.config.options.ConfigHotkey
import fi.dy.masa.malilib.hotkeys.IKeybind
import fi.dy.masa.malilib.hotkeys.KeyAction
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import net.minecraft.text.LiteralText

/**
 * 开关热键
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.config.base
 *
 * #class_name ToggleHotkey
 *
 * #create_time 2021-07-27 16:30
 */
class ToggleHotkey(private val toggle: ConfigBoolean) :
	ConfigHotkey(
		toggle.name.replace("hiirosakura.config.toggles.", "hiirosakura.config.toggles.hotkeys."),
		"",
		toggle.comment?.replace("hiirosakura.config.toggles.", "hiirosakura.config.toggles.hotkeys.")
	) {


	fun initCallback(hs: HiiroSakuraClient) {
		keybind.setCallback { _: KeyAction?, _: IKeybind? ->
			toggle.booleanValue = !toggle.booleanValue
			hs.showMessage(
				LiteralText("§b" + toggle.prettyName)
					.append(LiteralText(if (toggle.booleanValue) " :§a true" else " :§4 false"))
			)
			true
		}
	}
}