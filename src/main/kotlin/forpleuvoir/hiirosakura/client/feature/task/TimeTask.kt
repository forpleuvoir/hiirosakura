package forpleuvoir.hiirosakura.client.feature.task

import com.google.gson.JsonObject
import forpleuvoir.hiirosakura.client.HiiroSakuraClient
import forpleuvoir.hiirosakura.client.feature.task.executor.JavaScriptExecutor
import forpleuvoir.hiirosakura.client.feature.task.executor.SimpleExecutor
import forpleuvoir.hiirosakura.client.feature.task.executor.base.IExecutor
import org.openjdk.nashorn.internal.runtime.ScriptFunction
import java.util.*

/**
 * 定时任务
 *
 * @author forpleuvoir
 *
 * #project_name hiirosakura
 *
 * #package forpleuvoir.hiirosakura.client.feature.task
 *
 * #class_name TimeTask
 *
 * #create_time 2021/7/22 22:27
 */
class TimeTask(private val executor: IExecutor, val data: TimeTaskData) {
    var counter = 0
        private set
    private var timeCounter = 0

    constructor(data: TimeTaskData, scriptFunction: ScriptFunction) : this(
        JavaScriptExecutor(scriptFunction.documentation + "\n${scriptFunction.name}();", null),
        data
    )


    @JvmField
    val hs = HiiroSakuraClient

    fun executes() {
        if (isOver || !shouldExecute()) return
        executor.execute(this)
        counter++
        timeCounter = 0
    }

    private fun shouldExecute(): Boolean {
        timeCounter++
        return if (counter == 0) {
            timeCounter >= data.startTime
        } else {
            timeCounter >= data.cyclesTime
        }
    }

    fun toJsonObject(): JsonObject {
        val json = JsonObject()
        json.addProperty("name", name)
        json.addProperty("startTime", data.startTime)
        json.addProperty("cycles", data.cycles)
        json.addProperty("cyclesTime", data.cyclesTime)
        json.addProperty("script", executorAsString)
        return json
    }

    val isOver: Boolean
        get() = counter >= data.cycles
    val name: String
        get() = data.name
    val executorAsString: String
        get() = executor.asString

    companion object {

        @JvmStatic
        fun once(scriptFunction: ScriptFunction, startTime: Int = 0): TimeTask {
            return TimeTask(TimeTaskData(UUID.randomUUID().toString(), startTime, 1, 0), scriptFunction)
        }

        @JvmStatic
        fun once(scriptFunction: ScriptFunction, name: String, startTime: Int = 0): TimeTask {
            return TimeTask(TimeTaskData(name, startTime, 1, 0), scriptFunction)
        }

        @JvmStatic
        fun once(executor: IExecutor, startTime: Int = 0): TimeTask {
            return TimeTask(executor, TimeTaskData(UUID.randomUUID().toString(), startTime, 1, 0))
        }

        @JvmStatic
        fun once(executor: IExecutor, startTime: Int = 0, name: String): TimeTask {
            return TimeTask(executor, TimeTaskData(name, startTime, 1, 0))
        }

        @JvmStatic
        fun once(startTime: Int = 0, name: String, executor: (TimeTask) -> Unit): TimeTask {
            return once(SimpleExecutor {
                executor.invoke(it)
            }, startTime, name)
        }

        @JvmStatic
        fun once(executor: (TimeTask) -> Unit, startTime: Int = 0): TimeTask {
            return once(SimpleExecutor {
                executor.invoke(it)
            }, startTime)
        }

        @JvmStatic
        fun copy(timeTask: TimeTask): TimeTask {
            return TimeTask(timeTask.executor, timeTask.data)
        }

        @JvmStatic
        fun copy(timeTask: TimeTask, data: TimeTaskData): TimeTask {
            return TimeTask(timeTask.executor, data)
        }
    }
}