package moe.forpleuvoir.hiirosakura.functional.task

import moe.forpleuvoir.hiirosakura.common.HiiroSakuraData
import moe.forpleuvoir.ibukigourd.config.ModConfigContainer
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.nebula.config.item.impl.string
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import java.util.*

object TaskManager : HiiroSakuraData {

    object Config : ModConfigContainer("task_manager") {

        val scriptCommonLib by string("script_common_lib", "")

    }

    override val key: String
        get() = "task_manager"

    private val tasks = mutableListOf<KeyBindTickTask>()

    val taskList: List<KeyBindTickTask> get() = tasks

    fun add(task: KeyBindTickTask) {
        tasks.add(task)
        InputHandler.register(task.keyBind)
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

    override fun serialization(): SerializeElement = serializeArray(tasks)

    override fun deserialization(serializeElement: SerializeElement) {
        clear()
        serializeElement.checkType {
            check<SerializeArray> { array ->
                array.forEach {
                    add(KeyBindTickTask.deserialization(it))
                }
            }
        }.getOrThrow()
    }

}