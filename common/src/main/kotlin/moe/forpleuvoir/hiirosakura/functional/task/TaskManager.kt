package moe.forpleuvoir.hiirosakura.functional.task

import androidx.compose.runtime.mutableStateListOf
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.ui.util.Keyed
import moe.forpleuvoir.ibukigourd.ui.util.copyValue
import moe.forpleuvoir.ibukigourd.util.moveElement
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configString
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build

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

    override val key: String = "task_manager"

    private var nextKey: Long = 0

    val taskList: List<Keyed<KeybindTickTask>>
        field = mutableStateListOf()

    fun add(task: KeybindTickTask) {
        taskList.add(Keyed(nextKey++, task))
        InputHandler.register(task.keybind)
    }

    fun add(index: Int, task: Keyed<KeybindTickTask>) {
        taskList.add(index, task)
        InputHandler.register(task.value.keybind)
    }

    fun add(index: Int, task: KeybindTickTask) {
        taskList.add(index, Keyed(nextKey++, task))
        InputHandler.register(task.keybind)
    }

    fun update(index: Int, task: KeybindTickTask) {
        taskList.getOrNull(index)?.let {
            InputHandler.unregister(it.value.keybind)
            taskList[index] = it.copyValue(task)
        } ?: add(index, task)
    }

    fun moveElement(fromIndex: Int, toIndex: Int) {
        taskList.moveElement(fromIndex, toIndex)
    }

    fun removeAt(index: Int): Keyed<KeybindTickTask> =
        taskList.removeAt(index).apply { InputHandler.unregister(value.keybind) }

    fun clear() {
        taskList.forEach {
            InputHandler.unregister(it.value.keybind)
        }
        taskList.clear()
    }

    fun reBindKey() {
        taskList.forEach {
            InputHandler.unregister(it.value.keybind)
            InputHandler.register(it.value.keybind)
        }
    }

    override fun serialization(): SerializeObject = SerializeObject.build {
        "config"(Config)
        "tasks" arr {
            taskList.forEach { add(it.value) }
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
                        add(KeybindTickTask.deserialization(element).getOrThrow())
                    }.onFailure { throwable ->
                        log.warn(throwable)
                    }
                }
            }
        }
    }

}