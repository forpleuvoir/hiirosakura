package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.nebula.config.item.impl.string
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import java.util.*

object TaskManager : HiiroSakuraData {

    private val log = logger()

    object Config : ModConfigContainer("hiirosakura.data.task_manager") {

        val scriptCommonLib by string("script_common_lib", "")

        init {
            addConfig(QuickTickTaskExecuteScreen)
        }

    }

    init {
        Config.init()
    }

    override val key: String
        get() = "task_manager"

    private val tasks = mutableListOf<KeyBindTickTask>()

    val taskList: List<KeyBindTickTask> get() = tasks

    fun add(task: KeyBindTickTask) {
        tasks.add(task)
        InputHandler.register(task.keyBind)
    }

    operator fun set(index: Int, task: KeyBindTickTask) {
        tasks[index] = task
    }

    fun replace(origin: KeyBindTickTask, new: KeyBindTickTask) {
        tasks.indexOf(origin).let {
            tasks[it] = new
        }
    }

    fun remove(task: KeyBindTickTask) {
        tasks.remove(task)
        InputHandler.unregister(task.keyBind)
    }

    fun remove(index: Int) {
        tasks.removeAt(index).let { InputHandler.unregister(it.keyBind) }
    }

    fun clear() {
        tasks.forEach {
            InputHandler.unregister(it.keyBind)
        }
        tasks.clear()
    }

    fun moveUp(index: Int) {
        if (index - 1 in 0..tasks.lastIndex) {
            Collections.swap(tasks, index, index - 1)
        }
    }

    fun moveDown(index: Int) {
        if (index + 1 in 0..tasks.lastIndex) {
            Collections.swap(tasks, index, index + 1)
        }
    }

    fun reBindKey() {
        tasks.forEach {
            InputHandler.unregister(it.keyBind)
            InputHandler.register(it.keyBind)
        }
    }

    override fun serialization(): SerializeObject = serializeObject {
        "config" to Config.serialization()
        "tasks" to tasks
    }

    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.checkType {
            check<SerializeObject> { obj ->
                clear()
                runCatching {
                    Config.deserialization(obj["config"]!!)
                }.onFailure {
                    log.warn(it)
                }
                obj["tasks"]!!.asArray.forEach {
                    runCatching {
                        add(KeyBindTickTask.deserialization(it))
                    }.onFailure {
                        log.warn(it)
                    }
                }
            }
        }.getOrThrow()
    }

}