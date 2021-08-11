package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import forpleuvoir.hiirosakura.client.mixin.MixinTextFieldWidgetAccessor;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Predicate;


/**
 * 多行文本组件
 *
 * @author forpleuvoir
 * <p>
 * #project_name hiirosakura
 * <p>
 * #package forpleuvoir.hiirosakura.client.gui.event
 * <p>
 * #class_name MultiLineTextField
 * <p>
 * #create_time 2021-08-11 15:44
 */
public class MultiLineTextField extends GuiTextFieldGeneric {
    public MultiLineTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(x, y, width, height, textRenderer);
        this.setMaxLength(800);
        this.setText(text.asString());
        this.setRenderTextProvider((string, integer) ->
                OrderedText.styledForwardsVisitedString(string, Style.EMPTY)
        );
    }

    @Override
    public void write(String t) {
        int selectionStart = ((MixinTextFieldWidgetAccessor) this).getSelectionStart();
        int selectionEnd = ((MixinTextFieldWidgetAccessor) this).getSelectionEnd();
        int maxLength = ((MixinTextFieldWidgetAccessor) this).getMaxLength();
        Predicate<String> textPredicate = ((MixinTextFieldWidgetAccessor) this).getTextPredicate();
        String text = ((MixinTextFieldWidgetAccessor) this).getText();
        int i = Math.min(selectionStart, selectionEnd);
        int j = Math.max(selectionStart, selectionEnd);
        int k = maxLength - text.length() - i + j;
        String stripped = StringUtil.stripInvalidChars(t);
        int l = stripped.length();
        if (k < l) {
            stripped = stripped.substring(0, k);
            l = k;
        }
        String string = new StringBuilder(text).replace(i, j, stripped).toString();
        if (textPredicate.test(string)) {
            ((MixinTextFieldWidgetAccessor) this).setText(string);
            this.setSelectionStart(i + l);
            this.setSelectionEnd(((MixinTextFieldWidgetAccessor) this).getSelectionStart());
            ((MixinTextFieldWidgetAccessor) this).doOnChanged(((MixinTextFieldWidgetAccessor) this).getText());
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean editable = ((MixinTextFieldWidgetAccessor) this).getEditable();
        if (!isActive()) {
            return false;
        } else if (StringUtil.isValidChar(chr)) {
            if (editable) {
                write(Character.toString(chr));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            this.charTyped('\n', modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
