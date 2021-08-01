package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import forpleuvoir.hiirosakura.client.feature.event.eventinterface.IClientPlayerInterface;
import forpleuvoir.hiirosakura.client.feature.event.eventinterface.impl.ClientPlayerInterface;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * 玩家tick事件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnPlayTickEvent
 * <p>#create_time 2021/7/31 23:11
 */
public class OnPlayerTickEvent extends Event {

    public final IClientPlayerInterface player;

    public OnPlayerTickEvent(ClientPlayerEntity player) {
        this.player = new ClientPlayerInterface(player);
    }
}
