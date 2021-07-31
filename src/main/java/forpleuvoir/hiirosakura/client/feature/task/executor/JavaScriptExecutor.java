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
    private static final String MAIN_FUNCTION_NAME = "main";
    private transient final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final IJavaScriptInterface javaScriptInterface = new JavaScriptInterface();
    private final String script;
    @Nullable
    private final Event event;
    private static final String include = """
            var $TimeTask = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTask');
            var $TimeTaskData = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskData');
            var $TimeTaskHandler = Java.type('forpleuvoir.hiirosakura.client.feature.task.TimeTaskHandler');
            function $sendMessage(message){$hs.sendChatMessage(message);}
            function $attack(){$hs.doAttack();}
            function $use(){$hs.doItemUse();}
            function $pick(){$hs.doItemPick();}
            function $sneak(tick){$hs.sneak(tick)}
            function $jump(tick){$hs.jump(tick)}
            function $move(dir,tick){
                switch(dir){
                    case 'forward':$hs.forward(tick);break;
                    case 'back':$hs.back(tick);break;
                    case 'left':$hs.left(tick);break;
                    case 'right':$hs.right(tick);break;
                }
            }
            function $joinServer(address){$hs.joinServer(address);}
            function $joinServer(address,maxConnect){$hs.joinServer(address,maxConnect);}
            """;

    public JavaScriptExecutor(String script, @Nullable Event event) {
        this.script = script;
        this.event = event;
    }

    @Override
    public Consumer<TimeTask> getExecutor() {
        return timeTask -> {
            try {
                engine.eval(include);
                engine.put("$task", timeTask);
                engine.put("$log", log);
                engine.put("$hs", javaScriptInterface);
                if (event != null)
                    engine.put("$event", event);
                engine.eval(script);
            } catch (Exception e) {
                timeTask.hs.addChatMessage(new LiteralText("§c" + e.getMessage()));
                log.error(e);
            }
        };
    }
}
