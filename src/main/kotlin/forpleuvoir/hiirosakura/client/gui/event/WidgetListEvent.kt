package forpleuvoir.hiirosakura.client.gui.event

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener
import fi.dy.masa.malilib.gui.widgets.WidgetListBase
import forpleuvoir.hiirosakura.client.config.HiiroSakuraData
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase

/**
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.gui.event
 *
 * #class_name WidgetListEvent
 *
 * #create_time 2021-07-28 18:24
 */
class WidgetListEvent(
	x: Int, y: Int, width: Int, height: Int, private val eventType: String?,
	selectionListener: ISelectionListener<EventSubscriberBase?>?
) : WidgetListBase<EventSubscriberBase, WidgetEventEntry>(x, y, width, height, selectionListener) {
	override fun getAllEntries(): Collection<EventSubscriberBase> {
		return if (eventType == EventScreen.ALL) HiiroSakuraData.HIIRO_SAKURA_EVENTS.allEventSubscriberBase else HiiroSakuraData.HIIRO_SAKURA_EVENTS.getAllEventSubscriberBase(
			eventType
		)
	}

	override fun createListEntryWidget(
		x: Int, y: Int, listIndex: Int, isOdd: Boolean,
		entry: EventSubscriberBase
	): WidgetEventEntry {
		return WidgetEventEntry(
			x, y,
			browserEntryWidth,
			getBrowserEntryHeightFor(entry),
			isOdd,
			entry,
			listIndex,
			this
		)
	}

	init {
		browserEntryHeight = 22
	}
}