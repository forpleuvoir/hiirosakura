package forpleuvoir.hiirosakura.client.config.gui

import forpleuvoir.hiirosakura.client.gui.HiiroSakuraScreen
import forpleuvoir.ibuki_gourd.config.gui.IConfigGroup
import forpleuvoir.ibuki_gourd.config.gui.ScreenTabConfig
import forpleuvoir.ibuki_gourd.config.options.ConfigBase
import forpleuvoir.ibuki_gourd.gui.screen.IScreenTabEntry
import forpleuvoir.ibuki_gourd.gui.screen.ScreenTab
import net.minecraft.text.Text

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.config.gui

 * 文件名 HiiroSakuraConfigGroup

 * 创建时间 2022/1/18 13:02

 * @author forpleuvoir

 */
class HiiroSakuraConfigGroup(
	override val key: String,
	override val baseTitle: Text,
	override val configs: List<ConfigBase>
) : IConfigGroup {
	override val remark: String
		get() = key
	override val all: List<IScreenTabEntry>
		get() = HiiroSakuraScreen.allTabsEntry
	override val current: IScreenTabEntry
		get() = HiiroSakuraScreen.currentEntry
	override val screen: ScreenTab
		get() = ScreenTabConfig(24, this)

	override fun changeCurrent(current: IScreenTabEntry) {
		HiiroSakuraScreen.currentEntry = current
	}
}