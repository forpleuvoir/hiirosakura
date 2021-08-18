package forpleuvoir.hiirosakura.client.gui.event

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric
import fi.dy.masa.malilib.render.RenderUtils
import forpleuvoir.hiirosakura.client.util.Colors
import forpleuvoir.hiirosakura.client.util.StringUtil.filter
import forpleuvoir.hiirosakura.client.util.StringUtil.insertStringAt
import forpleuvoir.hiirosakura.client.util.StringUtil.wrapToWidth
import forpleuvoir.hiirosakura.client.util.StringUtil.wrapToWidthWithIndication
import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.Selectable.SelectionType
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Language
import net.minecraft.util.math.MathHelper
import java.util.*
import java.util.function.Consumer

/**
 * @author forpleuvoir
 *
 *
 * #project_name hiirosakura
 *
 *
 * #package forpleuvoir.hiirosakura.client.gui.event
 *
 *
 * #class_name JsTextField
 *
 *
 * #create_time 2021-08-11 17:47
 */
class JsTextField(
	private val fontRenderer: TextRenderer,
	private var xPosition: Int,
	private var yPosition: Int,
	width: Int,
	height: Int,
	margin: Int,
	multiline: Boolean
) : GuiTextFieldGeneric(
	xPosition, yPosition, width, height, fontRenderer
), Drawable, Element, Selectable {
	private val margin: Int
	private val maxVisibleLines: Int
	private val wrapWidth: Int
	private val multiline: Boolean
	private var text: String
	private var textChangedListener: Consumer<String>? = null
	private var topVisibleLine = 0
	private var bottomVisibleLine = 0
	private var cursorCounter = 0
	private var isFocused = false
	private var cursorPos = 0
	private var selectionPos: Int
	fun setTextChangedListener(textChangedListener: Consumer<String>?) {
		this.textChangedListener = textChangedListener
	}

	override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
		RenderUtils.drawRect(xPosition, yPosition, this.width, this.height, Colors.DHWUIA.getColor(0x4A))
		renderVisibleText(matrixStack)
		renderCursor(matrixStack)
		renderScrollBar()
		drawSelectionBox(x, y, this.width, this.height)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
		val isWithinBounds = isWithinBounds(mouseX, mouseY)
		setFocused(isWithinBounds)
		return click(mouseX, mouseY, mouseButton, isWithinBounds)
	}


	override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
		val isWithinBounds = isWithinBounds(mouseX, mouseY)
		setFocused(isWithinBounds)
		return click(mouseX, mouseY, state, isWithinBounds)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, mouseWheelDelta: Double): Boolean {
		return if (mouseWheelDelta < 0.0) {
			incrementVisibleLines()
			true
		} else if (mouseWheelDelta > 0.0) {
			decrementVisibleLines()
			true
		} else {
			false
		}
	}

	private fun click(mouseX: Double, mouseY: Double, mouseButton: Int, isWithinBounds: Boolean): Boolean {
		return if (isFocused && isWithinBounds && mouseButton == 0) {
			val relativeMouseX = mouseX.toInt() - xPosition - margin
			val relativeMouseY = mouseY.toInt() - yPosition - margin
			val y = MathHelper.clamp(relativeMouseY / 9 + topVisibleLine, 0, finalLineIndex)
			val x =
				fontRenderer.trimToWidth(StringVisitable.plain(getLine(y).string), relativeMouseX).string.length
			setCursorPos(countCharacters(y) + x)
			true
		} else {
			false
		}
	}


	override fun keyPressed(keyCode: Int, par2: Int, par3: Int): Boolean {
		if (Screen.isCopy(keyCode)) {
			mc.keyboard.clipboard = this.selectedText
		} else if (Screen.isCut(keyCode)) {
			if (selectionDifference != 0) {
				mc.keyboard.clipboard = this.selectedText
				deleteSelectedText()
			}
		} else if (Screen.isPaste(keyCode)) {
			insert(mc.keyboard.clipboard)
		} else if (isKeyComboCtrlBack(keyCode)) {
			deletePrevWord()
		} else {
			if (keyCode == 259) {
				if (selectionDifference != 0) {
					deleteSelectedText()
				} else {
					deletePrev()
				}
				return true
			}
			if (keyCode == 261) {
				if (selectionDifference != 0) {
					deleteSelectedText()
				} else {
					deleteNext()
				}
				return true
			}
			if (keyCode == 258) {
				insert("    ")
				return true
			}
			if (keyCode == 335) {
				if (multiline) {
					insertNewLine()
				}
				return true
			}
			if (keyCode == 257) {
				if (multiline) {
					insertNewLine()
				}
				return true
			}
			if (keyCode == 268) {
				updateSelectionPos()
				setCursorPos(0)
				return true
			}
			if (keyCode == 269) {
				updateSelectionPos()
				setCursorPos(this.text.length)
				return true
			}
			if (keyCode == 265) {
				updateSelectionPos()
				moveUp()
				return true
			}
			if (keyCode == 264) {
				updateSelectionPos()
				moveDown()
				return true
			}
			var moveRight: Boolean
			if (keyCode == 263) {
				moveRight = true
				if (Screen.hasShiftDown()) {
					if (selectionPos < 0) {
						selectionPos = cursorPos
					}
				} else {
					if (selectionPos > -1) {
						setCursorPos(this.getSelectionStart())
						moveRight = false
					}
					selectionPos = -1
				}
				if (moveRight) {
					moveLeft()
				}
				return true
			}
			if (keyCode == 262) {
				moveRight = true
				if (Screen.hasShiftDown()) {
					if (selectionPos < 0) {
						selectionPos = cursorPos
					}
				} else {
					if (selectionPos > -1) {
						setCursorPos(this.getSelectionEnd())
						moveRight = false
					}
					selectionPos = -1
				}
				if (moveRight) {
					moveRight()
				}
				return true
			}
		}
		return false
	}

	override fun charTyped(typedChar: Char, p_charTyped_2_: Int): Boolean {
		return if (isFocused() && SharedConstants.isValidChar(typedChar)) {
			insert(typedChar.toString())
			updateVisibleLines()
			true
		} else {
			false
		}
	}

	override fun tick() {
		++cursorCounter
	}

	fun toLines(): List<StringVisitable> {
		return wrapToWidth(this.text, wrapWidth)
	}

	fun toLinesWithIndication(): List<WrappedString> {
		return wrapToWidthWithIndication(this.text, wrapWidth)
	}

	fun getLine(line: Int): StringVisitable {
		return if (line >= 0 && line < toLines().size) toLines()[line] else finalLine
	}

	val finalLine: StringVisitable
		get() = getLine(finalLineIndex)
	val currentLine: StringVisitable
		get() = getLine(cursorY)
	val visibleLines: List<StringVisitable>
		get() {
			val lines = toLines()
			val visibleLines: MutableList<StringVisitable> = ArrayList()
			for (i in topVisibleLine..bottomVisibleLine) {
				if (i < lines.size) {
					visibleLines.add(lines[i])
				}
			}
			return visibleLines
		}

	override fun getText(): String {
		return this.text
	}

	override fun setText(newText: String) {
		if (multiline) {
			this.text = newText
		} else {
			this.text = newText.replace("\n".toRegex(), "")
		}
		this.onChanged()
		updateVisibleLines()
	}

	private fun onChanged() {
		if (textChangedListener != null) {
			textChangedListener!!.accept(this.text)
		}
	}

	val finalLineIndex: Int
		get() = toLines().size - 1

	private fun cursorIsValid(): Boolean {
		val y = cursorY
		return y in (topVisibleLine..bottomVisibleLine)
	}

	private val renderSafeCursorY: Int
		get() = cursorY - topVisibleLine
	val absoluteBottomVisibleLine: Int
		get() = topVisibleLine + (maxVisibleLines - 1)

	private fun getCursorWidth(): Int {
		val line = currentLine
		return fontRenderer.getWidth(line.string.substring(0, MathHelper.clamp(cursorX, 0, line.string.length)))
	}


	private fun isWithinBounds(mouseX: Double, mouseY: Double): Boolean {
		return mouseX >= xPosition.toDouble() && mouseX < (xPosition + this.width).toDouble() && mouseY >= yPosition.toDouble() && mouseY < (yPosition + this.height).toDouble()
	}

	fun atBeginningOfLine(): Boolean {
		return cursorX == 0
	}

	fun atEndOfLine(): Boolean {
		return cursorX == currentLine.string.length
	}

	private fun atBeginningOfNote(): Boolean {
		return cursorPos == 0
	}

	private fun atEndOfNote(): Boolean {
		return cursorPos >= this.text.length
	}

	val visibleLineCount: Int
		get() = bottomVisibleLine - topVisibleLine + 1

	fun updateVisibleLines() {
		while (visibleLineCount <= maxVisibleLines && bottomVisibleLine < finalLineIndex) {
			++bottomVisibleLine
		}
	}

	fun needsScrollBar(): Boolean {
		return toLines().size > visibleLineCount
	}

	override fun getWidth(): Int {
		return this.width - margin * 2
	}

	override fun isFocused(): Boolean {
		return isFocused
	}

	override fun setFocused(focused: Boolean) {
		if (focused && !isFocused) {
			cursorCounter = 0
		}
		isFocused = focused
	}

	fun isKeyComboCtrlBack(keyCode: Int): Boolean {
		return keyCode == 259 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()
	}

	fun insert(newText: String) {
		deleteSelectedText()
		val finalText = insertStringAt(filter(newText), this.text, cursorPos)
		setText(finalText)
		moveCursorPosBy(newText.length)
	}

	fun insertNewLine() {
		insert('\n'.toString())
	}

	private fun deleteNext() {
		val currentText = this.text
		if (!atEndOfNote() && currentText.isNotEmpty()) {
			val sb = StringBuilder(currentText)
			sb.deleteCharAt(cursorPos)
			setText(sb.toString())
			--selectionPos
		}
	}

	private fun deletePrev() {
		val currentText = this.text
		if (!atBeginningOfNote() && currentText.isNotEmpty()) {
			val sb = StringBuilder(currentText)
			sb.deleteCharAt(cursorPos - 1)
			setText(sb.toString())
			moveLeft()
		}
	}

	private fun deletePrevWord() {
		if (!atBeginningOfNote()) {
			var prev = this.text[cursorPos - 1]
			if (prev == ' ') {
				while (prev == ' ') {
					deletePrev()
					if (atBeginningOfNote()) {
						return
					}
					prev = this.text[cursorPos - 1]
				}
			} else {
				while (prev != ' ') {
					deletePrev()
					if (atBeginningOfNote()) {
						return
					}
					prev = this.text[cursorPos - 1]
				}
			}
		}
	}

	private fun deleteSelectedText() {
		while (selectionDifference > 0) {
			deletePrev()
		}
		while (selectionDifference < 0) {
			deleteNext()
		}
		selectionPos = -1
	}

	private fun incrementVisibleLines() {
		if (bottomVisibleLine < finalLineIndex) {
			++topVisibleLine
			++bottomVisibleLine
		}
	}

	private fun decrementVisibleLines() {
		if (topVisibleLine > 0) {
			--topVisibleLine
			--bottomVisibleLine
		}
	}

	private fun countCharacters(maxLineIndex: Int): Int {
		val wrappedLines = toLinesWithIndication()
		var count = 0
		for (i in 0 until maxLineIndex) {
			val wrappedLine = wrappedLines[i]
			count += wrappedLine.text.length
			if (!wrappedLine.isWrapped) {
				++count
			}
		}
		return count
	}

	private fun getCursorX(pos: Int): Int {
		val wrappedLines = toLinesWithIndication()
		val y = cursorY
		var currentLineIsWrapped = false
		var count = 0
		for (i in 0..y) {
			if (i < wrappedLines.size) {
				val wrappedLine = wrappedLines[i]
				if (i < y) {
					count += wrappedLine.text.length
					if (!wrappedLine.isWrapped) {
						++count
					}
				}
				if (wrappedLine.isWrapped && i == y && i > 0) {
					currentLineIsWrapped = true
				}
			}
		}
		if (currentLineIsWrapped) {
			--count
		}
		return pos - count
	}

	private val cursorX: Int
		get() = getCursorX(cursorPos)

	private fun getCursorY(pos: Int): Int {
		val wrappedLines = toLinesWithIndication()
		var count = 0
		for (i in wrappedLines.indices) {
			val wrappedLine = wrappedLines[i]
			count += wrappedLine.text.length
			if (!wrappedLine.isWrapped) {
				++count
			}
			if (count > pos) {
				return i
			}
		}
		return finalLineIndex
	}

	private val cursorY: Int
		get() = getCursorY(cursorPos)
	private val selectionDifference: Int
		get() = if (selectionPos > -1) cursorPos - selectionPos else 0

	private fun hasSelectionOnLine(line: Int): Boolean {
		if (selectionPos > -1) {
			val wrappedLines = toLinesWithIndication()
			var count = 0
			for (i in 0..line) {
				val wrappedLine = wrappedLines[i]
				for (j in 0 until wrappedLine.text.length) {
					++count
					if (line == i && isInSelection(count)) {
						return true
					}
				}
				if (!wrappedLine.isWrapped) {
					++count
				}
			}
		}
		return false
	}

	private fun setCursorPos(pos: Int) {
		cursorPos = MathHelper.clamp(pos, 0, this.text.length)
		if (cursorY > bottomVisibleLine) {
			incrementVisibleLines()
		} else if (cursorY < topVisibleLine) {
			decrementVisibleLines()
		}
	}

	private fun moveCursorPosBy(amount: Int) {
		setCursorPos(cursorPos + amount)
	}

	private fun moveRight() {
		if (!atEndOfNote()) {
			moveCursorPosBy(1)
		}
	}

	private fun moveLeft() {
		if (!atBeginningOfNote()) {
			moveCursorPosBy(-1)
		}
	}

	private fun moveUp() {
		val width = getCursorWidth()
		val y = cursorY
		while (cursorPos > 0 && (cursorY == y || getCursorWidth() > width)) {
			moveLeft()
		}
	}

	private fun moveDown() {
		val width = getCursorWidth()
		val y = cursorY
		while (cursorPos < this.text.length && (cursorY == y || getCursorWidth() < width)) {
			moveRight()
		}
	}

	private fun updateSelectionPos() {
		if (Screen.hasShiftDown()) {
			if (selectionPos < 0) {
				selectionPos = cursorPos
			}
		} else {
			selectionPos = -1
		}
	}

	private fun isInSelection(pos: Int): Boolean {
		return if (selectionPos <= -1) {
			false
		} else {
			pos >= this.getSelectionStart() && pos <= this.getSelectionEnd()
		}
	}

	private fun getSelectionStart(): Int {
		if (selectionPos > -1) {
			if (selectionPos > cursorPos) {
				return cursorPos
			}
			if (cursorPos > selectionPos) {
				return selectionPos
			}
		}
		return -1
	}


	private fun getSelectionEnd(): Int {
		if (selectionPos > -1) {
			if (selectionPos > cursorPos) {
				return selectionPos
			}
			if (cursorPos > selectionPos) {
				return cursorPos
			}
		}
		return -1
	}


	override fun getSelectedText(): String {
		return if (this.getSelectionStart() >= 0 && this.getSelectionEnd() >= 0) this.text.substring(
			this.getSelectionStart(),
			this.getSelectionEnd()
		) else ""
	}

	private fun drawSelectionBox(startX: Int, startY: Int, endX: Int, endY: Int) {
		if (isFocused()) RenderUtils.drawOutline(startX, startY, endX, endY, 0x7FE0E0E0)
	}

	private fun renderVisibleText(matrixStack: MatrixStack) {
		var renderY = yPosition + margin
		for (line in visibleLines) {
			val language = Language.getInstance()
			fontRenderer.drawWithShadow(
				matrixStack,
				language.reorder(line),
				(xPosition + margin).toFloat(),
				renderY.toFloat(),
				14737632
			)
			Objects.requireNonNull(fontRenderer)
			renderY += 9
		}
	}

	private fun renderCursor(matrixStack: MatrixStack) {
		val shouldDisplayCursor = isFocused && cursorCounter / 6 % 2 == 0 && cursorIsValid()
		if (shouldDisplayCursor) {
			val line = currentLine
			val renderCursorX = xPosition + margin + fontRenderer.getWidth(
				line.string.substring(
					0, MathHelper.clamp(
						cursorX, 0, line.string.length
					)
				)
			)
			val var10000 = yPosition + margin
			val var10001 = renderSafeCursorY
			Objects.requireNonNull(fontRenderer)
			val renderCursorY = var10000 + var10001 * 9
			fontRenderer.drawWithShadow(
				matrixStack,
				Text.of("§6_§r"),
				(renderCursorX + 1).toFloat(),
				renderCursorY.toFloat(),
				-3092272
			)
		}
	}

	private fun renderScrollBar() {
		if (needsScrollBar()) {
			val lines = toLines()
			val effectiveHeight = this.height - margin / 2
			val scrollBarHeight =
				MathHelper.floor(effectiveHeight.toDouble() * (visibleLineCount.toDouble() / lines.size.toDouble()))
			var scrollBarTop =
				yPosition + margin / 4 + MathHelper.floor(topVisibleLine.toDouble() / lines.size.toDouble() * effectiveHeight.toDouble())
			val diff = scrollBarTop + scrollBarHeight - (yPosition + this.height)
			if (diff > 0) {
				scrollBarTop -= diff
			}
			RenderUtils.drawRect(
				xPosition + this.width - margin * 2 / 4,
				scrollBarTop,
				margin / 4,
				scrollBarHeight,
				-3092272
			)
		}
	}

	override fun appendNarrations(builder: NarrationMessageBuilder) {}
	override fun getType(): SelectionType {
		return SelectionType.FOCUSED
	}

	 class WrappedString(val text: String, private val wrapped: Boolean) {
		val isWrapped: Boolean
			get() = this.wrapped
	}

	companion object {
		private val mc = MinecraftClient.getInstance()
	}

	init {
		this.width = width
		this.height = height
		this.margin = margin
		this.multiline = multiline
		this.text = ""
		val var10001 = height.toFloat() - margin.toFloat() * 2.0f
		Objects.requireNonNull(fontRenderer)
		maxVisibleLines = MathHelper.floor(var10001 / 9.0f) - 1
		wrapWidth = width - margin * 2
		selectionPos = -1
	}
}