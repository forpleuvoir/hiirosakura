package forpleuvoir.hiirosakura.client.feature.timertask.gui

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.TIMER_TASK
import forpleuvoir.hiirosakura.client.util.StringUtil
import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.button.ButtonIcon
import forpleuvoir.ibuki_gourd.gui.button.ButtonKeyBind
import forpleuvoir.ibuki_gourd.gui.common.PositionParentWidget
import forpleuvoir.ibuki_gourd.gui.icon.ArrowIcon
import forpleuvoir.ibuki_gourd.gui.icon.Icon
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase
import forpleuvoir.ibuki_gourd.gui.widget.LabelText
import forpleuvoir.ibuki_gourd.gui.widget.WidgetList
import forpleuvoir.ibuki_gourd.gui.widget.WidgetListEntry
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import forpleuvoir.ibuki_gourd.utils.clamp
import forpleuvoir.ibuki_gourd.utils.fText
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.util.math.MatrixStack

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui

 * 文件名 WidgetListEntryTimerTask

 * 创建时间 2022/1/19 20:13

 * @author forpleuvoir

 */
class WidgetListEntryTimerTask(
	val timerTaskWrappedWithKeyBind: TimerTaskWrappedWithKeyBind,
	parent: WidgetList<*>,
	x: Int,
	y: Int,
	width: Int,
	height: Int
) : WidgetListEntry<WidgetListEntryTimerTask>(parent, x, y, width, height) {

	private val nameLabel: LabelText = LabelText(timerTaskWrappedWithKeyBind.timerTask.name.fText, 0, 0).apply {
		this.align = LabelText.Align.CENTER_LEFT
		this.height = this@WidgetListEntryTimerTask.height - 2
		this.addHoverText(
			"name:${timerTaskWrappedWithKeyBind.timerTask.name}".text,
			"delay:${timerTaskWrappedWithKeyBind.timerTask.delay}".text,
			"period:${timerTaskWrappedWithKeyBind.timerTask.period}".text,
			"times:${timerTaskWrappedWithKeyBind.timerTask.times}".text,
			"executor:${
				StringUtil.tooLongOmitted(
					timerTaskWrappedWithKeyBind.timerTask.asJsonElement.asJsonObject["executor"].asString,
					50
				)
			}".text
		)
	}

	private val deleteButton: Button = Button(0, 0, IbukiGourdLang.Delete.tText()) {
		TIMER_TASK.remove(timerTaskWrappedWithKeyBind)
		(parentWidget as WidgetListTimerTask).refresh()
	}

	private val editButton: Button = Button(0, 0, IbukiGourdLang.Edit.tText()) {
		ScreenBase.openScreen(
			TimerTaskEditor(
				340.coerceAtMost(parentWidget.parent.width),
				320.coerceAtMost(parentWidget.parent.height),
				parentWidget.parent,
				this.timerTaskWrappedWithKeyBind
			)
		)
	}

	private val executeButton: Button = Button(0, 0, IbukiGourdLang.Execute.tText()) {
		parentWidget.parent.onClose()
		timerTaskWrappedWithKeyBind.run()
	}

	private val keyBind: PositionParentWidget = object : PositionParentWidget(0, 0, 120, 20) {
		private val setting: ButtonIcon =
			ButtonIcon(this.x, this.y, Icon.SETTING, this.height, padding = 4, renderBord = false, renderBg = true) {
				ScreenBase.openScreen(DialogKeyBindSetting(timerTaskWrappedWithKeyBind.keyBind, 140, 60, parent.parent) {
					TIMER_TASK.onValueChanged()
					(parentWidget as WidgetListTimerTask).refresh()
				})
			}

		private val button: ButtonKeyBind =
			ButtonKeyBind(
				this.setting.x + this.setting.width + 2,
				this.y,
				this.width - this.setting.width - 2,
				this.height,
				timerTaskWrappedWithKeyBind.keyBind
			) {
				TIMER_TASK.onValueChanged()
				(parentWidget as WidgetListTimerTask).refresh()
			}

		fun initWidget() {
			addDrawableChild(setting)
			addDrawableChild(button)
		}

		override fun appendNarrations(builder: NarrationMessageBuilder?) {
			builder?.put(NarrationPart.TITLE, timerTaskWrappedWithKeyBind.timerTask.name)
		}

		override fun getType(): Selectable.SelectionType {
			return Selectable.SelectionType.HOVERED
		}
	}.apply { initWidget() }

	private val upButton: ButtonIcon = ButtonIcon(0, 0, ArrowIcon.Up, this.height / 2, padding = -4).apply {
		active = false
		visible = false
		setOnPressAction {
			TIMER_TASK.moveUp(TIMER_TASK.indexOf(this@WidgetListEntryTimerTask.timerTaskWrappedWithKeyBind))
			(parentWidget as WidgetListTimerTask).refresh()
		}
	}

	private val downButton: ButtonIcon = ButtonIcon(0, 0, ArrowIcon.Down, this.height / 2, padding = -4).apply {
		active = false
		visible = false
		setOnPressAction {
			TIMER_TASK.moveDown(TIMER_TASK.indexOf(this@WidgetListEntryTimerTask.timerTaskWrappedWithKeyBind))
			(parentWidget as WidgetListTimerTask).refresh()
		}
	}

	init {
		addDrawableChild(nameLabel)
		addDrawableChild(upButton)
		addDrawableChild(downButton)
		addDrawableChild(keyBind)
		addDrawableChild(executeButton)
		addDrawableChild(editButton)
		addDrawableChild(deleteButton)
		initPosition()
	}

	override fun updateBgOpacity(delta: Float) {
		if (ScreenBase.isCurrent(parentWidget.parent)) {
			bgOpacity += delta.toInt()
			bgOpacity = bgOpacity.clamp(if (index % 2 == 1) 12 else 24, maxBgOpacity).toInt()
		}
	}

	override fun initPosition() {
		deleteButton.x = this.x + this.width - deleteButton.width - 5
		deleteButton.y = (this.y + this.height / 2) - (deleteButton.height / 2)

		editButton.setPosition(
			deleteButton.x - 5 - editButton.width,
			(this.y + this.height / 2) - (editButton.height / 2)
		)

		executeButton.setPosition(
			editButton.x - 5 - executeButton.width,
			(this.y + this.height / 2) - (executeButton.height / 2)
		)

		keyBind.setPosition(
			executeButton.x - 5 - keyBind.width,
			(this.y + this.height / 2) - (keyBind.height / 2)
		)

		upButton.setPosition(
			keyBind.x - upButton.width - 5,
			(this.y + this.height / 2) - upButton.height
		)

		downButton.setPosition(
			keyBind.x - downButton.width - 5,
			(this.y + this.height / 2)
		)

		nameLabel.setPosition(
			this.x + 15,
			this.y + this.height / 2 - this.nameLabel.height / 2
		)
	}

	override fun renderEntry(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
		if (ScreenBase.isCurrent(parent)) {
			downButton.active = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			downButton.visible = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			upButton.active = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			upButton.visible = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
		}
	}

	override fun resize() {
		nameLabel.width =
			this.width - deleteButton.width - editButton.width - executeButton.width - keyBind.width - downButton.width - 10 - 15 - 120
	}
}