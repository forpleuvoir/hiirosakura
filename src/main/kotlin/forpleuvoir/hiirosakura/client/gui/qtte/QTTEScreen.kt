package forpleuvoir.hiirosakura.client.gui.qtte

import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.Message
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.task.TimeTask
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskBase
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler
import forpleuvoir.hiirosakura.client.gui.task.TimeTaskEditScreen
import forpleuvoir.hiirosakura.client.util.StringUtil
import net.minecraft.text.TranslatableText
import java.util.concurrent.atomic.AtomicInteger


/**
 * 快捷任务执行

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.qtte

 * 文件名 QTTEScreen

 * 创建时间 2021/8/26 0:31

 * @author forpleuvoir

 */
class QTTEScreen : GuiBase() {
	private val empty = TranslatableText(String.format("%s.feature.qtte.data.empty", HiiroSakuraClient.MOD_ID))

	init {
		setTitle(StringUtils.translate(StringUtil.translatableText("gui.title.qtte").key))
	}

	override fun initGui() {
		super.initGui()
		val data = HiiroSakuraData.HIIRO_SAKURA_TIME_TASK.sortList()
		if (data.isEmpty()) {
			addGuiMessage(Message.MessageType.WARNING, 2000, empty.key)
			return
		}
		val indexX = AtomicInteger(40)
		val indexY = AtomicInteger(40)
		data.forEach {
			val name = it.name.replace("&", "§")
			val width = (this.mc.textRenderer.getWidth(name) + 12).coerceAtLeast(20)
			val x = indexX.get()
			indexX.addAndGet(width + 2)
			val y = indexY.get()
			if (this.width - indexX.get() <= 40 || indexX.get() + width > this.width - 20) {
				indexY.addAndGet(20)
				indexX.set(40)
			}
			val button = ButtonGeneric(x, y, width, 20, name)
			button.hoverStrings = it.widgetHoverLines
			this.addButton(button) { _: ButtonBase?, mouseButton: Int ->
				if (mouseButton == 0) {
					if (isShiftDown()) {
						openGui(TimeTaskEditScreen(it, this))
					} else {
						buttonClick(it)
					}
				}
			}
		}
	}

	override fun isPauseScreen(): Boolean {
		return false
	}

	private fun buttonClick(timeTaskBase: TimeTaskBase) {
		TimeTaskHandler.INSTANCE!!.addTask(timeTaskBase.timeTask)
		TimeTaskHandler.INSTANCE!!.addTask(
			TimeTask.once(
				5,
				"#Close_QTTE_Screen"
			) {
				onClose()
			}
		)
	}

}