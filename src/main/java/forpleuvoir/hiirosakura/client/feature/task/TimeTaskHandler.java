package forpleuvoir.hiirosakura.client.feature.task;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import kotlin.Unit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
	private static final HiiroSakuraClient hs = HiiroSakuraClient.INSTANCE;
	private static TimeTaskHandler INSTANCE;
	private final ConcurrentMap<String, TimeTask> timeTaskHashMap = new ConcurrentHashMap<>();
	private final ConcurrentLinkedQueue<String> removeList = new ConcurrentLinkedQueue<>();

	public static void initialize() {
		INSTANCE = new TimeTaskHandler();
		hs.addTickHandler(hiiroSakuraClient -> {
			INSTANCE.handle(hiiroSakuraClient);
			return Unit.INSTANCE;
		});
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

	public void clear() {
		timeTaskHashMap.clear();
	}

	public Set<String> getKeys() {
		Set<String> strings = new HashSet<>();
		timeTaskHashMap.keySet().forEach(s -> strings.add(String.format("\"%s\"", s)));
		return strings;
	}

	private void handle(HiiroSakuraClient client) {
		removeHandler();
		Iterator<Map.Entry<String, TimeTask>> iterator = timeTaskHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			TimeTask value = iterator.next().getValue();
			if (value.isOver()) {
				iterator.remove();
			} else {
				value.executes();
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
