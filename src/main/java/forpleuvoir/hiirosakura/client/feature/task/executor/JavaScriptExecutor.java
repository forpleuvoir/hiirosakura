package forpleuvoir.hiirosakura.client.feature.task.executor;

import forpleuvoir.hiirosakura.client.feature.event.base.Event;
import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IJavaScriptInterface;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.JavaScriptInterface;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
    private transient final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final IJavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private final String script;
    @Nullable
    private final Event event;
    private final String include;

    public JavaScriptExecutor(String script, @Nullable Event event) {
        this.script = script;
        this.event = event;
        this.include = JSHeadFile.getContent();
    }

    @Override
    public void execute(TimeTask task) {
        try {
            engine.eval(include);
            engine.put("$task", task);
            engine.put("$log", log);
            engine.put("$hs", javaScriptInterface);
            if (event != null)
                engine.put("$event", event);
            engine.eval(script);
        } catch (Exception e) {
            task.hs.addChatMessage(new LiteralText("§c" + e.getMessage()));
            log.error(e);
        }
    }

    @Override
    public String getAsString() {
        return script;
    }

}
