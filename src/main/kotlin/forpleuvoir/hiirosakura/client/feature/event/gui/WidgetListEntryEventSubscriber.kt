package forpleuvoir.hiirosakura.client.feature.event.gui

import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.EVENT
import forpleuvoir.hiirosakura.client.feature.timertask.gui.WidgetListTimerTask
import forpleuvoir.hiirosakura.client.util.StringUtil
import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.button.ButtonIcon
import forpleuvoir.ibuki_gourd.gui.button.ButtonOnOff
import forpleuvoir.ibuki_gourd.gui.icon.ArrowIcon
import forpleuvoir.ibuki_gourd.gui.screen.ScreenBase
import forpleuvoir.ibuki_gourd.gui.widget.LabelText
import forpleuvoir.ibuki_gourd.gui.widget.WidgetList
import forpleuvoir.ibuki_gourd.gui.widget.WidgetListEntry
import forpleuvoir.ibuki_gourd.mod.utils.IbukiGourdLang
import forpleuvoir.ibuki_gourd.utils.clamp
import forpleuvoir.ibuki_gourd.utils.fText
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Formatting

/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.event.gui

 * 文件名 WidgetListEntryEventSubscriber

 * 创建时间 2022/1/21 16:01

 * @author forpleuvoir

 */
class WidgetListEntryEventSubscriber(
	val eventSubscriber: EventSubscriber,
	parentWidget: WidgetList<*>,
	x: Int,
	y: Int,
	width: Int,
	height: Int
) : WidgetListEntry<WidgetListEntryEventSubscriber>(parentWidget, x, y, width, height) {

	val nameLabel: LabelText = LabelText(("&6${eventSubscriber.event.simpleName} &b=>&r ${eventSubscriber.name}").fText, 0, 0).apply {
		this.align = LabelText.Align.CENTER_LEFT
		this.height = this@WidgetListEntryEventSubscriber.height - 2
		this.addHoverText(
			"name:${eventSubscriber.name}".text,
			"event:${eventSubscriber.event.simpleName}".text,
			"enabled:${eventSubscriber.enabled}".text,
			"runnerType:${eventSubscriber.runnerType.name}".text,
			"runner:${StringUtil.tooLongOmitted(eventSubscriber.asString, 50)}".text
		)
	}

	private val deleteButton: Button = Button(0, 0, IbukiGourdLang.Delete.tText()) {
		EVENT.remove(eventSubscriber)
		(parentWidget as WidgetListEventSubscriber).refresh()
	}

	private val editButton: Button = Button(0, 0, IbukiGourdLang.Edit.tText()) {
		ScreenBase.openScreen(
			EventSubscriberEditor(
				400.coerceAtMost(this.parentWidget.parent.width),
				320.coerceAtMost(this.parentWidget.parent.height),
				parentWidget.parent,
				this.eventSubscriber
			)
		)
	}


	private val enabledButton: ButtonOnOff = ButtonOnOff(0, 0, 60, 20, eventSubscriber.enabled).apply {
		setOnOffText(
			IbukiGourdLang.Enabled.tText().styled { it.withColor(Formatting.GREEN) },
			IbukiGourdLang.Disabled.tText().styled { it.withColor(Formatting.RED) }
		)
		setCallback { _, status ->
			eventSubscriber.enabled = status
			EVENT.onValueChanged()
			(parentWidget as WidgetListEventSubscriber).refresh()
		}
	}

	private val upButton: ButtonIcon = ButtonIcon(0, 0, ArrowIcon.Up, this.height / 2, padding = -4).apply {
		active = false
		visible = false
		setOnPressAction {
			EVENT.moveUp(EVENT.indexOf(this@WidgetListEntryEventSubscriber.eventSubscriber))
			(parentWidget as WidgetListTimerTask).refresh()
		}
	}

	private val downButton: ButtonIcon = ButtonIcon(0, 0, ArrowIcon.Down, this.height / 2, padding = -4).apply {
		active = false
		visible = false
		setOnPressAction {
			EVENT.moveDown(EVENT.indexOf(this@WidgetListEntryEventSubscriber.eventSubscriber))
			(parentWidget as WidgetListTimerTask).refresh()
		}
	}

	init {
		addDrawableChild(nameLabel)
		addDrawableChild(upButton)
		addDrawableChild(downButton)
		addDrawableChild(enabledButton)
		addDrawableChild(editButton)
		addDrawableChild(deleteButton)
		initPosition()
	}

	override fun initPosition() {
		deleteButton.setPosition(
			this.x + this.width - deleteButton.width - 5,
			(this.y + this.height / 2) - (deleteButton.height / 2)
		)

		editButton.setPosition(
			deleteButton.x - 5 - editButton.width,
			(this.y + this.height / 2) - (editButton.height / 2)
		)

		enabledButton.setPosition(
			editButton.x - 5 - enabledButton.width,
			(this.y + this.height / 2) - (enabledButton.height / 2)
		)

		upButton.setPosition(
			enabledButton.x - upButton.width - 5,
			(this.y + this.height / 2) - upButton.height
		)

		downButton.setPosition(
			enabledButton.x - downButton.width - 5,
			(this.y + this.height / 2)
		)

		nameLabel.setPosition(
			this.x + 15,
			this.y + this.height / 2 - this.nameLabel.height / 2
		)
	}

	override fun updateBgOpacity(delta: Float) {
		if (ScreenBase.isCurrent(parentWidget.parent)) {
			bgOpacity += delta.toInt()
			bgOpacity = bgOpacity.clamp(if (index % 2 == 1) 12 else 24, maxBgOpacity).toInt()
		}
	}

    override fun renderEntry(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
		if (ScreenBase.isCurrent(parent)) {
			downButton.active = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			downButton.visible = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			upButton.active = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
			upButton.visible = isMouseOver(mouseX.toDouble(), mouseY.toDouble())
		}
	}

	override fun resize() {
		nameLabel.width =
			this.width - deleteButton.width - editButton.width - enabledButton.width - downButton.width - 10 - 15 - 120
	}
}