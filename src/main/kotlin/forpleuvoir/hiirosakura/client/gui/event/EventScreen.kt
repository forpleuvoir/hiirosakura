package forpleuvoir.hiirosakura.client.gui.event

import com.google.common.collect.Lists
import fi.dy.masa.malilib.gui.GuiListBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents
import forpleuvoir.hiirosakura.client.gui.GuiConfig
import forpleuvoir.hiirosakura.client.gui.task.TaskScreen
import net.minecraft.client.util.math.MatrixStack

/**
 * 事件管理界面
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui.event
 *
 * #class_name EventScreen
 *
 * #create_time 2021-07-28 15:54
 */
class EventScreen : GuiListBase<EventSubscriberBase, WidgetEventEntry, WidgetListEvent>(10, 64),
	ISelectionListener<EventSubscriberBase?> {
	private val widgetDropDown: WidgetDropDownList<String>
	private var currentEntry: String?

	companion object {
		const val ALL = "all"
	}

	init {
		val lists = Lists.newLinkedList(HiiroSakuraEvents.events.keys)
		lists.addFirst(ALL)
		setTitle(StringUtils.translate("hiirosakura.gui.title.event"))
		widgetDropDown = WidgetDropDownList(
			0, 0, 160, 17, 200, 10,
			lists
		) { type: String -> "§6§l§n$type" }
		widgetDropDown.selectedEntry = ALL
		currentEntry = ALL
	}

	override fun createListWidget(listX: Int, listY: Int): WidgetListEvent {
		return WidgetListEvent(listX, listY, this.browserWidth, this.browserHeight, currentEntry, this)
	}

	override fun getBrowserWidth(): Int {
		return width - 20
	}

	override fun getBrowserHeight(): Int {
		return height - listY - 6
	}

	override fun initGui() {
		GuiConfig.tab = GuiConfig.ConfigGuiTab.EVENT
		super.initGui()
		clearWidgets()
		clearButtons()
		createTabButtons()
	}

	override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
		super.render(matrixStack, mouseX, mouseY, partialTicks)
		if (currentEntry != widgetDropDown.selectedEntry) {
			currentEntry = widgetDropDown.selectedEntry
			reCreateListWidget()
			initGui()
		}
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
		widgetDropDown.setPosition(x + 1, y + 2)
		addWidget(widgetDropDown)
	}

	private fun createTabButton(x: Int, y: Int, width: Int, tab: GuiConfig.ConfigGuiTab): Int {
		val button = ButtonGeneric(x, y, width, 20, tab.displayName)
		button.setEnabled(GuiConfig.tab != tab)
		this.addButton(button) { _: ButtonBase, _: Int ->
			GuiConfig.tab = tab
			if (tab == GuiConfig.ConfigGuiTab.TASK) {
				openGui(TaskScreen())
			} else {
				openGui(GuiConfig())
			}

		}
		return button.width + 2
	}

	private fun addButton(x: Int, y: Int): Int {
		val button = ButtonGeneric(x, y, -1, false, "hiirosakura.gui.button.subscribe")
		this.addButton(button) { _: ButtonBase?, _: Int -> openGui(EventEditScreen(this)) }
		return button.width
	}

	override fun onSelectionChange(entry: EventSubscriberBase?) {
		val old = HiiroSakuraData.HIIRO_SAKURA_EVENTS.selected
		HiiroSakuraData.HIIRO_SAKURA_EVENTS.selected = if (old === entry) null else entry
	}

	override fun isPauseScreen(): Boolean {
		return false
	}


}