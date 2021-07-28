package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBase;
import forpleuvoir.hiirosakura.client.feature.event.base.HiiroSakuraEvents;

import java.util.Objects;

/**
 * 事件管理界面
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name EventScreen
 * <p>#create_time 2021-07-28 15:54
 */
public class EventScreen extends GuiListBase<EventBase, WidgetEventEntry, WidgetListEvent> implements ISelectionListener<EventBase> {
    private static String tab = "OnGameJoin";

    public EventScreen() {
        super(10, 50);
        this.setTitle(StringUtils.translate("hiirosakura.gui.title.event"));
    }

    @Override
    protected WidgetListEvent createListWidget(int listX, int listY) {
        return new WidgetListEvent(listX, listY, this.getBrowserWidth(), this.getBrowserWidth(),this);
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
        super.initGui();
        int x = 10;
        for (String name : HiiroSakuraEvents.events.keySet()) {
            x += this.createButton(x, name);
        }
    }


    private int createButton(int x, String name) {
        ButtonGeneric button = new ButtonGeneric(x, 26, -1, 20, tab);
        button.setEnabled(!tab.equals(name));
        this.addButton(button, new ButtonListener(tab, this));
        return button.getWidth() + 2;
    }

    @Override
    public void onSelectionChange(EventBase entry) {

    }

    private record ButtonListener(String tab, EventScreen parent) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            EventScreen.tab = this.tab;
            this.parent.reCreateListWidget();
            Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
