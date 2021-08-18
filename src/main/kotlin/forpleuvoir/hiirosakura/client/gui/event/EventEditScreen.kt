package forpleuvoir.hiirosakura.client.gui.event

import com.google.common.collect.ImmutableList
import com.google.gson.JsonObject
import fi.dy.masa.malilib.gui.*
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import forpleuvoir.hiirosakura.client.feature.event.OnGameJoinEvent
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents
import forpleuvoir.hiirosakura.client.feature.task.TimeTask
import forpleuvoir.hiirosakura.client.util.JsonUtil
import forpleuvoir.hiirosakura.client.util.StringUtil.isEmptyString
import forpleuvoir.hiirosakura.client.util.StringUtil.translatableText
import net.minecraft.client.gui.screen.Screen

/**
 * 事件编辑界面
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui.event
 *
 * #class_name EventEditScreen
 *
 * #create_time 2021/7/29 1:18
 */
class EventEditScreen : GuiBase {
	private var subscriber: EventSubscriberBase? = null
	private val editMode: Boolean
	private val nullValeText = translatableText("gui.event.null_value")
	private val saveButtonText = translatableText("gui.button.apply")
	private val eventTypeText = translatableText("gui.event.type")
	private val nameText = translatableText("gui.event.name")
	private val startTimeText = translatableText("gui.event.start_time")
	private val cyclesText = translatableText("gui.event.cycles")
	private val cyclesTimeText = translatableText("gui.event.cycles_time")
	private val scriptText = translatableText("gui.event.script")
	private var eventListDropDown: WidgetDropDownList<String>? = null
	private var nameInput: GuiTextFieldGeneric? = null
	private var startTimeInput: GuiTextFieldInteger? = null
	private var cyclesInput: GuiTextFieldInteger? = null
	private var cyclesTimeInput: GuiTextFieldInteger? = null
	private var scriptInput: JsTextField? = null
	private var startTime = 0
	private var cycles = 1
	private var cyclesTime = 0
	private var name = ""
	private var script = ""

	constructor(subscriber: EventSubscriberBase?, parentScreen: Screen?) {
		this.subscriber = subscriber
		if (this.subscriber != null) {
			val data = subscriber!!.getTimeTask().data
			startTime = data.startTime
			cycles = data.cycles
			cyclesTime = data.cyclesTime
			name = data.name
			script = subscriber.script
		}
		editMode = true
		parent = parentScreen
		title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID))
	}

	constructor(parentScreen: Screen?) {
		editMode = false
		parent = EventScreen()
		parent = parentScreen
		title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID))
	}

	override fun initGui() {
		super.initGui()
		var x = 10
		initSaveButton(x)
		x = initEventListDropDown(x)
		initScriptEditor(x)
		initEditor(x)
	}

	override fun isPauseScreen(): Boolean {
		return false
	}

	override fun onClose() {
		openGui(parent)
	}

	private fun checkSave(): Boolean {
		var arg = ""
		if (nameInput!!.text.isEmptyString()) {
			arg = nameText.key
		} else if (startTimeInput!!.text.isEmptyString()) {
			arg = startTimeText.key
		} else if (cyclesInput!!.text.isEmptyString()) {
			arg = cyclesText.key
		} else if (cyclesTimeInput!!.text.isEmptyString()) {
			arg = cyclesTimeText.key
		}
		return if (!arg.isEmptyString()) {
			addGuiMessage(Message.MessageType.WARNING, 2000, nullValeText.key, arg)
			false
		} else {
			true
		}
	}

	private fun save() {
		if (!checkSave()) return
		val eventType = eventListDropDown!!.selectedEntry
		val timeTask = JsonObject()
		val enabled = subscriber == null || subscriber!!.enabled
		timeTask.addProperty("name", name)
		timeTask.addProperty("enabled", enabled)
		val data: MutableMap<String, Any> = HashMap()
		data["startTime"] = startTime
		data["cycles"] = cycles
		data["cyclesTime"] = cyclesTime
		data["script"] = script
		data["name"] = name
		timeTask.addProperty("timeTask", JsonUtil.toJsonStr(data))
		val subscriber = EventSubscriberBase(eventType!!, timeTask)
		if (editMode) {
			HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.update(this.subscriber!!, subscriber)
		} else {
			HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.subscribe(subscriber)
		}
		closeGui(true)
	}

	private fun initSaveButton(x: Int) {
		val saveButton = ButtonGeneric(x + 12, height - 24, 128, false, saveButtonText.key)
		saveButton.setPosition(saveButton.x, height - 22 - saveButton.height)
		addButton(saveButton) { _: ButtonBase?, _: Int -> save() }
	}

	private fun initEventListDropDown(x: Int, y: Int = 24): Int {
		var funcY = y
		this.addLabel(x + 12, funcY, -1, 12, -0x1, "§b" + StringUtils.translate(eventTypeText.key))
		funcY += 4
		eventListDropDown = WidgetDropDownList(
			x, funcY, 128, 15, 200, 10,
			ImmutableList.copyOf(
				HiiroSakuraEvents.events.keys
			)
		) { type: String -> "§6§l§n$type" }
		eventListDropDown!!.setPosition(x + 12, funcY + eventListDropDown!!.height / 2)
		addWidget(eventListDropDown)
		if (subscriber != null) {
			eventListDropDown!!.setSelectedEntry(subscriber!!.eventType)
		} else {
			eventListDropDown!!.setSelectedEntry(HiiroSakuraEvents.getEventType(OnGameJoinEvent::class.java))
		}
		return x + eventListDropDown!!.width + 4
	}

	private fun initEditor(x: Int, y: Int = 24): Int {
		var funcX = x
		var timeTask: TimeTask? = null
		if (subscriber != null) {
			timeTask = subscriber!!.getTimeTask()
		}
		funcX += createStringEditorFiled(
			funcX,
			y,
			nameText.key,
			{
				nameInput = it
				name = nameInput!!.text
			},
			{ value: String -> name = value },
			timeTask?.name ?: ""
		)
		funcX += createIntEditorFiled(
			funcX,
			y,
			startTimeText.key,
			{
				startTimeInput = it
				startTime = startTimeInput!!.text.toInt()
			},
			{ value: Int -> startTime = value },
			timeTask?.data?.startTime ?: 0,
			0,
		)
		funcX += createIntEditorFiled(
			funcX,
			y,
			cyclesText.key,
			{
				cyclesInput = it
				cycles = cyclesInput!!.text.toInt()
			},
			{ value: Int -> cycles = value },
			timeTask?.data?.cycles ?: 1,
			1,
		)
		funcX += createIntEditorFiled(
			funcX,
			y,
			cyclesTimeText.key,
			{
				cyclesTimeInput = it
				cyclesTime = cyclesTimeInput!!.text.toInt()
			},
			{ value: Int -> cyclesTime = value },
			timeTask?.data?.cyclesTime ?: 0,
			0,
		)
		return funcX
	}

	private fun initScriptEditor(x: Int, y: Int = 52) {
		var funcY = y
		this.addLabel(x + 12, funcY, -1, 12, -0x1, StringUtils.translate(scriptText.key))
		funcY += 11
		scriptInput = JsTextField(textRenderer, x + 12, funcY, 261, height - funcY - 24, 10, true)
		scriptInput!!.text = script
		scriptInput!!.setTextChangedListener { text: String -> script = text }
		addTextField(scriptInput, null)
	}

	override fun onMouseScrolled(mouseX: Int, mouseY: Int, mouseWheelDelta: Double): Boolean {
		return if (scriptInput!!.isFocused && scriptInput!!.mouseScrolled(
				mouseX.toDouble(),
				mouseY.toDouble(),
				mouseWheelDelta
			)
		) {
			true
		} else super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta)
	}

	private inline fun createStringEditorFiled(
		x: Int,
		y: Int,
		translationKey: String,
		inputConsumer: (GuiTextFieldGeneric) -> Unit,
		noinline changedListener: (String) -> Unit,
		value: String
	): Int {
		var funcY = y
		this.addLabel(x + 12, funcY, -1, 12, -0x1, StringUtils.translate(translationKey))
		funcY += 11
		val inputFiled = GuiTextFieldGeneric(x + 12, funcY, 80, 14, textRenderer)
		inputFiled.text = value
		addTextField(inputFiled, null)
		inputFiled.setChangedListener(changedListener)
		inputConsumer.invoke(inputFiled)
		return inputFiled.width + 4
	}

	private inline fun createIntEditorFiled(
		x: Int,
		y: Int,
		translationKey: String,
		inputConsumer: (GuiTextFieldInteger) -> Unit,
		noinline changedListener: (Int) -> Unit,
		value: Int,
		min: Int,
		max: Int = Int.MAX_VALUE
	): Int {
		var funcY = y
		this.addLabel(x + 12, funcY, -1, 12, -0x1, StringUtils.translate(translationKey))
		funcY += 11
		val inputFiled = GuiTextFieldInteger(x + 12, funcY, 40, 14, textRenderer)
		inputFiled.text = value.toString()
		inputFiled.setEditable(false)
		addTextField(inputFiled, null)
		val hover = StringUtils.translate("malilib.gui.button.hover.plus_minus_tip")
		val button = ButtonGeneric(x + 54, funcY - 1, MaLiLibIcons.BTN_PLUSMINUS_16, hover)
		addButton(button, ButtonListenerIntegerModifier(inputFiled, min, max))
		inputFiled.setChangedListener { changedListener.invoke(it.toInt()) }
		inputConsumer.invoke(inputFiled)
		return button.width + inputFiled.width + 4
	}

	open class ButtonListenerIntegerModifier(
		private val consumer: GuiTextFieldInteger,
		private val minValue: Int,
		private val maxValue: Int
	) : IButtonActionListener {
		private val modifierShift = 10
		private val modifierAlt = 5
		override fun actionPerformedWithButton(button: ButtonBase, mouseButton: Int) {
			var amount = if (mouseButton == 1) -1 else 1
			if (isShiftDown()) {
				amount *= modifierShift
			}
			if (isAltDown()) {
				amount *= modifierAlt
			}
			var value = consumer.text.toInt() + amount
			value = value.coerceAtLeast(minValue)
			value = value.coerceAtMost(maxValue)
			consumer.text = value.toString()
		}
	}
}