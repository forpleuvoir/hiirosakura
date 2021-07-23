package forpleuvoir.hiirosakura.client.feature.task;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.feature.task.executor.ExecutorParser;


/**
 * 定时任务解析器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task
 * <p>#class_name TimeTaskParser
 * <p>#create_time 2021-07-23 15:42
 */
public class TimeTaskParser {

    public static TimeTask parse(JsonObject object) {
        var startTime = object.get("startTime") != null ? object.get("startTime").getAsInt() : 0;
        var cycles = object.get("cycles") != null ? object.get("cycles").getAsInt() : 1;
        var cyclesTime = object.get("cyclesTime") != null ? object.get("cyclesTime").getAsInt() : 0;
        var name = object.get("name").getAsString();
        var timeTaskData = new TimeTaskData(startTime, cycles, cyclesTime, name);
        var executor = ExecutorParser.parse(object.get("executor").getAsJsonObject(), timeTaskData);
        return new TimeTask(executor, timeTaskData);
    }

}
