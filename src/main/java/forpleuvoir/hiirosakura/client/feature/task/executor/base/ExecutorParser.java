package forpleuvoir.hiirosakura.client.feature.task.executor.base;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;
import forpleuvoir.hiirosakura.client.feature.task.executor.JoinServerExecutor;
import forpleuvoir.hiirosakura.client.feature.task.executor.SendChatMessageExecutor;

import java.util.function.Consumer;

/**
 * 执行器解析器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name ExecutorParser
 * <p>#create_time 2021-07-23 16:14
 */
public class ExecutorParser {

    public static Consumer<HiiroSakuraClient> parse(JsonObject object, TimeTaskData data) {
        var type = ExecutorType.valueOf(object.get("type").getAsString());
        IExecutor executor = switch (type) {
            case sendChatMessage -> new SendChatMessageExecutor(object, data);
            case joinServer -> new JoinServerExecutor(object, data);
        };
        return executor.getExecutor();

    }
}
