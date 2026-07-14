package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable

@Suppress("NOTHING_TO_INLINE")
object TaskLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.task.$key", args = args)

    inline val unSelected get() = lang("un_selected")

    inline val reBindKey get() = lang("re_bind_key")

    inline val reBindKeyComment get() = lang("re_bind_key.comment")

    inline val execute get() = lang("execute")

    inline val manager get() = lang("manager")

    inline fun runningPeriod(period: Int) = lang("running.period", period)

    inline fun runningRemainingTimes(times: Int) = lang("running.remaining_times", times)

    inline val editor get() = lang("editor")

    inline val name get() = lang("name")

    inline val delay get() = lang("delay")

    inline val period get() = lang("period")

    inline val times get() = lang("times")

    inline val executeOn get() = lang("execute_on")

    inline val executorType get() = lang("executor_type")

    inline val icon get() = lang("icon")

    inline val executor get() = lang("executor")

    inline val tasks get() = lang("tasks")
    inline val runningTasks get() = lang("running_tasks")
    inline val settings get() = lang("settings")
    inline val newTask get() = lang("new_task")
    inline val editTask get() = lang("edit_task")
    inline val save get() = lang("save")
    inline val cancel get() = lang("cancel")

}
