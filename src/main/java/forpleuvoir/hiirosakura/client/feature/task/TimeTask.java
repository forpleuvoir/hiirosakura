package forpleuvoir.hiirosakura.client.feature.task;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.util.function.Consumer;

/**
 * 定时任务
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task
 * <p>#class_name TimeTask
 * <p>#create_time 2021/7/22 22:27
 */
public class TimeTask {
    private final Consumer<TimeTask> task;
    public final TimeTaskData data;
    private Integer counter = 0;
    private Integer timeCounter = 0;
    public HiiroSakuraClient hs = HiiroSakuraClient.getInstance();

    public static TimeTask once(Consumer<TimeTask> task, String name) {
        return once(task, 0, name);
    }

    public static TimeTask once(Consumer<TimeTask> task, Integer startTime, String name) {
        return new TimeTask(task, new TimeTaskData(startTime, 1, 0, name));
    }

    public TimeTask(Consumer<TimeTask> task, TimeTaskData data) {
        this.task = task;
        this.data = data;
    }

    public void executes() {
        if (isOver() || !shouldExecute()) return;
        task.accept(this);
        counter++;
        timeCounter = 0;
    }

    private boolean shouldExecute() {
        timeCounter++;
        if (counter == 0) {
            return timeCounter >= data.startTime();
        } else {
            return timeCounter >= data.cyclesTime();
        }

    }

    public boolean isOver() {
        return counter >= data.cycles();
    }

    public String getName() {
        return data.name();
    }

    public Integer getCounter() {
        return counter;
    }
}
