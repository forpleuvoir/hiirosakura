package forpleuvoir.hiirosakura.client.feature.event.gui

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.event.events.Events
import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.screen.IScreenTabEntry
import forpleuvoir.ibuki_gourd.gui.screen.ScreenTab
import forpleuvoir.ibuki_gourd.gui.widget.SearchBar
import forpleuvoir.ibuki_gourd.gui.widget.WidgetDropList
import forpleuvoir.ibuki_gourd.utils.text
import java.util.regex.PatternSyntaxException

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 EventScreenTab

 * 创建时间 2022/1/21 16:31

 * @author forpleuvoir

 */
class EventScreenTab(tabEntry: IScreenTabEntry) : ScreenTab(tabEntry) {

	private lateinit var searchBar: SearchBar
	private val searchBarHeight: Int = 20
	private var scrollAmount: Double = 0.0

	private lateinit var listWidget: WidgetListEventSubscriber

	private lateinit var addButton: Button

	private lateinit var dropList: WidgetDropList<String>


	override fun init() {
		super.init()
		initAddButton()
		initDropList()
		initSearchBar()
		initListWidget((this.height - (searchBarHeight + searchBar.y + margin)) / 24)
		addDrawableChild(dropList)
	}


	private fun initAddButton() {
		addButton = Button(
			15,
			top + margin + 20,
			"${HiiroSakuraClient.modId}.screen.event.subscribe".tText()
		) {
			openScreen(
				EventSubscriberEditor(400.coerceAtMost(this.width), 320.coerceAtMost(this.height), this)
			)
		}
		this.addDrawableChild(addButton)
	}

	private fun initDropList() {
		val none = "-"
		val list = ArrayList<String>().apply {
			add(none)
			Events.getEvents().forEach { this.add(it.simpleName) }
		}
		dropList = WidgetDropList(
			items = list,
			default = none,
			stringAdapter = { it },
			entryAdapter = { it },
			hoverTextAdapter = { s ->
				if (s == none) listOf(s.text)
				else listOf(Events.getDescription(s)!!)
			},
			parent = this,
			pageSize = 10,
			itemHeight = 16,
			x = addButton.x + addButton.width + 2,
			y = top + margin + 20 + 2,
			width = 160
		).apply {
			onExpandChangedCallback = {
				listWidget.toggleRenderHoverText(!it)
			}
		}
	}

	private fun initSearchBar() {
		searchBar =
			SearchBar(
				addButton.x + addButton.width + 2 + 160 + 2,
				top + margin + 20,
				this.width - addButton.width - 160 - 15 * 2 - 2,
				searchBarHeight
			)
		this.addDrawableChild(searchBar)
	}


	private fun initListWidget(pageSize: Int) {
		listWidget = WidgetListEventSubscriber(this, 0, searchBar.y + searchBarHeight + margin, pageSize, 24, this.width)
		listWidget.setFilter {
			if (it.eventSubscriber.event.simpleName == dropList.current || dropList.current == "-") {
				try {
					val regex = Regex(searchBar.text)
					return@setFilter regex.run {
						containsMatchIn(it.eventSubscriber.name) ||
								containsMatchIn(it.eventSubscriber.event.simpleName) ||
								containsMatchIn(it.eventSubscriber.enabled.toString()) ||
								containsMatchIn(it.eventSubscriber.runnerType.name)
					}
				} catch (_: PatternSyntaxException) {
					return@setFilter true
				}
			}
			return@setFilter false
		}
		listWidget.scrollbar.amount = scrollAmount
		listWidget.setScrollAmountConsumer {
			scrollAmount = it
		}
		listWidget.setHoverCallback { entry -> drawTopMessage(entry.eventSubscriber.name.text) }
		this.addDrawableChild(listWidget)
	}


	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
		if (this::dropList.isInitialized && dropList.expand) {
			if (dropList.isMouseOver(mouseX, mouseY)) {
				if (dropList.mouseScrolled(mouseX, mouseY, amount)) return true
			}
		}
		return super.mouseScrolled(mouseX, mouseY, amount)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (this::dropList.isInitialized && dropList.expand) {
			if (dropList.isMouseOver(mouseX, mouseY)) {
				if (dropList.mouseClicked(mouseX, mouseY, button)) return true
			}
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun tick() {
		searchBar.tick()
	}
}