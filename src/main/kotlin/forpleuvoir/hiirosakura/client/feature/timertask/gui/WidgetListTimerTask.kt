package forpleuvoir.hiirosakura.client.feature.timertask.gui

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.TIMER_TASK
import forpleuvoir.ibuki_gourd.gui.widget.WidgetList
import net.minecraft.client.gui.screen.Screen

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 WidgetListTimerTask

 * 创建时间 2022/1/19 20:13

 * @author forpleuvoir

 */
class WidgetListTimerTask(
	parent: Screen,
	x: Int,
	y: Int,
	pageSize: Int,
	itemHeight: Int,
	width: Int
) : WidgetList<WidgetListEntryTimerTask>(parent, x, y, pageSize, itemHeight, width) {

	init {
		refresh()
	}

	fun refresh() {
		clearEntries()
		TIMER_TASK.data().forEach {
			addEntry(WidgetListEntryTimerTask(it, this, 0, 0, this.contentWidth, this.itemHeight))
		}
	}
}