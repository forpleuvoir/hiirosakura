package forpleuvoir.hiirosakura.client.gui.event;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.event.base.EventSubscriberBase;

/**
 * 事件编辑界面
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.event
 * <p>#class_name EventEditScreen
 * <p>#create_time 2021/7/29 1:18
 */
public class EventEditScreen extends GuiBase {
    private EventSubscriberBase subscriber = null;
    private final boolean editMode;

    public EventEditScreen(EventSubscriberBase subscriber) {
        this.subscriber = subscriber;
        this.editMode = true;
        this.title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID));
    }

    public EventEditScreen() {
        this.editMode = false;
        this.title = StringUtils.translate(String.format("%s.gui.title.event.edit", HiiroSakuraClient.MOD_ID));
    }
}
