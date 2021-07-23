package forpleuvoir.hiirosakura.client.feature.event.base;

import com.google.common.collect.ImmutableMap;
import forpleuvoir.hiirosakura.client.feature.event.OnGameJoinEvent;

import java.util.Map;

/**
 * MOD事件集合
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.base
 * <p>#class_name HiiroSakuraEvents
 * <p>#create_time 2021-07-23 13:44
 */
public class HiiroSakuraEvents {
    public static final Map<String, Class<? extends Event>> events = ImmutableMap.of(
            "OnGameJoin", OnGameJoinEvent.class
    );

}
