package forpleuvoir.hiirosakura.client.gui.event;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase;
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents;
import forpleuvoir.hiirosakura.client.gui.GuiConfig;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static forpleuvoir.hiirosakura.client.gui.GuiConfig.ConfigGuiTab;

/**
 * 事件管理界面
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name EventScreen
 * <p>#create_time 2021-07-28 15:54
 */
public class EventScreen extends GuiListBase<EventSubscriberBase, WidgetEventEntry, WidgetListEvent> implements ISelectionListener<EventSubscriberBase> {

    protected final WidgetDropDownList<String> widgetDropDown;
    public static final String ALL = "all";
    private String currentEntry;

    public EventScreen() {
        super(10, 64);
        var lists = Lists.newLinkedList(HiiroSakuraEvents.events.keySet());
        lists.addFirst(ALL);
        this.setTitle(StringUtils.translate("hiirosakura.gui.title.event"));
        this.widgetDropDown = new WidgetDropDownList<>
                (0, 0, 160, 17, 200, 10,
                        lists,
                        (type) -> "§6§l§n" + type
                );
        this.widgetDropDown.setSelectedEntry(ALL);
        this.currentEntry = ALL;
    }

    @Override
    protected WidgetListEvent createListWidget(int listX, int listY) {
        return new WidgetListEvent(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), currentEntry, this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - this.getListY() - 6;
    }

    @Override
    public void initGui() {
        GuiConfig.tab = GuiConfig.ConfigGuiTab.EVENT;
        super.initGui();
        this.clearWidgets();
        this.clearButtons();
        this.createTabButtons();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (!currentEntry.equals(widgetDropDown.getSelectedEntry())) {
            currentEntry = widgetDropDown.getSelectedEntry();
            this.reCreateListWidget();
            this.initGui();
        }
    }

    protected void createTabButtons() {
        int x = 10;
        int y = 26;
        int rows = 1;

        for (GuiConfig.ConfigGuiTab tab : GuiConfig.ConfigGuiTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10) {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createTabButton(x, y, width, tab);
        }

        this.setListPosition(this.getListX(), 68 + (rows - 1) * 22);
        this.getListWidget().setSize(this.getBrowserWidth(), this.getBrowserHeight());
        this.getListWidget().initGui();

        y += 20;

        x = getListX();

        x += this.addButton(x, y);
        this.widgetDropDown.setPosition(x + 1, y + 2);
        this.addWidget(this.widgetDropDown);
    }

    protected int createTabButton(int x, int y, int width, GuiConfig.ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfig.tab != tab);
        this.addButton(button, new ButtonListenerTab(tab));
        return button.getWidth() + 2;
    }


    protected int addButton(int x, int y) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, false, "hiirosakura.gui.button.subscribe");
        this.addButton(button, (button1, mouseButton) -> {
            GuiBase.openGui(new EventEditScreen(this));
        });
        return button.getWidth();
    }


    @Override
    public void onSelectionChange(@Nullable EventSubscriberBase entry) {
        EventSubscriberBase old = HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.getSelected();
        HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.setSelected(old == entry ? null : entry);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public record ButtonListenerTab(
            ConfigGuiTab tab) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfig.tab = this.tab;
            GuiBase.openGui(new GuiConfig());
        }
    }
}
