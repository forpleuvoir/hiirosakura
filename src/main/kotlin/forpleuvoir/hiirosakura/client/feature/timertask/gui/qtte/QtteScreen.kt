package forpleuvoir.hiirosakura.client.feature.timertask.gui.qtte

import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.HiiroSakuraClient.mc
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData.TIMER_TASK
import forpleuvoir.hiirosakura.client.feature.timertask.gui.TimerTaskEditor
import forpleuvoir.hiirosakura.client.util.StringUtil
import forpleuvoir.ibuki_gourd.common.mText
import forpleuvoir.ibuki_gourd.common.tText
import forpleuvoir.ibuki_gourd.gui.button.Button
import forpleuvoir.ibuki_gourd.gui.dialog.DialogBase
import forpleuvoir.ibuki_gourd.utils.fText
import forpleuvoir.ibuki_gourd.utils.text
import net.minecraft.client.gui.screen.Screen
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * 快捷定时任务执行界面

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.feature.timertask.gui.qtte

 * 文件名 QtteScreen

 * 创建时间 2022/1/21 23:59

 * @author forpleuvoir

 */
class QtteScreen(parent: Screen? = null) : DialogBase<QtteScreen>(
    360.coerceAtMost((mc.window.scaledWidth * 0.8).toInt()),
    320.coerceAtMost((mc.window.scaledHeight * 0.8).toInt()),
    "${HiiroSakuraClient.modId}.screen.timertask".tText().mText,
    parent
) {
	override fun init() {
		super.init()
		val data = TIMER_TASK.data()

		val indexX = AtomicInteger(left + 2)
		val indexY = AtomicInteger(top + 2)

		data.forEach { task ->
			val name = task.timerTask.name.fText
			val width = (this.mc.textRenderer.getWidth(name) + 12).coerceAtLeast(20)
			val x = indexX.get()
			indexX.addAndGet(width + 2)
			val y = indexY.get()
			if (left + this.contentWidth - indexX.get() <= 40 || indexX.get() + width > left + this.contentWidth - 20) {
				indexY.addAndGet(22)
				indexX.set(left + 2)
			}
			val button = Button(x, y, width, 20, name).apply {
				setOnPressAction {
					if (hasShiftDown()) {
						openScreen(
							TimerTaskEditor(
								340.coerceAtMost(HiiroSakuraClient.mc.window.scaledWidth),
								320.coerceAtMost(HiiroSakuraClient.mc.window.scaledHeight),
								this@QtteScreen,
								task
							)
						)
					} else {
                        task.run()
                        this@QtteScreen.close()
					}
				}
				setHoverText(
					listOf(
						"name:${task.timerTask.name}".text,
						"delay:${task.timerTask.delay}".text,
						"period:${task.timerTask.period}".text,
						"times:${task.timerTask.times}".text,
						"executor:${
							StringUtil.tooLongOmitted(
								task.timerTask.asJsonElement.asJsonObject["executor"].asString,
								this@QtteScreen.dialogWidth
							)
						}".text
					)
				)
			}
			this.addDrawableChild(button)
		}
	}
}
