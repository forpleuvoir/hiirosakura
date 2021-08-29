package forpleuvoir.hiirosakura.client.gui.qcms

import fi.dy.masa.malilib.gui.GuiBase
import fi.dy.masa.malilib.gui.GuiConfirmAction
import fi.dy.masa.malilib.gui.Message
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.interfaces.IConfirmationListener
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessage
import forpleuvoir.hiirosakura.client.feature.task.TimeTask.Companion.once
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler.Companion.INSTANCE
import forpleuvoir.hiirosakura.client.util.Colors
import forpleuvoir.hiirosakura.client.util.StringUtil.translatableText
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.text.TranslatableText
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer


/**
 * 快捷聊天消息发送GUI

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.gui.qcms

 * 文件名 QCMSScreen

 * 创建时间 2021-08-18 16:02

 * @author forpleuvoir

 */
@Deprecated("准备删除")
class QCMSScreen : GuiBase() {
	private val empty = TranslatableText(String.format("%s.feature.qcms.data.empty", HiiroSakuraClient.MOD_ID))
	private val buttons: LinkedList<QCMSButton> = LinkedList()

	init {
		setTitle(StringUtils.translate(translatableText("gui.title.qcms").key))
	}

	override fun initGui() {
		super.initGui()
		val data = HiiroSakuraData.QUICK_CHAT_MESSAGE_SORT.getSortedData(true)
		HiiroSakuraData.QUICK_CHAT_MESSAGE_SORT.unSortedData.forEach(Consumer { e: QuickChatMessage ->
			data.addLast(
				e
			)
		})
		addButton(
			QCMSButton(
				width / 2 - 40, height - 55, 80, 20,
				StringUtils.translate(String.format("%s.gui.button.add", HiiroSakuraClient.MOD_ID))
			)
		) { _: ButtonBase?, _: Int -> openAddScreen() }
		if (data.isEmpty()) {
			addGuiMessage(Message.MessageType.WARNING, 2000, empty.key)
			return
		}
		val padding = 10
		val indexX = AtomicInteger(40)
		val indexY = AtomicInteger(40)
		data.forEach(Consumer { qcm: QuickChatMessage ->
			val remark = qcm.remark.replace("&", "§")
			val width = (this.mc.textRenderer.getWidth(remark) + 12).coerceAtLeast(20)
			val x = indexX.get()
			indexX.addAndGet(width + 2)
			val y = indexY.get()
			if (this.width - indexX.get() <= 40 || indexX.get() + width > this.width - 20) {
				indexY.addAndGet(20)
				indexX.set(40)
			}
			val message =
				LiteralText(qcm.message).styled { style: Style ->
					style.withColor(
						Colors.DHWUIA.color
					)
				}
			val tooltip = TranslatableText(
				String.format(
					"%s.gui.qcms.hover",
					HiiroSakuraClient.MOD_ID
				)
			).styled { style: Style ->
				style.withColor(
					Colors.FORPLEUVOIR.color
				)
			}
			val qcmsButton = QCMSButton(
				x + padding, y + padding, width, 20,
				remark,
				message, tooltip
			)
			addButton(qcmsButton,
				IButtonActionListener { _: ButtonBase?, mouseButton: Int -> this.buttonClick(mouseButton, qcm) }
			)
		})
	}

	private fun sendChatMessage(message: String) {
		HiiroSakuraClient.sendMessage(message)
		INSTANCE!!.addTask(
			once(
				5,
				"#Close_QCMS_Screen"
			) {
				onClose()
			}
		)
	}

	override fun drawButtonHoverTexts(mouseX: Int, mouseY: Int, partialTicks: Float, matrixStack: MatrixStack?) {
		for (button in buttons) {
			if (button.hasHoverText() && button.isMouseOver) {
				renderTooltip(matrixStack, button.getHoverText(), mouseX + 10, mouseY)
			}
		}
	}

	override fun <T : ButtonBase> addButton(button: T, listener: IButtonActionListener): T {
		if (button is QCMSButton) {
			this.buttons.add(button)
		}
		return super.addButton(button, listener)
	}

	override fun isPauseScreen(): Boolean {
		return false
	}

	private fun buttonClick(mouseButton: Int, quickChatMessage: QuickChatMessage) {
		when (mouseButton) {
			0 -> sendChatMessage(quickChatMessage.message)
			1 -> openEditScreen(quickChatMessage)
			2 -> openDeleteScreen(quickChatMessage.remark)
		}

	}

	private fun openDeleteScreen(key: String) {
		val titleKey = String.format("%s.gui.title.qcms.delete", HiiroSakuraClient.MOD_ID)
		val messageKey = String.format("%s.gui.qcms.confirmDelete", HiiroSakuraClient.MOD_ID)
		val width = textRenderer
			.getWidth(StringUtils.translate(titleKey)).coerceAtLeast(
				textRenderer
					.getWidth(StringUtils.translate(messageKey))
			)
		val dialog = GuiConfirmAction(
			width,
			titleKey,
			object : IConfirmationListener {
				override fun onActionConfirmed(): Boolean {
					HiiroSakuraData.QUICK_CHAT_MESSAGE_SEND.remove(key)
					HiiroSakuraData.QUICK_CHAT_MESSAGE_SORT.remove(key)
					return true
				}

				override fun onActionCancelled(): Boolean {
					return false
				}

			},
			this,
			messageKey,
			key
		)
		openGui(dialog)
	}

	private fun openEditScreen(quickChatMessage: QuickChatMessage) {
		openGui(EditScreen(quickChatMessage, this))
	}

	private fun openAddScreen() {
		openGui(EditScreen(this))
	}


}