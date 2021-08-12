package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;


/**
 * 收到消息事件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnMessageEvent
 * <p>#create_time 2021/7/31 22:50
 */
public class OnMessageEvent extends Event {

    /**
     * 收到的消息文本
     */
    public final String message;
    /**
     * 消息类型
     */
    public final Integer type;

    public OnMessageEvent(String message, Integer type) {
        this.message = message.replaceAll("(§.)", "");
        this.type = type;
    }
}
