package forpleuvoir.hiirosakura.client.gui.task

import fi.dy.masa.malilib.gui.GuiListBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskBase
import forpleuvoir.hiirosakura.client.gui.GuiConfig
import forpleuvoir.hiirosakura.client.gui.event.EventScreen


/**
 * 任务屏幕

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.task

 * 文件名 TaskScreen

 * 创建时间 2021/8/23 23:17

 * @author forpleuvoir

 */
class TaskScreen : GuiListBase<TimeTaskBase, WidgetTimeTaskEntry, WidgetListTimeTask>(10, 64),
	ISelectionListener<TimeTaskBase> {

	init {
		setTitle(StringUtils.translate("hiirosakura.gui.title.task"))
	}

	override fun initGui() {
		super.initGui()
		GuiConfig.tab = GuiConfig.ConfigGuiTab.TASK
		clearWidgets()
		clearButtons()
		createTabButtons()
	}


	private fun createTabButtons() {
		var x = 10
		var y = 26
		var rows = 1
		for (tab in GuiConfig.ConfigGuiTab.values()) {
			val width = getStringWidth(tab.displayName) + 10
			if (x >= this.width - width - 10) {
				x = 10
				y += 22
				rows++
			}
			x += createTabButton(x, y, width, tab)
		}
		setListPosition(listX, 68 + (rows - 1) * 22)
		this.listWidget!!.setSize(this.browserWidth, this.browserHeight)
		this.listWidget!!.initGui()
		y += 20
		x = listX
		x += this.addButton(x, y)
	}

	private fun addButton(x: Int, y: Int): Int {
		val button = ButtonGeneric(x, y, -1, false, StringUtils.translate("hiirosakura.gui.button.add"))
		this.addButton(button) { _: ButtonBase?, _: Int ->
			openGui(TimeTaskEditScreen(parentScreen = this))
		}
		return button.width
	}

	private fun createTabButton(x: Int, y: Int, width: Int, tab: GuiConfig.ConfigGuiTab): Int {
		val button = ButtonGeneric(x, y, width, 20, tab.displayName)
		button.setEnabled(GuiConfig.tab != tab)
		this.addButton(button) { _: ButtonBase, _: Int ->
			GuiConfig.tab = tab
			if(tab==GuiConfig.ConfigGuiTab.EVENT){
				openGui(EventScreen())
			}else{
				openGui(GuiConfig())
			}

		}
		return button.width + 2
	}

	override fun createListWidget(listX: Int, listY: Int): WidgetListTimeTask {
		return WidgetListTimeTask(listX, listY, this.browserWidth, this.browserHeight, this,this)
	}

	override fun getBrowserWidth(): Int {
		return this.width - 20
	}

	override fun getBrowserHeight(): Int {
		return height - listY - 6
	}

	override fun onSelectionChange(entry: TimeTaskBase?) {
		val old = HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.selected
		HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.selected = if (old === entry) null else entry
	}

	override fun isPauseScreen(): Boolean {
		return false
	}

}