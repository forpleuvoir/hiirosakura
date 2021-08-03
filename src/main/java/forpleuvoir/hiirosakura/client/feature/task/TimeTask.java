package forpleuvoir.hiirosakura.client.feature.task;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor;

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
    private final IExecutor executor;
    public final TimeTaskData data;
    private Integer counter = 0;
    private Integer timeCounter = 0;
    public HiiroSakuraClient hs = HiiroSakuraClient.getInstance();

    public static TimeTask once(IExecutor executor, String name) {
        return once(executor, 0, name);
    }

    public static TimeTask once(IExecutor executor, Integer startTime, String name) {
        return new TimeTask(executor, new TimeTaskData(name, startTime, 1, 0));
    }

    public TimeTask(IExecutor executor, TimeTaskData data) {
        this.executor = executor;
        this.data = data;
    }

    public void executes() {
        if (isOver() || !shouldExecute()) return;
        executor.execute(this);
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

    public String getExecutorAsString() {
        return executor.getAsString();
    }

    public Integer getCounter() {
        return counter;
    }

}
