package forpleuvoir.hiirosakura.client.feature.task.executor;

import com.google.gson.JsonObject;
import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IJavaScriptInterface;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.JavaScriptInterface;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Consumer;

/**
 * js脚本执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor
 * <p>#class_name JavaScriptExecutor
 * <p>#create_time 2021/7/27 22:34
 */
public class JavaScriptExecutor implements IExecutor {
    private transient static final HSLogger log = HSLogger.getLogger(JavaScriptExecutor.class);
    private static final String MAIN_FUNCTION_NAME = "main";
    private transient final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final IJavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private final String script;
    @Nullable
    private final Event event;

    public JavaScriptExecutor(JsonObject object, @Nullable Event event) {
        this.script = object.get("script").getAsString();
        this.event = event;
    }

    @Override
    public Consumer<TimeTask> getExecutor() {
        return timeTask -> {
            try {
                engine.eval(script);
                if (engine instanceof Invocable invocable) {
                    engine.put("$task", timeTask);
                    engine.put("$log",log);
                    engine.put("$hs", javaScriptInterface);
                    if (event != null)
                        engine.put("$event", event);
                    invocable.invokeFunction(MAIN_FUNCTION_NAME);
                }

            } catch (ScriptException e) {
                timeTask.hs.addChatMessage(new LiteralText("§c" + e.getMessage()));
            } catch (NoSuchMethodException e) {
                timeTask.hs.addChatMessage(new LiteralText("§c main function not found"));
            }
        };
    }
}
