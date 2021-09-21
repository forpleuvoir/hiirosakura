package forpleuvoir.hiirosakura.client.gui.task

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskBase
import net.minecraft.client.gui.screen.Screen


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.task

 * 文件名 WidgetListTimeTask

 * 创建时间 2021/8/24 22:46

 * @author forpleuvoir

 */
class WidgetListTimeTask(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    selectionListener: ISelectionListener<TimeTaskBase>?,
    val parentScreen: Screen? = null
) : WidgetListBase<TimeTaskBase, WidgetTimeTaskEntry>(x, y, width, height, selectionListener) {

    override fun getAllEntries(): Collection<TimeTaskBase> {
        return HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.sortList()
    }

    init {
        browserEntryHeight = 22
    }

    override fun createListEntryWidget(
        x: Int,
        y: Int,
        listIndex: Int,
        isOdd: Boolean,
        entry: TimeTaskBase
    ): WidgetTimeTaskEntry {
        return WidgetTimeTaskEntry(
            isOdd,
            this,
            x,
            y,
            browserEntryWidth,
            getBrowserEntryHeightFor(entry),
            entry,
            listIndex
        )
    }
}