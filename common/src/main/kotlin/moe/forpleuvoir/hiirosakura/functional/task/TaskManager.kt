package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configString
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import java.util.*

object TaskManager : HiiroSakuraData {

    private val log = logger()

    object Config : ConfigGroup("hiirosakura.data.task_manager") {

        val scriptCommonLib by configString("script_common_lib", "")

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
        InputHandler.register(task.keybind)
    }

    fun remove(task: KeyBindTickTask) {
        tasks.indexOf(task)
        tasks.remove(task)
        InputHandler.unregister(task.keybind)
    }

    fun remove(index: Int) {
        tasks.removeAt(index).let { InputHandler.unregister(it.keybind) }
    }

    fun clear() {
        tasks.forEach {
            InputHandler.unregister(it.keybind)
        }
        tasks.clear()
    }

    fun moveUp(index: Int) {
        if (index - 1 in tasks.indices) {
            Collections.swap(tasks, index, index - 1)
        }
    }

    fun moveDown(index: Int) {
        if (index + 1 in tasks.indices) {
            Collections.swap(tasks, index, index + 1)
        }
    }

    fun reBindKey() {
        tasks.forEach {
            InputHandler.unregister(it.keybind)
            InputHandler.register(it.keybind)
        }
    }

    override fun serialization(): SerializeObject = SerializeObject.build {
        "config" to Config.serialization()
        "tasks" arr {
            tasks.forEach { add(it.serialization()) }
        }
    }

    override fun deserialization(data: SerializeElement) {
        data.checkType<SerializeObject, Unit> {
            clear()
            runCatching {
                Config.deserialization(it.requireKey("config"))
            }.onFailure {
                log.warn(it)
            }
            it.requireKey("tasks").checkType<SerializeArray, Unit> { array ->
                array.forEach { element ->
                    runCatching {
                        add(KeyBindTickTask.deserialization(element).getOrThrow())
                    }.onFailure { throwable ->
                        log.warn(throwable)
                    }
                }
            }
        }
    }

}