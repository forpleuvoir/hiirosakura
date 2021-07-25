package forpleuvoir.hiirosakura.client.feature.task;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * 定时任务处理器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task
 * <p>#class_name TimeTaskHandler
 * <p>#create_time 2021/7/22 23:00
 */
public class TimeTaskHandler {
    private static final HiiroSakuraClient hs = HiiroSakuraClient.getINSTANCE();
    private static TimeTaskHandler INSTANCE;
    private final ConcurrentMap<String, TimeTask> timeTaskHashMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> removeList = new ConcurrentLinkedQueue<>();

    public static void initialize() {
        INSTANCE = new TimeTaskHandler();
        hs.addTickHandler(INSTANCE::handle);
    }

    public void addTask(String name, TimeTask task) {
        timeTaskHashMap.put(name, task);
    }

    public void addTask(TimeTask task) {
        timeTaskHashMap.put(task.getName(), task);
    }

    public void removeTask(String name) {
        removeList.add(name);
    }

    private void handle(HiiroSakuraClient client) {
        removeHandler();
        Iterator<Map.Entry<String, TimeTask>> iterator = timeTaskHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            TimeTask value = iterator.next().getValue();
            if (value.isOver()) {
                iterator.remove();
            } else {
                value.executes(client);
            }
        }
    }

    private void removeHandler() {
        removeList.forEach(timeTaskHashMap::remove);
        removeList.clear();
    }

    public static TimeTaskHandler getInstance() {
        return INSTANCE;
    }
}
