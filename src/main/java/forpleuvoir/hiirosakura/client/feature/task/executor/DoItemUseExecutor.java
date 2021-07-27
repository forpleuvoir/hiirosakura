package forpleuvoir.hiirosakura.client.feature.task.executor;

import forpleuvoir.hiirosakura.client.feature.task.TimeTask;
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor;
import forpleuvoir.hiirosakura.client.mixin.MixinMinecraftClientInterface;

import java.util.function.Consumer;

/**
 * 物品使用执行器
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor
 * <p>#class_name DoItemUseExecutor
 * <p>#create_time 2021-07-26 16:41
 */
public class DoItemUseExecutor implements IExecutor {

    @Override
    public Consumer<TimeTask> getExecutor() {
        return timeTask ->  ((MixinMinecraftClientInterface) timeTask.hs.mc).callDoItemUse();
    }
}
