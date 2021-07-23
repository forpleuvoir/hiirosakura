package forpleuvoir.hiirosakura.client.feature.task.executor;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;

import java.util.function.Consumer;

/**
 * 消息发送执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor
 * <p>#class_name SendChatMessageExecutor
 * <p>#create_time 2021-07-23 16:22
 */
public class SendChatMessageExecutor implements IExecutor {
    private final String message;

    public SendChatMessageExecutor(JsonObject object, TimeTaskData data) {
        var message = object.get("message").getAsString();
        message = message.replace("${startTime}", data.startTime().toString())
                .replace("${cycles}", data.cycles().toString())
                .replace("${cyclesTime}", data.cyclesTime().toString());
        this.message = message;
    }

    @Override
    public Consumer<HiiroSakuraClient> getExecutor() {
        return hiiroSakuraClient -> {
            hiiroSakuraClient.sendMessage(message);
        };
    }
}
