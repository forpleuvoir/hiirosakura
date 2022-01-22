package forpleuvoir.hiirosakura.client.feature.timertask.gui

import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.screen.IScreenTabEntry
import forpleuvoir.ibuki_gourd.gui.screen.ScreenTab
import forpleuvoir.ibuki_gourd.gui.widget.SearchBar
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import forpleuvoir.ibuki_gourd.utils.text
import java.util.regex.PatternSyntaxException

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 TimerTaskScreenTab

 * 创建时间 2022/1/19 20:06

 * @author forpleuvoir

 */
class TimerTaskScreenTab(tabEntry: IScreenTabEntry) : ScreenTab(tabEntry) {

	private lateinit var searchBar: SearchBar
	private val searchBarHeight: Int = 20
	private var scrollAmount: Double = 0.0

	private lateinit var listWidget: WidgetListTimerTask

	private lateinit var addButton: Button


	override fun init() {
		super.init()
		initAddButton()
		initSearchBar()
		initListWidget((this.height - (searchBarHeight + searchBar.y + margin)) / 24)
	}

	private fun initAddButton() {
		addButton = Button(
			15,
			top + margin + 20,
			IbukiGourdLang.Add.tText()
		) {
			openScreen(TimerTaskEditor(340.coerceAtMost(this.width), 320.coerceAtMost(this.height), this))
		}
		this.addDrawableChild(addButton)
	}

	private fun initSearchBar() {
		searchBar =
			SearchBar(
				15 + addButton.width + 2,
				top + margin + 20,
				this.width - addButton.width - 15 * 2 - 2,
				searchBarHeight
			)
		this.addDrawableChild(searchBar)
	}


	private fun initListWidget(pageSize: Int) {
		listWidget = WidgetListTimerTask(this, 0, searchBar.y + searchBarHeight + margin, pageSize, 24, this.width)
		listWidget.setFilter {
			try {
				val regex = Regex(searchBar.text)
				regex.run {
					containsMatchIn(it.timerTaskWrappedWithKeyBind.timerTask.name) ||
							containsMatchIn(it.timerTaskWrappedWithKeyBind.timerTask.delay.toString()) ||
							containsMatchIn(it.timerTaskWrappedWithKeyBind.timerTask.period.toString()) ||
							containsMatchIn(it.timerTaskWrappedWithKeyBind.timerTask.times.toString())
				}
			} catch (_: PatternSyntaxException) {
				true
			}
		}
		listWidget.scrollbar.amount = scrollAmount
		listWidget.setScrollAmountConsumer {
			scrollAmount = it
		}
		listWidget.setHoverCallback { entry -> drawTopMessage(entry.timerTaskWrappedWithKeyBind.timerTask.name.text) }
		this.addDrawableChild(listWidget)
	}

	override fun tick() {
		searchBar.tick()
	}
}