package forpleuvoir.hiirosakura.client.gui.event;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase;
import forpleuvoir.hiirosakura.client.util.StringUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.List;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name WidgetEventEntry
 * <p>#create_time 2021-07-28 18:24
 */
public class WidgetEventEntry extends WidgetListEntryBase<EventSubscriberBase> {
    private final EventSubscriberBase eventSubscriberBase;
    private final List<String> hoverLines;
    private final boolean isOdd;
    private final WidgetListEvent parent;
    private final int buttonsStartX;

    public WidgetEventEntry(int x, int y, int width, int height, boolean isOdd, EventSubscriberBase entry,
                            int listIndex, WidgetListEvent parent
    ) {
        super(x, y, width, height, entry, listIndex);
        this.eventSubscriberBase = entry;
        this.hoverLines = entry.getWidgetHoverLines();
        this.isOdd = isOdd;
        this.parent = parent;
        y += 1;
        int posX = x + width - 2;

        posX -= this.addButton(posX, y, ButtonListener.Type.REMOVE);
        posX -= this.createButtonOnOff(posX, y, this.eventSubscriberBase.enabled, ButtonListener.Type.ENABLED);
        posX -= this.addButton(posX, y, ButtonListener.Type.EDIT);

        this.buttonsStartX = posX;
    }

    protected int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, (type == ButtonListener.Type.REMOVE ? "§c" : "") + type
                .getDisplayName());
        this.addButton(button, new ButtonListener(type, this));
        return button.getWidth() + 1;
    }

    private int createButtonOnOff(int xRight, int y, boolean isCurrentlyOn, ButtonListener.Type type) {
        ButtonOnOff button = new ButtonOnOff(xRight, y, -1, true,
                                             type.getDisplayName() + ":" + (isCurrentlyOn ? "§a" : "§c") + isCurrentlyOn,
                                             isCurrentlyOn
        );
        this.addButton(button, new ButtonListener(type, this));
        return button.getWidth() + 2;
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton) {
        return super.canSelectAt(mouseX, mouseY, mouseButton) && mouseX < this.buttonsStartX;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        boolean isSelected = HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.isSelected(this.eventSubscriberBase);

        if (isSelected || selected || this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x2AFFFFFF);
        } else if (this.isOdd) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x10FFFFFF);
        } else {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        if (isSelected) {
            RenderUtils.drawOutline(this.x, this.y, this.width, this.height, 0x7FE0E0E0);
        }

        String name = this.eventSubscriberBase.name;
        this.drawString(this.x + 4, this.y + 7, 0xFFFFFFFF, name, matrixStack);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        super.render(mouseX, mouseY, selected, matrixStack);

        RenderUtils.disableDiffuseLighting();

    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
        super.postRenderHovered(mouseX, mouseY, selected, matrixStack);

        if (mouseX >= this.x && mouseX < this.buttonsStartX && mouseY >= this.y && mouseY <= this.y + this.height) {
            RenderUtils.drawHoverText(mouseX, mouseY, this.hoverLines, matrixStack);
        }
    }

    private record ButtonListener(WidgetEventEntry.ButtonListener.Type type,
                                  WidgetEventEntry widget) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            switch (this.type) {
                case EDIT -> {
                    GuiBase.openGui(new EventEditScreen(this.widget.eventSubscriberBase));
                }
                case REMOVE -> {
                    HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.unsubscribe(this.widget.eventSubscriberBase);
                    this.widget.parent.refreshEntries();
                }
                case ENABLED -> {
                    this.widget.eventSubscriberBase.toggleEnable();
                    this.widget.parent.refreshEntries();
                }

            }
        }

        public enum Type {
            EDIT(StringUtil.translatableText("gui.button.edit")),
            ENABLED(StringUtil.translatableText("gui.button.enabled")),
            REMOVE(StringUtil.translatableText("gui.button.remove"));

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
