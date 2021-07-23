package forpleuvoir.hiirosakura.client.feature.task;

/**
 * 定时任务 参数
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task
 * <p>#class_name TimeTaskData
 * <p>#create_time 2021-07-23 16:25
 */
public record TimeTaskData(Integer startTime,Integer cycles,Integer cyclesTime,String name) {
}
