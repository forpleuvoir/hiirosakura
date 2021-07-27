package forpleuvoir.hiirosakura.client.feature.task.executor;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;

import java.util.function.Consumer;

/**
 * 加入服务器执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor
 * <p>#class_name JoinServerExecutor
 * <p>#create_time 2021/7/25 22:05
 */
public class JoinServerExecutor implements IExecutor {
    public final String address;

    public JoinServerExecutor(JsonObject object, TimeTaskData data) {
        var address = object.get("address").getAsString();
        address = address.replace("${startTime}", data.startTime().toString())
                         .replace("${cycles}", data.cycles().toString())
                         .replace("${cyclesTime}", data.cyclesTime().toString());
        this.address = address;
    }

    @Override
    public Consumer<TimeTask> getExecutor() {
        return timeTask ->
                ConnectScreen.connect(null, timeTask.hs.mc, ServerAddress.parse(address), null);
    }
}
