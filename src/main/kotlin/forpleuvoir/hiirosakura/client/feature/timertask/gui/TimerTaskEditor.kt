package forpleuvoir.hiirosakura.client.feature.timertask.gui

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.TIMER_TASK
import forpleuvoir.hiirosakura.client.feature.timertask.TimerTask
import forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JsExecutor
import forpleuvoir.hiirosakura.client.feature.timertask.gui.TimerTaskWrapped.RunAt
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.gui.button.ButtonOption
import forpleuvoir.ibuki_gourd.gui.dialog.DialogConfirm
import forpleuvoir.ibuki_gourd.gui.widget.LabelText
import forpleuvoir.ibuki_gourd.gui.widget.MultilineTextField
import forpleuvoir.ibuki_gourd.gui.widget.WidgetIntInput
import forpleuvoir.ibuki_gourd.gui.widget.WidgetText
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.util.function.Function


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 TimerTaskEditor

 * 创建时间 2022/1/19 22:53

 * @author forpleuvoir

 */
class TimerTaskEditor(
	dialogWidth: Int,
	dialogHeight: Int,
	parent: Screen?,
	private val old: TimerTaskWrappedWithKeyBind? = null
) : DialogConfirm(dialogWidth, dialogHeight, "${HiiroSakuraClient.modId}.screen.timertask.edit".tText(), parent) {

	private val editMode: Boolean = old != null

	init {
		confirmCallback = Function { save() }
	}

	private lateinit var nameText: LabelText
	private lateinit var nameInput: WidgetText
	private var nameValue: String = if (editMode) old!!.timerTask.name else "timer_task"

	private lateinit var delayText: LabelText
	private lateinit var delayInput: WidgetIntInput
	private var delayValue: Int = if (editMode) old!!.timerTask.delay else 0

	private lateinit var periodText: LabelText
	private lateinit var periodInput: WidgetIntInput
	private var periodValue: Int = if (editMode) old!!.timerTask.period else 1

	private lateinit var timesText: LabelText
	private lateinit var timesInput: WidgetIntInput
	private var timesValue: Int = if (editMode) old!!.timerTask.times else 1

	private lateinit var runAtText: LabelText
	private lateinit var runAtButton: ButtonOption
	private var runAtValue: RunAt = if (editMode) old!!.runAt else RunAt.StartTick

	private lateinit var executorEditor: MultilineTextField
	private var executorValue = if (editMode) old!!.asString else ""

	override fun init() {
		super.init()
		initName()
		initDelay()
		initPeriod()
		initTimes()
		initRunAt()
		initExecutor()
	}

	private fun initName() {
		nameText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.name".tText(), left + 2, top).apply {
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

	private fun initDelay() {
		delayText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.delay".tText(), nameInput.x + nameInput.width + 5, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		delayInput = WidgetIntInput(delayText.x, top + delayText.height, 60, 16, delayValue, 0).apply {
			setOnValueChangedCallback {
				delayValue = it
			}
		}
		addDrawableChild(delayText)
		addDrawableChild(delayInput)
	}

	private fun initPeriod() {
		periodText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.period".tText(), delayInput.x + delayInput.width + 5, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		periodInput = WidgetIntInput(periodText.x, top + periodText.height, 60, 16, periodValue, minValue = 1).apply {
			setOnValueChangedCallback {
				periodValue = it
			}
		}
		addDrawableChild(periodText)
		addDrawableChild(periodInput)
	}

	private fun initTimes() {
		timesText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.times".tText(), periodInput.x + periodInput.width + 5, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		timesInput = WidgetIntInput(timesText.x, top + timesText.height, 60, 16, timesValue, minValue = 1).apply {
			setOnValueChangedCallback {
				timesValue = it
			}
		}
		addDrawableChild(timesText)
		addDrawableChild(timesInput)
	}

	private fun initRunAt() {
		runAtText = LabelText("${HiiroSakuraClient.modId}.screen.timertask.runAt".tText(), timesInput.x + timesInput.width + 5, top).apply {
			align = LabelText.Align.CENTER_LEFT
		}
		runAtButton = ButtonOption(
			listOf(RunAt.StartTick.name, RunAt.EndTick.name),
			current = runAtValue.name,
			runAtText.x,
			runAtText.y + runAtText.height - 2,
			60,
			20
		) {
			runAtValue = RunAt.valueOf(it)
		}
		addDrawableChild(runAtText)
		addDrawableChild(runAtButton)
	}

	private fun initExecutor() {
		executorEditor = MultilineTextField(
			left + 1,
			timesInput.y + timesInput.height + 5,
			this.contentWidth - 2,
			this.contentHeight - (timesText.height + timesInput.height) - 7,
			4
		).apply {
			text = executorValue
			setTextChangedListener {
				executorValue = it
			}
		}
		addDrawableChild(executorEditor)
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

	private fun save(): Boolean {
		var arg: Text = "".text
		var success = false
		if (nameValue.isEmpty()) {
			arg = nameText.text
		} else {
			success = true
			val new = TimerTaskWrappedWithKeyBind()
			val timerTask = TimerTask(nameValue, delayValue, periodValue, timesValue, JsExecutor(executorValue))
			val runAt = runAtValue
			new.timerTask = timerTask
			new.runAt = runAt
			if (editMode) {
				new.keyBind.copyOf(old!!.keyBind)
				TIMER_TASK.reset(old, new)
			} else {
				TIMER_TASK.add(new)
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
}