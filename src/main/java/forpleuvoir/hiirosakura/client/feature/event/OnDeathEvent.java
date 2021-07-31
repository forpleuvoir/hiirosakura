package forpleuvoir.hiirosakura.client.feature.event;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端玩家死亡事件
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event
 * <p>#class_name OnDeathEvent
 * <p>#create_time 2021/7/31 22:59
 */
public class OnDeathEvent extends Event {
    public final boolean showsDeathScreen;
    @Nullable
    public final String damageSource;
    @Nullable
    public final String attacker;

    public OnDeathEvent(boolean showsDeathScreen, @Nullable String damageSource, @Nullable String attacker) {
        this.showsDeathScreen = showsDeathScreen;
        this.damageSource = damageSource;
        this.attacker = attacker;
    }
}
