package forpleuvoir.hiirosakura.client.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

/**
 * 文本组件访问器
 *
 * @author forpleuvoir
 * <p>
 * #project_name hiirosakura
 * <p>
 * #package forpleuvoir.hiirosakura.client.mixin
 * <p>
 * #class_name TextFieldWidgetAccessor
 * <p>
 * #create_time 2021-08-11 16:44
 */
@Mixin(TextFieldWidget.class)
public interface MixinTextFieldWidgetAccessor {

    @Accessor("selectionStart")
    int getSelectionStart();

    @Accessor("selectionEnd")
    int getSelectionEnd();

    @Accessor("maxLength")
    int getMaxLength();

    @Accessor("editable")
    boolean getEditable();

    @Accessor("textPredicate")
    Predicate<String> getTextPredicate();

    @Accessor("text")
    String getText();

    @Accessor("text")
    void setText(String text);

    @Invoker("onChanged")
    void doOnChanged(String newText);
}
