package forpleuvoir.hiirosakura.client.gui.event

import com.mojang.blaze3d.systems.RenderSystem
import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.ButtonOnOff
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import fi.dy.masa.malilib.render.RenderUtils
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase
import forpleuvoir.hiirosakura.client.util.StringUtil.translatableText
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText


/**
 * 事件组件

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.event

 * 文件名 WidgetEventEntry

 * 创建时间 2021-08-18 15:09

 * @author forpleuvoir

 */
class WidgetEventEntry(
	x: Int, y: Int, width: Int, height: Int,
	private val isOdd: Boolean,
	entry: EventSubscriberBase,
	listIndex: Int,
	val parent: WidgetListEvent
) : WidgetListEntryBase<EventSubscriberBase>(
	x, y, width, height, entry, listIndex
) {
	private val hoverLines: List<String>
	private val buttonsStartX: Int

	init {
		this.hoverLines = entry.widgetHoverLines
		val posY = y + 1
		var posX = x + width - 2

		posX -= addButton(posX, posY, Type.REMOVE)
		posX -= createButtonOnOff(posX, posY, entry.enabled, Type.ENABLED)
		posX -= this.addButton(posX, posY, Type.EDIT)

		buttonsStartX = posX
	}

	private fun addButton(x: Int, y: Int, type: Type): Int {
		val button = ButtonGeneric(
			x, y, -1, true, (if (type == Type.REMOVE) "§c" else "") + type
				.getDisplayName()
		)
		this.addButton(button, ButtonListener(type, this))
		return button.width + 1
	}

	private fun createButtonOnOff(
		xRight: Int,
		y: Int,
		isCurrentlyOn: Boolean,
		type: Type
	): Int {
		val button = ButtonOnOff(
			xRight, y, -1, true,
			type.getDisplayName() + ":" + (if (isCurrentlyOn) "§a" else "§c") + isCurrentlyOn,
			isCurrentlyOn
		)
		this.addButton(button, ButtonListener(type, this))
		return button.width + 2
	}

	override fun canSelectAt(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		return super.canSelectAt(mouseX, mouseY, mouseButton) && mouseX < buttonsStartX
	}

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, matrixStack: MatrixStack) {
		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.enableDepthTest()
		val isSelected = HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.isSelected(this.entry)
		if (isSelected || selected || isMouseOver(mouseX, mouseY)) {
			RenderUtils.drawRect(x, y, width, height, 0x2AFFFFFF)
		} else if (isOdd) {
			RenderUtils.drawRect(x, y, width, height, 0x10FFFFFF)
		} else {
			RenderUtils.drawRect(x, y, width, height, 0x20FFFFFF)
		}
		if (isSelected) {
			RenderUtils.drawOutline(x, y, width, height, 0x7FE0E0E0)
		}
		val name = "§6" + this.entry?.eventType + "§d >> §b" + this.entry?.name
		drawString(x + 4, y + 7, -0x1, name, matrixStack)
		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.disableBlend()
		super.render(mouseX, mouseY, selected, matrixStack)
		RenderUtils.disableDiffuseLighting()
	}

	override fun postRenderHovered(mouseX: Int, mouseY: Int, selected: Boolean, matrixStack: MatrixStack) {
		super.postRenderHovered(mouseX, mouseY, selected, matrixStack)
		if (mouseX in x until buttonsStartX && mouseY >= y && mouseY <= y + height) {
			RenderUtils.drawHoverText(mouseX, mouseY, hoverLines, matrixStack)
		}
	}

	inner class ButtonListener(val type: Type, val widget: WidgetEventEntry) : IButtonActionListener {
		override fun actionPerformedWithButton(button: ButtonBase?, mouseButton: Int) {
			when (type) {
				Type.EDIT -> GuiBase.openGui(EventEditScreen(widget.entry, EventScreen()))
				Type.REMOVE -> {
					HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.unsubscribe(widget.entry!!)
					widget.parent.refreshEntries()
				}
				Type.ENABLED -> {
					this.widget.entry?.toggleEnable()
					this.widget.parent.refreshEntries()
				}
			}
		}


	}

	enum class Type(private val translationKey: TranslatableText) {
		EDIT(translatableText("gui.button.edit")),
		ENABLED(translatableText("gui.button.enabled")),
		REMOVE(translatableText("gui.button.remove"));

		private fun getTranslationKey(): String {
			return translationKey.key
		}

		fun getDisplayName(vararg args: Any?): String {
			return StringUtils.translate(getTranslationKey(), *args)
		}
	}
}