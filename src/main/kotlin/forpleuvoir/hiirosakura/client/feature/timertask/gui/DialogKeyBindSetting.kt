package forpleuvoir.hiirosakura.client.feature.timertask.gui

import forpleuvoir.ibuki_gourd.config.gui.DialogHotkeySetting
import forpleuvoir.ibuki_gourd.gui.button.ButtonOption
import forpleuvoir.ibuki_gourd.gui.dialog.DialogBase
import forpleuvoir.ibuki_gourd.keyboard.KeyBind
import forpleuvoir.ibuki_gourd.keyboard.KeyEnvironment
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import net.minecraft.client.gui.screen.Screen

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 DialogKeyBindSetting

 * 创建时间 2022/1/19 20:43

 * @author forpleuvoir

 */
class DialogKeyBindSetting(
	private val keyBind: KeyBind,
	dialogWidth: Int,
	dialogHeight: Int,
	parent: Screen?,
	private val keyBindChangedCallback: (KeyEnvironment) -> Unit
) : DialogBase<DialogHotkeySetting>(dialogWidth, dialogHeight, IbukiGourdLang.KeyEnvironment.tText(), parent) {

	private val values = ArrayList<String>().apply {
		KeyEnvironment.values().forEach {
			this.add(it.name)
		}
	}

	private lateinit var envButton: ButtonOption


	override fun init() {
		super.init()
		envButton = ButtonOption(
			values,
			keyBind.keyEnvironment.name,
			x = this.x + this.paddingLeft + 5,
			y = this.paddingTop + this.y + this.contentHeight / 2 - 20 / 2,
			width = this.dialogWidth - (this.paddingLeft + this.paddingRight) - 10,
		) {
			keyBind.keyEnvironment = KeyEnvironment.valueOf(it)
			keyBindChangedCallback.invoke(keyBind.keyEnvironment)
		}
		addDrawableChild(envButton)
	}

}