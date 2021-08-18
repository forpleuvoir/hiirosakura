package forpleuvoir.hiirosakura.client.gui.qcms


import fi.dy.masa.malilib.gui.GuiDialogBase
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import fi.dy.masa.malilib.gui.GuiTextFieldInteger
import fi.dy.masa.malilib.gui.button.ButtonBase
import fi.dy.masa.malilib.gui.button.ButtonGeneric
import fi.dy.masa.malilib.gui.button.IButtonActionListener
import fi.dy.masa.malilib.render.RenderUtils
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas
import forpleuvoir.hiirosakura.client.feature.qcms.QuickChatMessage
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui.qcms
 *
 * #class_name EditScreen
 *
 * #create_time 2021/7/17 0:59
 */
class EditScreen : GuiDialogBase {
	private val remark: String?
	private val message: String?
	private val level: Int?
	private var remarkTextField: GuiTextFieldGeneric? = null
	private var messageTextField: GuiTextFieldGeneric? = null
	private var levelTextField: GuiTextFieldInteger? = null
	private val remarkText = TranslatableText(String.format("%s.gui.qcms.key", HiiroSakuraClient.MOD_ID))
	private val messageText = TranslatableText(String.format("%s.gui.qcms.value", HiiroSakuraClient.MOD_ID))
	private val levelText = TranslatableText(String.format("%s.gui.qcms.level", HiiroSakuraClient.MOD_ID))
	private val editModel: Boolean

	constructor(quickChatMessage: QuickChatMessage, parent: Screen?) {
		this.parent = parent
		remark = quickChatMessage.remark
		message = quickChatMessage.message
		level = quickChatMessage.level
		editModel = true
	}

	constructor(parent: Screen?) {
		this.parent = parent
		remark = null
		message = null
		level = null
		editModel = false
	}

	override fun initGui() {
		super.initGui()
		setWidthAndHeight(200, 155)
		setTitle(StringUtils.translate(String.format("%s.gui.qcms.edit", HiiroSakuraClient.MOD_ID)))
		centerOnScreen()
		var x = dialogLeft + 10
		val y = dialogTop + dialogHeight - 24
		val buttonWidth = dialogWidth / 2 - 20
		createButton(x, y, buttonWidth, ButtonType.APPLY)
		x += buttonWidth + 20
		createButton(x, y, buttonWidth, ButtonType.CANCEL)
		var tY = dialogTop + 24
		createRemarkTextField(tY)
		tY += 35
		createMessageTextField(tY)
		tY += 35
		createLevelTextField(tY)
	}

	private fun createRemarkTextField(y: Int) {
		val x = dialogLeft + 10
		val width = dialogWidth - 20
		val textWidth = textRenderer.getWidth(remarkText)
		remarkTextField = GuiTextFieldGeneric(
			x + textWidth + 5, y, width - textWidth - 6, 20,
			textRenderer
		)
		if (editModel) remarkTextField!!.text = remark
		addTextField(remarkTextField, null)
	}

	private fun createMessageTextField(y: Int) {
		val x = dialogLeft + 10
		val width = dialogWidth - 20
		val textWidth = textRenderer.getWidth(messageText)
		messageTextField = GuiTextFieldGeneric(
			x + textWidth + 5, y, width - textWidth - 6, 20,
			textRenderer
		)
		if (editModel) messageTextField!!.text = message
		addTextField(messageTextField, null)
	}

	private fun createLevelTextField(y: Int) {
		val x = dialogLeft + 10
		val width = dialogWidth - 20
		val textWidth = textRenderer.getWidth(messageText)
		levelTextField = GuiTextFieldInteger(
			x + textWidth + 5, y, width - textWidth - 6, 20,
			textRenderer
		)
		if (editModel) levelTextField!!.text = level.toString()
		addTextField(levelTextField, null)
	}

	override fun drawContents(
		matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float
	) {
		if (parent != null) {
			parent!!.render(matrixStack, mouseX, mouseY, partialTicks)
		}
		matrixStack.push()
		matrixStack.translate(0.0, 0.0, zOffset.toDouble())
		RenderUtils
			.drawOutlinedBox(
				dialogLeft, dialogTop, dialogWidth, dialogHeight, -0x10000000,
				COLOR_HORIZONTAL_BAR
			)
		this.drawStringWithShadow(
			matrixStack, this.titleString, dialogLeft + 10, dialogTop + 4,
			COLOR_WHITE
		)
		this.drawStringWithShadow(
			matrixStack, StringUtils.translate(remarkText.key), dialogLeft + 10,
			remarkTextField!!.y + 5,
			COLOR_WHITE
		)
		this.drawStringWithShadow(
			matrixStack, StringUtils.translate(messageText.key), dialogLeft + 10,
			messageTextField!!.y + 5,
			COLOR_WHITE
		)
		this.drawStringWithShadow(
			matrixStack, StringUtils.translate(levelText.key), dialogLeft + 10,
			levelTextField!!.y + 5,
			COLOR_WHITE
		)
		drawTextFields(mouseX, mouseY, matrixStack)
		drawButtons(mouseX, mouseY, partialTicks, matrixStack)
		matrixStack.pop()
	}

	override fun drawTitle(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {}
	private fun createButton(x: Int, y: Int, buttonWidth: Int, type: ButtonType) {
		val button = ButtonGeneric(x, y, buttonWidth, 20, type.displayName)
		addButton(
			button,
			if (type == ButtonType.APPLY) IButtonActionListener { _: ButtonBase, mouseButton: Int ->
				this.apply(mouseButton)
			} else IButtonActionListener { _: ButtonBase, _: Int -> cancel() })
	}

	private fun apply(mouseButton: Int) {
		if (mouseButton == 0) {
			if (editModel) {
				HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND
					.reset(remark!!, remarkTextField!!.text, messageTextField!!.text)
				if (levelTextField!!.text != null) {
					var level: Int? = null
					try {
						level = levelTextField!!.text.toInt()
					} catch (ignored: Exception) {
					}
					HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.resetLevel(remark, remarkTextField!!.text, level)
				}
			} else {
				HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SEND.add(remarkTextField!!.text, messageTextField!!.text)
				if (levelTextField!!.text != null) {
					try {
						val level = levelTextField!!.text.toInt()
						HiiroSakuraDatas.QUICK_CHAT_MESSAGE_SORT.setSort(remarkTextField!!.text, level)
					} catch (ignored: Exception) {
					}
				}
			}
		}
		closeGui(true)
	}

	private fun cancel() {
		closeGui(true)
	}

	private enum class ButtonType(private val labelKey: String) {
		APPLY(
			String.format(
				"%s.gui.button.apply",
				HiiroSakuraClient.MOD_ID
			)
		),
		CANCEL(String.format("%s.gui.button.cancel", HiiroSakuraClient.MOD_ID));

		val displayName: String
			get() = (if (this == APPLY) TXT_GREEN else TXT_RED) + StringUtils
				.translate(labelKey) + TXT_RST
	}
}