package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable

object HSLang {

    fun lang(key: String, vararg args: Any): Text = Translatable("${HiiroSakura.MOD_ID}.$key", args = args)

    //------------ TaskManager ------------\\

    val taskUnSelected get() = lang("task.un_selected")

    val taskExecute get() = lang("task.execute")

    val taskManager get() = lang("task.manager")

    val taskName get() = lang("task.name")

    val taskDelay get() = lang("task.delay")

    val taskPeriod get() = lang("task.period")

    val taskTimes get() = lang("task.times")

    val taskExecuteOn get() = lang("task.execute_on")

    val taskExecutorType get() = lang("task.executor_type")

    val taskIcon get() = lang("task.icon")

    val taskExecutor get() = lang("task.executor")

    val taskNameEmpty get() = lang("task.name_empty")

}