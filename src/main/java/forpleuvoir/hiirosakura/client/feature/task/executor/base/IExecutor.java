package forpleuvoir.hiirosakura.client.feature.task.executor.base;

import forpleuvoir.hiirosakura.client.feature.task.TimeTask;

/**
 * 执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name IExecutor
 * <p>#create_time 2021-07-23 16:21
 */
public interface IExecutor {
    void execute(TimeTask task);

    default String getAsString(){
        return this.toString();
    }
}
