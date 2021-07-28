package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBase;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.text.TranslatableText;

import java.util.List;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name WidgetEventEntry
 * <p>#create_time 2021-07-28 18:24
 */
public class WidgetEventEntry extends WidgetListEntryBase<EventBase> {
    private final EventBase event;
    private final List<String> hoverLines;
    private final boolean isOdd;
    private final WidgetListEvent parent;
    private final int buttonsStartX;

    public WidgetEventEntry(int x, int y, int width, int height, boolean isOdd, EventBase entry, int listIndex, WidgetListEvent parent) {
        super(x, y, width, height, entry, listIndex);
        this.event = entry;
        this.hoverLines = entry.getWidgetHoverLines();
        this.isOdd = isOdd;
        this.parent = parent;
        y += 1;
        int posX = x + width - 2;

        this.buttonsStartX = posX;
    }

    protected int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth() + 1;
    }


    private static class ButtonListener implements IButtonActionListener {
        private final Type type;
        private final WidgetEventEntry widget;

        public ButtonListener(Type type, WidgetEventEntry widget) {
            this.type = type;
            this.widget = widget;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            switch (this.type){
                case EDIT -> {
                    //todo open edit gui
                }
                case REMOVE -> {
                    HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.unsubscribe(this.widget.event);
                    this.widget.parent.refreshEntries();
                }
                case ENABLED -> {
                    this.widget.event.toggleEnable();
                    this.widget.parent.refreshEntries();
                }

            }
        }

        public enum Type {
            EDIT(StringUtil.translatableText("gui.button.edit")),
            ENABLED     (StringUtil.translatableText(".gui.button.enabled")),
            REMOVE(StringUtil.translatableText(".gui.button.remove"));

            private final TranslatableText translationKey;

            Type(TranslatableText translationKey) {
                this.translationKey = translationKey;
            }

            public String getTranslationKey() {
                return this.translationKey.getKey();
            }

            public String getDisplayName(Object... args) {
                return StringUtils.translate(getTranslationKey(), args);
            }
        }
    }
}
