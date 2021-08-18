package forpleuvoir.hiirosakura.client.feature.task

/**
 * 定时任务 参数
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task
 *
 * #class_name TimeTaskData
 *
 * #create_time 2021-07-23 16:25
 */
class TimeTaskData(val name: String, val startTime: Int = 0, val cycles: Int = 1,val cyclesTime: Int = 0)