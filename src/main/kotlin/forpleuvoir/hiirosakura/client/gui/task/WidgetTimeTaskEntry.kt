package forpleuvoir.hiirosakura.client.gui.task

import com.mojang.blaze3d.systems.RenderSystem
import fi.dy.masa.malilib.gui.GuiListBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase
import fi.dy.masa.malilib.render.RenderUtils
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskBase
import forpleuvoir.hiirosakura.client.gui.event.WidgetEventEntry
import net.minecraft.client.util.math.MatrixStack


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.task

 * 文件名 WidgetTimeTaskEntry

 * 创建时间 2021/8/24 22:44

 * @author forpleuvoir

 */
class WidgetTimeTaskEntry(
	private val isOdd: Boolean,
	private val parent: WidgetListTimeTask,
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	entry: TimeTaskBase,
	listIndex: Int
) : WidgetListEntryBase<TimeTaskBase>(x, y, width, height, entry, listIndex) {
	private val hoverLines = entry.widgetHoverLines
	private val buttonStartX: Int

	init {
		val posY = y + 1
		var posX = x + width - 2

		posX -= this.addButton(posX, posY, "§c${WidgetEventEntry.Type.REMOVE.getDisplayName()}") {
			HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.remove(entry.name)
			parent.refreshEntries()
		}

		posX -= this.addButton(posX, posY, WidgetEventEntry.Type.EDIT.getDisplayName()) {
			GuiListBase.openGui(TimeTaskEditScreen(this.entry, TaskScreen()))
		}

		buttonStartX = posX
	}

	private fun addButton(x: Int, y: Int, translationKey: String, listener: () -> Unit): Int {
		val button = ButtonGeneric(x, y, -1, true, translationKey)
		this.addButton(button) { _, _ ->
			listener.invoke()
		}
		return button.width + 1
	}

	override fun canSelectAt(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
		return super.canSelectAt(mouseX, mouseY, mouseButton) && mouseX < buttonStartX
	}

	override fun render(mouseX: Int, mouseY: Int, selected: Boolean, matrixStack: MatrixStack) {
		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.enableDepthTest()
		val isSelected = HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.isSelected(this.entry)
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
		val name = "§6" + this.entry?.sort + "§b >> §r" + this.entry?.name?.replace("&", "§")
		drawString(x + 4, y + 7, -0x1, name, matrixStack)
		RenderUtils.color(1f, 1f, 1f, 1f)
		RenderSystem.disableBlend()
		super.render(mouseX, mouseY, selected, matrixStack)
		RenderUtils.disableDiffuseLighting()
	}

	override fun postRenderHovered(mouseX: Int, mouseY: Int, selected: Boolean, matrixStack: MatrixStack) {
		super.postRenderHovered(mouseX, mouseY, selected, matrixStack)
		if (mouseX in x until buttonStartX && mouseY >= y && mouseY <= y + height) {
			RenderUtils.drawHoverText(mouseX, mouseY, hoverLines, matrixStack)
		}
	}

}