package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase;

import java.util.Collection;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name WidgetListEvent
 * <p>#create_time 2021-07-28 18:24
 */
public class WidgetListEvent extends WidgetListBase<EventSubscriberBase, WidgetEventEntry> {

    public WidgetListEvent(int x, int y, int width, int height,
                           ISelectionListener<EventSubscriberBase> selectionListener
    ) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
    }

    @Override
    protected Collection<EventSubscriberBase> getAllEntries() {
        return HiiroSakuraDatas.HIIRO_SAKURA_EVENTS.getAllEventSubscriberBase();
    }

    @Override
    protected WidgetEventEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                     EventSubscriberBase entry
    ) {
        return new WidgetEventEntry(x, y,
                                    this.browserEntryWidth,
                                    this.getBrowserEntryHeightFor(entry),
                                    isOdd,
                                    entry,
                                    listIndex,
                                    this
        );
    }
}
