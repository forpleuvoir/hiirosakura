package forpleuvoir.hiirosakura.client.feature.event.gui

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.ibuki_gourd.gui.widget.WidgetList
import net.minecraft.client.gui.screen.Screen

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 WidgetListEventSubscriber

 * 创建时间 2022/1/21 16:11

 * @author forpleuvoir

 */
class WidgetListEventSubscriber(
	parent: Screen,
	x: Int,
	y: Int,
	pageSize: Int,
	itemHeight: Int,
	width: Int
) : WidgetList<WidgetListEntryEventSubscriber>(parent, x, y, pageSize, itemHeight, width) {
	init {
		refresh()
	}

	fun refresh() {
		clearEntries()
		HiiroSakuraData.EVENT.data().forEach {
			addEntry(WidgetListEntryEventSubscriber(it, this, 0, 0, this.contentWidth, this.itemHeight))
		}
	}

	fun toggleRenderHoverText(renderHoverText: Boolean) {
		children.forEach {
			it.nameLabel.renderHoverText = renderHoverText
		}
	}
}