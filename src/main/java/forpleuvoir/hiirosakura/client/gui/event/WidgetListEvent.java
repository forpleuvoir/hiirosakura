package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import forpleuvoir.hiirosakura.client.feature.event.base.EventBase;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name WidgetListEvent
 * <p>#create_time 2021-07-28 18:24
 */
public class WidgetListEvent extends WidgetListBase<EventBase, WidgetEventEntry> {

    public WidgetListEvent(int x, int y, int width, int height, ISelectionListener<EventBase> selectionListener) {
        super(x, y, width, height, selectionListener);
    }

    @Override
    protected WidgetEventEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, EventBase entry) {
        return new WidgetEventEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this);
    }
}
