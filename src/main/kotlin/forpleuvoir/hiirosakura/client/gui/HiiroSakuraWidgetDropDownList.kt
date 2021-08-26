package forpleuvoir.hiirosakura.client.gui

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList
import fi.dy.masa.malilib.interfaces.IStringRetriever


/**
 * 带有切换事件的下拉菜单

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui

 * 文件名 HiiroSakuraWidgetDropDownList

 * 创建时间 2021/8/27 1:45

 * @author forpleuvoir

 */
class HiiroSakuraWidgetDropDownList<T>(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	maxHeight: Int,
	maxVisibleEntries: Int,
	entries: MutableList<T>?,
	stringRetriever: IStringRetriever<T>? = null,
	var onChangeSelected: ((T) -> Unit)? = null
) : WidgetDropDownList<T>(x, y, width, height, maxHeight, maxVisibleEntries, entries, stringRetriever) {

	override fun setSelectedEntry(index: Int) {
		super.setSelectedEntry(index)
		selectedEntry?.let {
			onChangeSelected?.invoke(it)
		}

	}
}