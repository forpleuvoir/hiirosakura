package forpleuvoir.hiirosakura.client.feature.task.executor.base;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.TimeTaskData;
import forpleuvoir.hiirosakura.client.feature.task.executor.*;
import org.jetbrains.annotations.Nullable;

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

    public static Consumer<TimeTask> parse(String script, @Nullable Event event) {
        return new JavaScriptExecutor(script,event).getExecutor();
    }
}
