package forpleuvoir.hiirosakura.client.feature.task.executor.base;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;

import java.util.function.Consumer;

/**
 * 执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name IExecutor
 * <p>#create_time 2021-07-23 16:21
 */
@FunctionalInterface
public interface IExecutor {
    Consumer<HiiroSakuraClient> getExecutor();
}
