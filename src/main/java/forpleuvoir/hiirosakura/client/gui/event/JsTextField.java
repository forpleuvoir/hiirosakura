package forpleuvoir.hiirosakura.client.gui.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.render.RenderUtils;
import forpleuvoir.hiirosakura.client.util.Colors;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author forpleuvoir
 * <p>
 * #project_name hiirosakura
 * <p>
 * #package forpleuvoir.hiirosakura.client.gui.event
 * <p>
 * #class_name JsTextField
 * <p>
 * #create_time 2021-08-11 17:47
 */
public class JsTextField extends GuiTextFieldGeneric implements Drawable, Element, Selectable {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final TextRenderer fontRenderer;
    private final int margin;
    private final int maxVisibleLines;
    private final int wrapWidth;
    private final boolean canLoseFocus = true;
    private final boolean isEnabled = true;
    private final boolean multiline;
    public int xPosition;
    public int yPosition;
    public int width;
    public int height;
    private String text;
    private int topVisibleLine;
    private int bottomVisibleLine;
    private int cursorCounter;
    private boolean isFocused;
    private int cursorPos;
    private int selectionPos;

    public JsTextField(TextRenderer fontRenderer, int x, int y, int width, int height, int margin, boolean multiline) {
        super(x, y, width, height, fontRenderer);
        this.fontRenderer = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.multiline = multiline;
        this.text = "";
        float var10001 = (float) height - (float) margin * 2.0F;
        Objects.requireNonNull(fontRenderer);
        this.maxVisibleLines = MathHelper.floor(var10001 / 9.0F) - 1;
        this.wrapWidth = width - margin * 2;
        this.selectionPos = -1;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(this.xPosition, this.yPosition,  this.width,  this.height, Colors.DHWUIA.getColor(0x7A));
        this.renderVisibleText(matrixStack);
        this.renderCursor(matrixStack);
        this.renderScrollBar();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean isWithinBounds = this.isWithinBounds(mouseX, mouseY);
        this.setFocused(isWithinBounds);
        if (this.isFocused && isWithinBounds && mouseButton == 0) {
            int relativeMouseX = (int) mouseX - this.xPosition - this.margin;
            int relativeMouseY = (int) mouseY - this.yPosition - this.margin;
            Objects.requireNonNull(this.fontRenderer);
            int y = MathHelper.clamp(relativeMouseY / 9 + this.topVisibleLine, 0, this.getFinalLineIndex());
            int x = this.fontRenderer.trimToWidth(StringVisitable.plain(this.getLine(y).getString()), relativeMouseX).getString().length();
            this.setCursorPos(this.countCharacters(y) + x);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        boolean isWithinBounds = this.isWithinBounds(mouseX, mouseY);
        this.setFocused(isWithinBounds);
        if (this.isFocused && isWithinBounds && state == 0) {
            int relativeMouseX = (int) mouseX - this.xPosition - this.margin;
            int relativeMouseY = (int) mouseY - this.yPosition - this.margin;
            Objects.requireNonNull(this.fontRenderer);
            int y = MathHelper.clamp(relativeMouseY / 9 + this.topVisibleLine, 0, this.getFinalLineIndex());
            int x = this.fontRenderer.trimToWidth(StringVisitable.plain(this.getLine(y).getString()), relativeMouseX).getString().length();
            int pos = MathHelper.clamp(this.countCharacters(y) + x, 0, this.text.length());
            if (pos != this.cursorPos) {
                this.selectionPos = this.cursorPos;
                this.setCursorPos(pos);
            } else {
                this.selectionPos = -1;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double par1, double par2, double par3) {
        if (par1 < 0.0D) {
            this.incrementVisibleLines();
            return true;
        } else if (par1 > 0.0D) {
            this.decrementVisibleLines();
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int par2, int par3) {
        if (Screen.isCopy(keyCode)) {
            mc.keyboard.setClipboard(this.getSelectedText());
        } else if (Screen.isCut(keyCode)) {
            if (this.getSelectionDifference() != 0) {
                mc.keyboard.setClipboard(this.getSelectedText());
                this.deleteSelectedText();
            }
        } else if (Screen.isPaste(keyCode)) {
            this.insert(mc.keyboard.getClipboard());
        } else if (this.isKeyComboCtrlBack(keyCode)) {
            this.deletePrevWord();
        } else {
            if (keyCode == 259) {
                if (this.getSelectionDifference() != 0) {
                    this.deleteSelectedText();
                } else {
                    this.deletePrev();
                }

                return true;
            }

            if (keyCode == 261) {
                if (this.getSelectionDifference() != 0) {
                    this.deleteSelectedText();
                } else {
                    this.deleteNext();
                }

                return true;
            }

            if (keyCode == 258) {
                this.insert("    ");
                return true;
            }

            if (keyCode == 335) {
                if (this.multiline) {
                    this.insertNewLine();
                }

                return true;
            }

            if (keyCode == 257) {
                if (this.multiline) {
                    this.insertNewLine();
                }

                return true;
            }

            if (keyCode == 268) {
                this.updateSelectionPos();
                this.setCursorPos(0);
                return true;
            }

            if (keyCode == 269) {
                this.updateSelectionPos();
                this.setCursorPos(this.text.length());
                return true;
            }

            if (keyCode == 265) {
                this.updateSelectionPos();
                this.moveUp();
                return true;
            }

            if (keyCode == 264) {
                this.updateSelectionPos();
                this.moveDown();
                return true;
            }

            boolean moveRight;
            if (keyCode == 263) {
                moveRight = true;
                if (Screen.hasShiftDown()) {
                    if (this.selectionPos < 0) {
                        this.selectionPos = this.cursorPos;
                    }
                } else {
                    if (this.selectionPos > -1) {
                        this.setCursorPos(this.getSelectionStart());
                        moveRight = false;
                    }

                    this.selectionPos = -1;
                }

                if (moveRight) {
                    this.moveLeft();
                }

                return true;
            }

            if (keyCode == 262) {
                moveRight = true;
                if (Screen.hasShiftDown()) {
                    if (this.selectionPos < 0) {
                        this.selectionPos = this.cursorPos;
                    }
                } else {
                    if (this.selectionPos > -1) {
                        this.setCursorPos(this.getSelectionEnd());
                        moveRight = false;
                    }

                    this.selectionPos = -1;
                }

                if (moveRight) {
                    this.moveRight();
                }

                return true;
            }
        }

        return false;
    }

    public boolean charTyped(char typedChar, int p_charTyped_2_) {
        if (this.isFocused() && SharedConstants.isValidChar(typedChar)) {
            this.insert(Character.toString(typedChar));
            this.updateVisibleLines();
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        ++this.cursorCounter;
    }

    public List<StringVisitable> toLines() {
        return StringUtil.wrapToWidth(this.text, this.wrapWidth);
    }

    public List<WrappedString> toLinesWithIndication() {
        return StringUtil.wrapToWidthWithIndication(this.text, this.wrapWidth);
    }

    public StringVisitable getLine(int line) {
        return line >= 0 && line < this.toLines().size() ? (StringVisitable) this.toLines().get(line) : this.getFinalLine();
    }

    public StringVisitable getFinalLine() {
        return this.getLine(this.getFinalLineIndex());
    }

    public StringVisitable getCurrentLine() {
        return this.getLine(this.getCursorY());
    }

    public List<StringVisitable> getVisibleLines() {
        List<StringVisitable> lines = this.toLines();
        List<StringVisitable> visibleLines = new ArrayList<>();

        for (int i = this.topVisibleLine; i <= this.bottomVisibleLine; ++i) {
            if (i < lines.size()) {
                visibleLines.add(lines.get(i));
            }
        }

        return visibleLines;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String newText) {
        if (this.multiline) {
            this.text = newText;
        } else {
            this.text = newText.replaceAll("\n", "");
        }

        this.updateVisibleLines();
    }

    public int getFinalLineIndex() {
        return this.toLines().size() - 1;
    }

    public boolean cursorIsValid() {
        int y = this.getCursorY();
        return y >= this.topVisibleLine && y <= this.bottomVisibleLine;
    }

    public int getRenderSafeCursorY() {
        return this.getCursorY() - this.topVisibleLine;
    }

    public int getAbsoluteBottomVisibleLine() {
        return this.topVisibleLine + (this.maxVisibleLines - 1);
    }

    public int getCursorWidth(int pos) {
        StringVisitable line = this.getCurrentLine();
        return this.fontRenderer.getWidth(line.getString().substring(0, MathHelper.clamp(this.getCursorX(), 0, line.getString().length())));
    }

    public int getCursorWidth() {
        return this.getCursorWidth(this.cursorPos);
    }

    public boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double) this.xPosition && mouseX < (double) (this.xPosition + this.width) && mouseY >= (double) this.yPosition && mouseY < (double) (this.yPosition + this.height);
    }

    public boolean atBeginningOfLine() {
        return this.getCursorX() == 0;
    }

    public boolean atEndOfLine() {
        return this.getCursorX() == this.getCurrentLine().getString().length();
    }

    public boolean atBeginningOfNote() {
        return this.cursorPos == 0;
    }

    public boolean atEndOfNote() {
        return this.cursorPos >= this.text.length();
    }

    public int getVisibleLineCount() {
        return this.bottomVisibleLine - this.topVisibleLine + 1;
    }

    public void updateVisibleLines() {
        while (this.getVisibleLineCount() <= this.maxVisibleLines && this.bottomVisibleLine < this.getFinalLineIndex()) {
            ++this.bottomVisibleLine;
        }

    }

    public boolean needsScrollBar() {
        return this.toLines().size() > this.getVisibleLineCount();
    }

    public int getWidth() {
        return this.width - this.margin * 2;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setFocused(boolean focused) {
        if (focused && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = focused;
    }

    public boolean isKeyComboCtrlBack(int keyCode) {
        return keyCode == 259 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void insert(String newText) {
        this.deleteSelectedText();
        String finalText = StringUtil.insertStringAt(StringUtil.filter(newText), this.text, this.cursorPos);
        this.setText(finalText);
        this.moveCursorPosBy(newText.length());
    }

    public void insertNewLine() {
        this.insert(String.valueOf('\n'));
    }

    private void deleteNext() {
        String currentText = this.text;
        if (!this.atEndOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.deleteCharAt(this.cursorPos);
            this.setText(sb.toString());
            --this.selectionPos;
        }

    }

    private void deletePrev() {
        String currentText = this.text;
        if (!this.atBeginningOfNote() && !currentText.isEmpty()) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.deleteCharAt(this.cursorPos - 1);
            this.setText(sb.toString());
            this.moveLeft();
        }

    }

    private void deletePrevWord() {
        if (!this.atBeginningOfNote()) {
            char prev = this.text.charAt(this.cursorPos - 1);
            if (prev == ' ') {
                while (prev == ' ') {
                    this.deletePrev();
                    if (this.atBeginningOfNote()) {
                        return;
                    }

                    prev = this.text.charAt(this.cursorPos - 1);
                }
            } else {
                while (prev != ' ') {
                    this.deletePrev();
                    if (this.atBeginningOfNote()) {
                        return;
                    }

                    prev = this.text.charAt(this.cursorPos - 1);
                }
            }
        }

    }

    private void deleteSelectedText() {
        while (this.getSelectionDifference() > 0) {
            this.deletePrev();
        }

        while (this.getSelectionDifference() < 0) {
            this.deleteNext();
        }

        this.selectionPos = -1;
    }

    private void incrementVisibleLines() {
        if (this.bottomVisibleLine < this.getFinalLineIndex()) {
            ++this.topVisibleLine;
            ++this.bottomVisibleLine;
        }

    }

    private void decrementVisibleLines() {
        if (this.topVisibleLine > 0) {
            --this.topVisibleLine;
            --this.bottomVisibleLine;
        }

    }

    private int countCharacters(int maxLineIndex) {
        List<WrappedString> wrappedLines = this.toLinesWithIndication();
        int count = 0;

        for (int i = 0; i < maxLineIndex; ++i) {
            WrappedString wrappedLine = (WrappedString) wrappedLines.get(i);
            count += wrappedLine.getText().length();
            if (!wrappedLine.isWrapped()) {
                ++count;
            }
        }

        return count;
    }

    private int getCursorX(int pos) {
        List<WrappedString> wrappedLines = this.toLinesWithIndication();
        int y = this.getCursorY();
        boolean currentLineIsWrapped = false;
        int count = 0;

        for (int i = 0; i <= y; ++i) {
            if (i < wrappedLines.size()) {
                WrappedString wrappedLine = (WrappedString) wrappedLines.get(i);
                if (i < y) {
                    count += wrappedLine.getText().length();
                    if (!wrappedLine.isWrapped()) {
                        ++count;
                    }
                }

                if (wrappedLine.isWrapped() && i == y && i > 0) {
                    currentLineIsWrapped = true;
                }
            }
        }

        if (currentLineIsWrapped) {
            --count;
        }

        return pos - count;
    }

    private int getCursorX() {
        return this.getCursorX(this.cursorPos);
    }

    private int getCursorY(int pos) {
        List<WrappedString> wrappedLines = this.toLinesWithIndication();
        int count = 0;

        for (int i = 0; i < wrappedLines.size(); ++i) {
            WrappedString wrappedLine = (WrappedString) wrappedLines.get(i);
            count += wrappedLine.getText().length();
            if (!wrappedLine.isWrapped()) {
                ++count;
            }

            if (count > pos) {
                return i;
            }
        }

        return this.getFinalLineIndex();
    }

    private int getCursorY() {
        return this.getCursorY(this.cursorPos);
    }

    private int getSelectionDifference() {
        return this.selectionPos > -1 ? this.cursorPos - this.selectionPos : 0;
    }

    private boolean hasSelectionOnLine(int line) {
        if (this.selectionPos > -1) {
            List<WrappedString> wrappedLines = this.toLinesWithIndication();
            int count = 0;

            for (int i = 0; i <= line; ++i) {
                WrappedString wrappedLine = wrappedLines.get(i);

                for (int j = 0; j < wrappedLine.getText().length(); ++j) {
                    ++count;
                    if (line == i && this.isInSelection(count)) {
                        return true;
                    }
                }

                if (!wrappedLine.isWrapped()) {
                    ++count;
                }
            }
        }

        return false;
    }

    private void setCursorPos(int pos) {
        this.cursorPos = MathHelper.clamp(pos, 0, this.text.length());
        if (this.getCursorY() > this.bottomVisibleLine) {
            this.incrementVisibleLines();
        } else if (this.getCursorY() < this.topVisibleLine) {
            this.decrementVisibleLines();
        }

    }

    private void moveCursorPosBy(int amount) {
        this.setCursorPos(this.cursorPos + amount);
    }

    private void moveRight() {
        if (!this.atEndOfNote()) {
            this.moveCursorPosBy(1);
        }

    }

    private void moveLeft() {
        if (!this.atBeginningOfNote()) {
            this.moveCursorPosBy(-1);
        }

    }

    private void moveUp() {
        int width = this.getCursorWidth();
        int y = this.getCursorY();

        while (this.cursorPos > 0 && (this.getCursorY() == y || this.getCursorWidth() > width)) {
            this.moveLeft();
        }

    }

    private void moveDown() {
        int width = this.getCursorWidth();
        int y = this.getCursorY();

        while (this.cursorPos < this.text.length() && (this.getCursorY() == y || this.getCursorWidth() < width)) {
            this.moveRight();
        }

    }

    private void updateSelectionPos() {
        if (Screen.hasShiftDown()) {
            if (this.selectionPos < 0) {
                this.selectionPos = this.cursorPos;
            }
        } else {
            this.selectionPos = -1;
        }

    }

    private boolean isInSelection(int pos) {
        if (this.selectionPos <= -1) {
            return false;
        } else {
            return pos >= this.getSelectionStart() && pos <= this.getSelectionEnd();
        }
    }

    private int getSelectionStart() {
        if (this.selectionPos > -1) {
            if (this.selectionPos > this.cursorPos) {
                return this.cursorPos;
            }

            if (this.cursorPos > this.selectionPos) {
                return this.selectionPos;
            }
        }

        return -1;
    }

    private int getSelectionEnd() {
        if (this.selectionPos > -1) {
            if (this.selectionPos > this.cursorPos) {
                return this.selectionPos;
            }

            if (this.cursorPos > this.selectionPos) {
                return this.cursorPos;
            }
        }

        return -1;
    }

    public String getSelectedText() {
        return this.getSelectionStart() >= 0 && this.getSelectionEnd() >= 0 ? this.text.substring(this.getSelectionStart(), this.getSelectionEnd()) : "";
    }

    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        int temp;
        if (startX < endX) {
            temp = startX;
            startX = endX;
            endX = temp;
        }

        if (startY < endY) {
            temp = startY;
            startY = endY;
            endY = temp;
        }

        if (endX > this.xPosition + this.width) {
            endX = this.xPosition + this.width;
        }

        if (startX > this.xPosition + this.width) {
            startX = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.clearColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.COPY);
        buffer.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION);
        buffer.vertex(startX, endY, 0.0D).next();
        buffer.vertex(endX, endY, 0.0D).next();
        buffer.vertex(endX, startY, 0.0D).next();
        buffer.vertex(startX, startY, 0.0D).next();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private void renderSelectionBox(int y, int renderY, OrderedText line) {
        if (this.hasSelectionOnLine(y)) {
            StringVisitable absoluteLine = this.getLine(y);
            int count = 0;
            List<WrappedString> wrappedLines = this.toLinesWithIndication();

            int start;
            for (start = 0; start < y; ++start) {
                WrappedString wrappedLine = wrappedLines.get(start);
                count += wrappedLine.getText().length();
                if (!wrappedLine.isWrapped()) {
                    ++count;
                }
            }

            if (wrappedLines.get(y).isWrapped()) {
                --count;
            }

            start = this.getSelectionStart() - count;
            if (start < 0) {
                start = 0;
            }

            int end = this.getSelectionEnd() - count;
            if (end > absoluteLine.getString().length()) {
                end = absoluteLine.getString().length();
            }

            if (start >= end) {
                this.selectionPos = -1;
            } else {
                String selection = absoluteLine.getString().substring(start, end);
                int startX = this.xPosition + this.margin + this.fontRenderer.getWidth(absoluteLine.getString().substring(0, start));
                int endX = startX + this.fontRenderer.getWidth(selection);
                Objects.requireNonNull(this.fontRenderer);
                this.drawSelectionBox(startX, renderY, endX, renderY + 9);
            }
        }

    }

    private void renderVisibleText(MatrixStack matrixStack) {
        int renderY = this.yPosition + this.margin;
        int y = this.topVisibleLine;

        for (Iterator<StringVisitable> var4 = this.getVisibleLines().iterator(); var4.hasNext(); ++y) {
            StringVisitable line = var4.next();
            Language language = Language.getInstance();
            this.fontRenderer.drawWithShadow(matrixStack, language.reorder(line), (float) (this.xPosition + this.margin), (float) renderY, 14737632);
            this.renderSelectionBox(y, renderY, language.reorder(line));
            Objects.requireNonNull(this.fontRenderer);
            renderY += 9;
        }

    }

    private void renderCursor(MatrixStack matrixStack) {
        boolean shouldDisplayCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0 && this.cursorIsValid();
        if (shouldDisplayCursor) {
            StringVisitable line = this.getCurrentLine();
            int renderCursorX = this.xPosition + this.margin + this.fontRenderer.getWidth(line.getString().substring(0, MathHelper.clamp(this.getCursorX(), 0, line.getString().length())));
            int var10000 = this.yPosition + this.margin;
            int var10001 = this.getRenderSafeCursorY();
            Objects.requireNonNull(this.fontRenderer);
            int renderCursorY = var10000 + var10001 * 9;
            this.fontRenderer.drawWithShadow(matrixStack, Text.of("_"), (float) (renderCursorX + 1), (float) renderCursorY, -3092272);
        }

    }

    private void renderScrollBar() {
        if (this.needsScrollBar()) {
            List<StringVisitable> lines = this.toLines();
            int effectiveHeight = this.height - this.margin / 2;
            int scrollBarHeight = MathHelper.floor((double) effectiveHeight * ((double) this.getVisibleLineCount() / (double) lines.size()));
            int scrollBarTop = this.yPosition + this.margin / 4 + MathHelper.floor((double) this.topVisibleLine / (double) lines.size() * (double) effectiveHeight);
            int diff = scrollBarTop + scrollBarHeight - (this.yPosition + this.height);
            if (diff > 0) {
                scrollBarTop -= diff;
            }

            RenderUtils.drawRect(this.xPosition + this.width - this.margin * 2 / 4, scrollBarTop,   this.margin / 4,   scrollBarHeight, -3092272);
        }

    }

    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    public Selectable.SelectionType getType() {
        return Selectable.SelectionType.FOCUSED;
    }

    public record WrappedString(String text, boolean wrapped) {

        public String getText() {
            return this.text;
        }

        public boolean isWrapped() {
            return this.wrapped;
        }
    }

}
