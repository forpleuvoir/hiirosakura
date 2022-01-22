package forpleuvoir.hiirosakura.client.feature.event.gui

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.EVENT
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.TIMER_TASK
import forpleuvoir.hiirosakura.client.feature.common.javascript.JsRunner
import forpleuvoir.hiirosakura.client.feature.event.gui.EventSubscriber.RunnerType
import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask
import forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JsExecutor
import forpleuvoir.hiirosakura.client.feature.timertask.gui.TimerTaskWrapped
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.event.Event
import forpleuvoir.ibuki_gourd.event.events.Events
import forpleuvoir.ibuki_gourd.event.events.GameInitializedEvent
import forpleuvoir.ibuki_gourd.gui.button.ButtonOnOff
import forpleuvoir.ibuki_gourd.gui.button.ButtonOption
import forpleuvoir.ibuki_gourd.gui.dialog.DialogConfirm
import forpleuvoir.ibuki_gourd.gui.widget.*
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import forpleuvoir.ibuki_gourd.utils.clamp
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW
import java.util.function.Function

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 EventSubscriberEditor

 * 创建时间 2022/1/21 17:18

 * @author forpleuvoir

 */
class EventSubscriberEditor(
	dialogWidth: Int,
	dialogHeight: Int,
	parent: Screen?,
	private val old: EventSubscriber? = null
) : DialogConfirm(dialogWidth, dialogHeight, "${HiiroSakuraClient.modId}.screen.event.edit".tText(), parent) {

	private val editMode: Boolean = old != null

	init {
		confirmCallback = Function { save() }
	}

	private lateinit var nameText: LabelText
	private lateinit var nameInput: WidgetText
	private var nameValue: String = if (editMode) old!!.name else "event_subscriber"

	private lateinit var eventTypeText: LabelText
	private lateinit var eventTypeDropList: WidgetDropList<Class<out Event>>
	private var eventTypeValue: Class<out Event> = if (editMode) old!!.event else GameInitializedEvent::class.java

	private lateinit var enabledText: LabelText
	private lateinit var enabledButton: ButtonOnOff
	private var enabledValue: Boolean = if (editMode) old!!.enabled else true

	private lateinit var runnerTypeText: LabelText
	private lateinit var runnerTypeButton: ButtonOption
	private var runnerTypeValue: RunnerType = if (editMode) old!!.runnerType else RunnerType.JsRunner

	private lateinit var delayText: LabelText
	private lateinit var delayInput: WidgetIntInput
	private var delayValue: Int = if (editMode && old!!.runnerType == RunnerType.TimerTask)
		(old.runner as TimerTaskWrapped).timerTask.delay else 0

	private lateinit var periodText: LabelText
	private lateinit var periodInput: WidgetIntInput
	private var periodValue: Int = if (editMode && old!!.runnerType == RunnerType.TimerTask)
		(old.runner as TimerTaskWrapped).timerTask.period else 1

	private lateinit var timesText: LabelText
	private lateinit var timesInput: WidgetIntInput
	private var timesValue: Int = if (editMode && old!!.runnerType == RunnerType.TimerTask)
		(old.runner as TimerTaskWrapped).timerTask.times else 1

	private lateinit var runAtText: LabelText
	private lateinit var runAtButton: ButtonOption
	private var runAtValue: TimerTaskWrapped.RunAt = if (editMode && old!!.runnerType == RunnerType.TimerTask)
		(old.runner as TimerTaskWrapped).runAt else TimerTaskWrapped.RunAt.StartTick

	private lateinit var fromTimerTaskText: LabelText
	private lateinit var fromTimerTaskDropList: WidgetDropList<String>

	private lateinit var executorEditor: MultilineTextField
	private var executorValue = if (editMode) old!!.runner.asString else ""

	override fun init() {
		super.init()
		initName()
		initEnabled()
		initRunnerType()
		initDelay()
		initPeriod()
		initTimes()
		initRunAt()
		initExecutor()
		initEventType()
		initFromTimerTask()
	}

	private fun initName() {
		nameText = LabelText("${HiiroSakuraClient.modId}.screen.event.name".tText(), left + 2, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		nameInput = WidgetText(nameText.x, top + nameText.height, 60, 16).apply {
			text = nameValue
			setChangedListener {
				nameValue = it
			}
		}
		addDrawableChild(nameText)
		addDrawableChild(nameInput)
	}

	private fun initEventType() {
		eventTypeText = LabelText("${HiiroSakuraClient.modId}.screen.event.type".tText(), nameInput.x + nameInput.width + 5, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		eventTypeDropList = WidgetDropList(
			items = Events.getEvents(),
			default = eventTypeValue,
			stringAdapter = { e -> e.simpleName },
			entryAdapter = { s -> Events.getEventByName(s)!! },
			hoverTextAdapter = { s -> listOf(Events.getDescription(s)!!) },
			parent = this,
			pageSize = 10,
			itemHeight = 16,
			x = eventTypeText.x,
			y = eventTypeText.y + eventTypeText.height,
			width = 120
		).apply {
			zOffset = 20.0
			toggleCallback = {
				eventTypeValue = it
			}
		}
		addDrawableChild(eventTypeText)
		addDrawableChild(eventTypeDropList)
	}

	private fun initEnabled() {
		enabledText =
			LabelText("${HiiroSakuraClient.modId}.screen.event.enabled".tText(), nameInput.x + nameInput.width + 5 + 120 + 5, top).apply {
				align = LabelText.Align.CENTER_LEFT
			}
		enabledButton = ButtonOnOff(enabledText.x, enabledText.y + enabledText.height - 2, 60, status = enabledValue).apply {
			setOnOffText(
				IbukiGourdLang.Enabled.tText().styled { it.withColor(Formatting.GREEN) },
				IbukiGourdLang.Disabled.tText().styled { it.withColor(Formatting.RED) }
			)
			setCallback { _, status ->
				enabledValue = status
			}
		}
		this.addDrawableChild(enabledText)
		this.addDrawableChild(enabledButton)
	}

	private fun initRunnerType() {
		runnerTypeText =
			LabelText("${HiiroSakuraClient.modId}.screen.event.runnerType".tText(), enabledButton.x + enabledButton.width + 5, top).apply {
				align = LabelText.Align.CENTER_LEFT
			}
		val list = ArrayList<String>().apply {
			RunnerType.values().forEach { add(it.name) }
		}
		runnerTypeButton = ButtonOption(
			list,
			runnerTypeValue.name,
			runnerTypeText.x,
			runnerTypeText.y + runnerTypeText.height - 2,
			60
		) {
			runnerTypeValue = RunnerType.valueOf(it)
			changeRunner(runnerTypeValue)
		}
		addDrawableChild(runnerTypeText)
		addDrawableChild(runnerTypeButton)
	}

	private fun initDelay() {
		delayText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.delay".tText(), left + 2, nameInput.y + nameInput.height + 5).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		delayInput = WidgetIntInput(delayText.x, delayText.y + delayText.height, 60, 16, delayValue, 0).apply {
			setOnValueChangedCallback {
				delayValue = it
			}
		}
		val visible = runnerTypeValue == RunnerType.TimerTask
		delayText.visible = visible
		delayInput.visible = visible
		delayText.active = visible
		delayInput.active = visible
		addDrawableChild(delayText)
		addDrawableChild(delayInput)
	}

	private fun initPeriod() {
		periodText =
			LabelText("${HiiroSakuraClient.modId}.screen.timertask.period".tText(), delayInput.x + delayInput.width + 5, delayText.y).apply {
				align = LabelText.Align.CENTER_LEFT
			}
		periodInput = WidgetIntInput(periodText.x, periodText.y + periodText.height, 60, 16, periodValue, minValue = 1).apply {
			setOnValueChangedCallback {
				periodValue = it
			}
		}
		val visible = runnerTypeValue == RunnerType.TimerTask
		periodText.visible = visible
		periodInput.visible = visible
		periodText.active = visible
		periodInput.active = visible
		addDrawableChild(periodText)
		addDrawableChild(periodInput)
	}

	private fun initTimes() {
		timesText =
			LabelText("${HiiroSakuraClient.modId}.screen.timertask.times".tText(), periodInput.x + periodInput.width + 5, periodText.y).apply {
				align = LabelText.Align.CENTER_LEFT
			}
		timesInput = WidgetIntInput(timesText.x, timesText.y + timesText.height, 60, 16, timesValue, minValue = 1).apply {
			setOnValueChangedCallback {
				timesValue = it
			}
		}
		val visible = runnerTypeValue == RunnerType.TimerTask
		timesText.visible = visible
		timesInput.visible = visible
		timesText.active = visible
		timesInput.active = visible
		addDrawableChild(timesText)
		addDrawableChild(timesInput)
	}

	private fun initRunAt() {
		runAtText =
			LabelText("${HiiroSakuraClient.modId}.screen.timertask.runAt".tText(), timesInput.x + timesInput.width + 5, timesText.y).apply {
				align = LabelText.Align.CENTER_LEFT
			}
		runAtButton = ButtonOption(
			listOf(TimerTaskWrapped.RunAt.StartTick.name, TimerTaskWrapped.RunAt.EndTick.name),
			current = runAtValue.name,
			runAtText.x,
			runAtText.y + runAtText.height - 2,
			60,
			20
		) {
			runAtValue = TimerTaskWrapped.RunAt.valueOf(it)
		}
		val visible = runnerTypeValue == RunnerType.TimerTask
		runAtText.visible = visible
		runAtButton.visible = visible
		runAtText.active = visible
		runAtButton.active = visible
		addDrawableChild(runAtText)
		addDrawableChild(runAtButton)
	}

	private fun initFromTimerTask() {
		val visible = runnerTypeValue == RunnerType.TimerTask
		fromTimerTaskText =
			LabelText(
				"${HiiroSakuraClient.modId}.screen.event.fromTimerTask".tText(),
				runAtButton.x + runAtButton.width + 5,
				timesText.y
			).apply {
				this.visible = visible
				active = visible
				align = LabelText.Align.CENTER_LEFT
			}
		val list = ArrayList<String>().apply {
			TIMER_TASK.data().forEach { add(it.timerTask.name) }
		}
		fromTimerTaskDropList = WidgetDropList(
			items = list,
			default = if (list.isNotEmpty()) list[0] else "-",
			stringAdapter = { it },
			entryAdapter = { it },
			parent = this,
			pageSize = list.size.clamp(1, 10).toInt(),
			itemHeight = 16,
			x = fromTimerTaskText.x,
			y = fromTimerTaskText.y + fromTimerTaskText.height,
			width = 120
		).apply {
			this.visible = visible
			active = visible
			toggleCallback = { str ->
				TIMER_TASK.data().find { it.timerTask.name == str }?.let {
					nameValue = it.timerTask.name
					nameInput.text = nameValue

					delayValue = it.timerTask.delay
					delayInput.value = delayValue

					periodValue = it.timerTask.period
					periodInput.value = periodValue

					timesValue = it.timerTask.times
					timesInput.value = timesValue

					executorValue = it.asString
					executorEditor.text = executorValue

				}
			}
		}
		addDrawableChild(fromTimerTaskText)
		addDrawableChild(fromTimerTaskDropList)
	}

	private fun initExecutor() {
		val height = when (runnerTypeValue) {
			RunnerType.JsRunner  -> {
				this.contentHeight - (nameText.height + nameInput.height) - 7
			}
			RunnerType.TimerTask -> {
				this.contentHeight - (nameText.height + nameInput.height + timesText.height + timesInput.height) - 12
			}
		}
		val y = when (runnerTypeValue) {
			RunnerType.JsRunner  -> {
				nameInput.y + nameInput.height + 5
			}
			RunnerType.TimerTask -> {
				timesInput.y + timesInput.height + 5
			}
		}
		executorEditor = MultilineTextField(
			left + 1,
			y,
			this.contentWidth - 2,
			height,
			4
		).apply {
			text = executorValue
			setTextChangedListener {
				executorValue = it
			}
		}
		addDrawableChild(executorEditor)
	}


	private fun changeRunner(runnerTypeValue: RunnerType) {
		when (runnerTypeValue) {
			RunnerType.JsRunner  -> {
				delayText.visible = false
				delayInput.visible = false
				delayText.active = false
				delayInput.active = false

				periodText.visible = false
				periodInput.visible = false
				periodText.active = false
				periodInput.active = false

				timesText.visible = false
				timesInput.visible = false
				timesText.active = false
				timesInput.active = false

				runAtText.visible = false
				runAtButton.visible = false
				runAtText.active = false
				runAtButton.active = false

				fromTimerTaskText.visible = false
				fromTimerTaskDropList.visible = false
				fromTimerTaskText.active = false
				fromTimerTaskDropList.active = false
			}
			RunnerType.TimerTask -> {
				delayText.visible = true
				delayInput.visible = true
				delayText.active = true
				delayInput.active = true

				periodText.visible = true
				periodInput.visible = true
				periodText.active = true
				periodInput.active = true

				timesText.visible = true
				timesInput.visible = true
				timesText.active = true
				timesInput.active = true

				runAtText.visible = true
				runAtButton.visible = true
				runAtText.active = true
				runAtButton.active = true

				fromTimerTaskText.visible = true
				fromTimerTaskDropList.visible = true
				fromTimerTaskText.active = true
				fromTimerTaskDropList.active = true
			}
		}
		remove(executorEditor)
		remove(eventTypeDropList)
		remove(eventTypeText)
		remove(fromTimerTaskText)
		remove(fromTimerTaskDropList)
		initExecutor()
		initEventType()
		initFromTimerTask()
	}

	private fun save(): Boolean {
		var arg: Text = "".text
		var success = false
		if (nameValue.isEmpty()) {
			arg = nameText.text
		} else {
			success = true
			val new = EventSubscriber()
			val timerTask = TimerTask(nameValue, delayValue, periodValue, timesValue, JsExecutor(executorValue))
			val runAt = runAtValue
			new.name = nameValue
			new.event = eventTypeValue
			new.enabled = enabledValue
			new.runnerType = runnerTypeValue
			new.runner = when (new.runnerType) {
				RunnerType.JsRunner  -> {
					JsRunner(executorValue)
				}
				RunnerType.TimerTask -> {
					TimerTaskWrapped().apply {
						this.timerTask = timerTask
						this.runAt = runAt
					}
				}
			}
			if (editMode) {
				EVENT.reset(old!!, new)
			} else {
				EVENT.add(new)
			}
		}
		if (!success)
			SystemToast.show(
				mc.toastManager,
				SystemToast.Type.PACK_COPY_FAILURE,
				"${HiiroSakuraClient.modId}.screen.null_value".tText(arg),
				null
			)
		return success
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
		if (this::eventTypeDropList.isInitialized && eventTypeDropList.expand) {
			if (eventTypeDropList.isMouseOver(mouseX, mouseY)) {
				if (eventTypeDropList.mouseScrolled(mouseX, mouseY, amount)) return true
			}
		}
		if (this::fromTimerTaskDropList.isInitialized && fromTimerTaskDropList.expand) {
			if (fromTimerTaskDropList.isMouseOver(mouseX, mouseY)) {
				if (fromTimerTaskDropList.mouseScrolled(mouseX, mouseY, amount)) return true
			}
		}
		return super.mouseScrolled(mouseX, mouseY, amount)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (this::eventTypeDropList.isInitialized && eventTypeDropList.expand) {
			if (eventTypeDropList.isMouseOver(mouseX, mouseY)) {
				if (eventTypeDropList.mouseClicked(mouseX, mouseY, button)) return true
			}
		}
		if (this::fromTimerTaskDropList.isInitialized && fromTimerTaskDropList.expand) {
			if (fromTimerTaskDropList.isMouseOver(mouseX, mouseY)) {
				if (fromTimerTaskDropList.mouseClicked(mouseX, mouseY, button)) return true
			}
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (this::executorEditor.isInitialized)
			if (keyCode == GLFW.GLFW_KEY_TAB) {
				if (executorEditor.isFocused) {
					executorEditor.keyPressed(keyCode, scanCode, modifiers)
					return true
				}
			}
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

	override fun tick() {
		if (this::executorEditor.isInitialized)
			executorEditor.tick()
		if (this::nameInput.isInitialized)
			nameInput.tick()
	}

}