package forpleuvoir.hiirosakura.client.feature.task;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import net.minecraft.client.MinecraftClient;

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
    private final Consumer<HiiroSakuraClient> task;
    /**
     * 初次执行时间间隔 客户端tick
     */
    private final Integer startTime;
    /**
     * 循环次数
     */
    private final Integer cycles;
    /**
     * 每次循环执行的间隔
     */
    private final Integer cyclesTime;

    private Integer counter = 0;
    private Integer timeCounter = 0;

    public static TimeTask once(Consumer<HiiroSakuraClient> task) {
        return new TimeTask(task, 0, 1, 0);
    }

    public static TimeTask once(Consumer<HiiroSakuraClient> task, Integer startTime) {
        return new TimeTask(task, startTime, 1, 0);
    }

    public TimeTask(Consumer<HiiroSakuraClient> task, Integer cycles,
                    Integer cyclesTime
    ) {
        this(task, 0, cycles, cyclesTime);
    }

    public TimeTask(Consumer<HiiroSakuraClient> task, Integer startTime, Integer cycles,
                    Integer cyclesTime
    ) {
        this.task = task;
        this.startTime = startTime;
        this.cycles = cycles;
        this.cyclesTime = cyclesTime;
    }

    public void executes(HiiroSakuraClient client) {
        if (isOver() || !shouldExecute()) return;
        task.accept(client);
        counter++;
        timeCounter = 0;
    }

    private boolean shouldExecute() {
        timeCounter++;
        if (counter == 0) {
            return timeCounter >= startTime;
        } else {
            return timeCounter >= cyclesTime;
        }

    }

    public boolean isOver() {
        return counter >= cycles;
    }
}
